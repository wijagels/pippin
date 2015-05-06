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
        // two more lines needed
        allowsImmediate.add("LOD");
        // seven more lines needed
        allowsIndirect.add("LOD");
        // five more lines needed
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
            throw IllegalArgumentException("errors is null.");
        }

        ArrayList<String> inputText = new ArrayList<>();
        
        try (Scanner inp = new Scanner(input)){
            int i=0;
            while(inp.hasNextLine()){
                inputText.add(inp);
                if (inputText.get(i).trim().length() > 0){ //if nonblank line
                    if (inputText.get(i)[0] == ' ' || inputText.get(i)[0] == '\t'){
                        errors.put(i+1, "Error on line " + (i+1) + ": starts with white space");
                    }
                }
                i++;
            }
        }catch (FileNotFoundException e){
            errors.put(0, "Error: Unable to open the input file");
        }

        int blankLnNum = null;

        for(int i=0; i<inputText.size(); i++){
            if (inputText.get(i).trim().length()==0 && blankLnNum==null){
                blankLnNum = i;
            }
            if (blankLnNum!=null && inputText.get(i).trim().length()>0){
                errors.put(blankLnNum, "Error on line " + blankLnNum + ": illegal blank line");
                blankLnNum=null;
            }
        }

        ArrayList<String> inCode = new ArrayList<>();
        ArrayList<String> inData = new ArrayList<>();

        String pushTo = "inCode";

        for (int i=0; i<inputText.size(); i++){
            if (!(inputText.get(i).equalsIgnoreCase("DATA")) && pushTo=="inCode"){
                inCode.add(inputText.get(i).trim());
            }else if (inputText.get(i).equalsIgnoreCase("DATA")){
                if (!(inputText.get(i).equals("DATA"))){
                    errors.put(i, "Error on line " + i + ": 'DATA' is not all uppercase.");
                }
                // but dont add it to either arraylist
            }else{
                inData.add(inputText.get(i).trim());
            }
        }

        ArrayList<String> outCode = new ArrayList<>();

        for (int i=0; i<inCode.size(); i++){
            String[] parts = inCode.get(i).split("\\s+");
            if(!InstructionMap.opcode.containsKey(parts[0].toUpperCase())){
                errors.put(i+1, "Error on line " + (i+1) + ": illegal mnemonic.");
            }else if (InstructionMap.opcode.containsKey(parts[0])){
                if (noArgument.conatins(parts[0]) && parts.length > 1){
                    errors.put(i+1, "Error on line " + (i+1) + ": mnemonic does not take arguments.");
                }else if (noArgument.conatins(parts[0]) && parts.length == 1){
                    outCode.add(Integer.toHexString(InstructionMap.opcode.get(parts[0])) + " 0 0");
                }else{
                    if (!(noArgument.conatins(parts[0])) && parts.length > 2){
                        errors.put(i+1, "Error on line " + (i+1) + ": mnemonic has too many arguments.");
                    } else {
                        if (parts[1].length>=3 && parts[1].substring(0).equals('[') && parts[1].substring(1).equals('[')){
                            try{
                                int arg = Integer.parseInt(parts[1].substring(2),16); 
                                outCode.add(Integer.toHexString(InstructionMap.opcode.get(parts[0])) + " " + Integer.toHexString(arg).toUpperCase() + " 2"); 
                            }catch(NumberFormatException e){
                                errors.put(i+1, "Error on line "+(i+1)+ ": indirect argument is not a hex number.");
                            }
                        }else if (parts[1].length>=2 && parts[1].substring(0).equals('[')){
                            try{
                                int arg = Integer.parseInt(parts[1].substring(1),16); 
                                outCode.add(Integer.toHexString(InstructionMap.opcode.get(parts[0])) + " " + Integer.toHexString(arg).toUpperCase() + " 1"); 
                            }catch(NumberFormatException e){
                                errors.put(i+1, "Error on line "+(i+1)+ ": direct argument is not a hex number.");
                            }
                        }else if (parts[1].length>=1 && !(parts[1].substring(0).equals('['))){
                            if (allowsImmediate.contains(parts[0])){
                                try{
                                    int arg = Integer.parseInt(parts[1].substring(2),16); 
                                    outCode.add(Integer.toHexString(InstructionMap.opcode.get(parts[0])) + " " + Integer.toHexString(arg).toUpperCase() + " 0"); 
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

        for (int i; i<inData.size(); i++){
            String[] parts = inData.get(i).split("\\s+");
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
                outData.add(Integer.toHexString(addr).toUpperCase() + " " + Integer.toHexString(val).toUpperCase());
            }
        }

        if(errors.size() == 0) {
            try (PrintWriter outp = new PrintWriter(output)){
                for(String str : outCode) outp.println(str);
                outp.println(-1); // the separator where the source has “DATA” for(String str : outData) outp.println(str);
            }catch (FileNotFoundException e){
                errors.put(0, "Error: Unable to write the assembled program to the output file");
            }
        }
        return errors.size() == 0; // TRUE means there were no errors
    }
}
