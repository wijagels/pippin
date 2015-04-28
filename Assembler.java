package pippin;

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
        try(Scanner sc = new Scanner(input);
                PrintWriter pw = new PrintWriter(output)) {
            while(sc.hasNextLine()) {
                Scanner parser = new Scanner(sc.nextLine());
                Integer opcode = InstructionMap.opcode.get(parser.next());
                pw.println(opcode);
            }
            return true;
        }
        catch(FileNotFoundException e) {
            return false;
        }
    }
}
