package pippin;

import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Assembler {
    public static void assemble(String filename) {
        File f = new File("out.pexe");
        try {
            f.createNewFile();
        }
        catch(IOException e) {
            System.out.println("Omgwtf can't make a file");
        }
        try(Scanner sc = new Scanner(new File(filename));
                PrintWriter pw = new PrintWriter(f)) {
            while(sc.hasNextLine()) {
                Scanner parser = new Scanner(sc.nextLine());
                Integer opcode = InstructionMap.opcode.get(parser.next());
                pw.println(opcode);
            }
        }
        catch(FileNotFoundException e) {
            System.out.println("File wasn't found");
        }
    }
}
