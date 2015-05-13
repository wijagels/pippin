package pippin;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Assembler {

    /**
     * lists the mnemonics of the instructions that do not have arguments
     */
    public static Set<String> noArgument = new TreeSet<String>();
    /**
     * lists the mnemonics of the instructions that allow immediate addressing
     */
    public static Set<String> allowsImmediate = new TreeSet<String>();
    /**
     * lists the mnemonics of the instructions that allow indirect addressing
     */
    public static Set<String> allowsIndirect = new TreeSet<String>();

    static {
        noArgument.add("HALT");
        noArgument.add("NOP");
        noArgument.add("NOT");
        allowsImmediate.add("LOD");
        allowsImmediate.add("STO");
        allowsImmediate.add("AND");
        allowsImmediate.add("ADD");
        allowsImmediate.add("SUB");
        allowsImmediate.add("MUL");
        allowsImmediate.add("DIV");
        allowsImmediate.add("JUMP");
        allowsImmediate.add("JMPZ");
        allowsImmediate.add("ROT");
        allowsIndirect.add("LOD");
        allowsIndirect.add("STO");
        allowsIndirect.add("ADD");
        allowsIndirect.add("SUB");
        allowsIndirect.add("MUL");
        allowsIndirect.add("DIV");
    }

    /**
     * Method to assemble a file to its binary representation. If the input has errors
     * a list of errors will be written to the errors map. If there are errors,
     * they appear as a map with the line number as the key and the description of the error
     * as the value. If the input or output cannot be opened, the "line number" key is 0.
     * @param input the source assembly language file
     * @param output the binary version of the program if the souce program is
     * correctly formatted
     * @param errors the errors map
     * @return
     */
    public static boolean assemble(File input, File output, Map<Integer, String> errors) {
        if (errors==null){
            throw new IllegalArgumentException("errors is null.");
        }

        ArrayList<String> inputText = new ArrayList<>();

        try (Scanner inp = new Scanner(input)){
            boolean isB = false;
            boolean blankRecorded = false;
            int i=0;
            while(inp.hasNextLine()){
                String text = inp.nextLine();
                if (text.trim().length() > 0){ //if nonblank line
                    if (text.charAt(0) == ' ' || text.charAt(0) == '\t'){
                        errors.put(i+1, "Error on line " + (i+1) + ": starts with white space");
                    }
                    if(isB && blankRecorded==false){
                        errors.put(i, "Error on line " + (i) + ": blank line");
                        blankRecorded = true;
                    }else{
                        inputText.add(text);
                    }
                }
                else
                    isB = true;
                i++;
            }
        }catch (FileNotFoundException e){
            errors.put(0, "Error: Unable to open the input file");
        }

        int blankLnNum = -1;

        for(int i=0; i<inputText.size(); i++){
            if (inputText.get(i).trim().length()==0 && blankLnNum==-1){
                blankLnNum = i;
            }
            if (blankLnNum!=-1 && inputText.get(i).trim().length()>0){
                errors.put(blankLnNum, "Error on line " + blankLnNum + ": illegal blank line");
                blankLnNum=-1;
            }
        }

        ArrayList<String> inCode = new ArrayList<>();
        ArrayList<String> inData = new ArrayList<>();

        String pushTo = "inCode";

        for (int i=0; i<inputText.size(); i++){
            if (!(inputText.get(i).trim().equalsIgnoreCase("DATA")) && pushTo=="inCode"){
                inCode.add(inputText.get(i).trim());
            }else if (inputText.get(i).trim().equalsIgnoreCase("DATA")){
                if (!(inputText.get(i).trim().equals("DATA"))){
                    errors.put(i, "Error on line " + i + ": 'DATA' is not all uppercase.");
                }
                pushTo = "inData";
                // but dont add it to either arraylist
            }else{
                inData.add(inputText.get(i).trim());
            }
        }

        ArrayList<String> outCode = new ArrayList<>();

        for (int i=0; i<inCode.size(); i++){
            String[] parts = inCode.get(i).split("\\s+");
            if(!InstructionMap.opcode.containsKey(parts[0].toUpperCase())){
                errors.put(i+1, "Error on line " + (i+1) + ": illegal mnemonic. " + parts[0]);
            }else if (InstructionMap.opcode.containsKey(parts[0])){
                if (noArgument.contains(parts[0]) && parts.length > 1){
                    errors.put(i+1, "Error on line " + (i+1) + ": mnemonic does not take arguments.");
                }else if (noArgument.contains(parts[0]) && parts.length == 1){
                    outCode.add(Integer.toString(InstructionMap.opcode.get(parts[0]), 16) + " 0 0");
                }else{
                    if (!(noArgument.contains(parts[0])) && parts.length > 2){
                        errors.put(i+1, "Error on line " + (i+1) + ": mnemonic has too many arguments.");
                    } else {
                        if(parts.length <= 1)
                            errors.put(i+1, "Error on line " + (i+1) + ": not enough arguments");
                        else if (parts[1].length()>=3 && parts[1].charAt(0) == '[' && parts[1].charAt(1) == '[' ){
                            try{
                                int arg = Integer.parseInt(parts[1].substring(2),16);
                                outCode.add(Integer.toString(InstructionMap.opcode.get(parts[0]), 16) + " " + Integer.toString(arg, 16).toUpperCase() + " 2");
                            }catch(NumberFormatException e){
                                errors.put(i+1, "Error on line "+(i+1)+ ": indirect argument is not a hex number.");
                            }
                        }else if (parts[1].length()>=2 && parts[1].charAt(0) == '[' ){
                            try{
                                int arg = Integer.parseInt(parts[1].substring(1),16);
                                outCode.add(Integer.toString(InstructionMap.opcode.get(parts[0]), 16) + " " + Integer.toString(arg, 16).toUpperCase() + " 1");
                            }catch(NumberFormatException e){
                                errors.put(i+1, "Error on line "+(i+1)+ ": direct argument is not a hex number.");
                            }
                        }else if (parts[1].length()>=1 && !(parts[1].charAt(0) == '[' )){
                            if (allowsImmediate.contains(parts[0])){
                                try{
                                    int arg = Integer.parseInt(parts[1].substring(0),16);
                                    outCode.add(Integer.toString(InstructionMap.opcode.get(parts[0]), 16) + " " + Integer.toString(arg, 16).toUpperCase() + " 0");
                                }catch(NumberFormatException e){
                                    errors.put(i+1, "Error on line "+(i+1)+ ": immediate argument is not a hex number.");
                                }
                            }else{
                                errors.put(i+1, "Error on line "+(i+1)+ ": argument is not allowed to occur as immediate.");
                            }
                        }
                    }
                }
            }else{
                errors.put(i+1, "Error on line " + (i+1) + ": mnemonics must be in uppercase.");
            }
        }

        int offset = inCode.size()+1;

        ArrayList<String> outData = new ArrayList<>();

        for (int i=0; i<inData.size(); i++){
            String[] parts = inData.get(i).trim().split("\\s+");
            if (parts.length!=2){
                errors.put(i+offset, "Error on line "+(i+offset)+ ": this is not an address/value pair.");
            }else{
                int addr = -1;
                int val = -1;
                try{
                    addr = Integer.parseInt(parts[0],16);
                }catch(NumberFormatException e){
                    errors.put(i+offset, "Error on line "+(i+offset)+ ": address is not a hex number.");
                }
                try{
                    val = Integer.parseInt(parts[1],16);
                }catch(NumberFormatException e){
                    errors.put(i+offset, "Error on line "+(i+offset)+ ": value is not a hex number.");
                }
                outData.add(Integer.toString(addr, 16).toUpperCase() + " " + Integer.toString(val, 16).toUpperCase());
            }
        }

        if(errors.size() == 0) {
            try (PrintWriter outp = new PrintWriter(output)){
                for(String str : outCode) outp.println(str);
                outp.println(-1); // the separator where the source has “DATA”
                for(String str : outData) outp.println(str);
            }catch (FileNotFoundException e){
                errors.put(0, "Error: Unable to write the assembled program to the output file");
            }
        }
        return errors.size() == 0; // TRUE means there were no errors
    }
}
