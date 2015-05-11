package pippin;

import java.util.Map;
import java.util.TreeMap;
import java.io.File;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AssemblerTester{

    @Test
    public void testAssembler(){
        File fileOut = new File("./assembly/out.pexe");
        /**http://www.javaprogrammingforums.com/java-programming-tutorials/3-java-program-can-list-all-files-given-directory.html*/
        // Directory path here
        String path = "./assembly";

        String files;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (File f : listOfFiles)
        {
            if (f.isFile()) {
                if (f.getName().endsWith(".pasm")) {
                    Map<Integer, String> errors = new TreeMap<>();
                    System.out.println(f.getName());
                    Assembler.assemble(f, fileOut, errors);
                    System.out.println(errors);
                    if(f.getName().contains("e.pasm") && f.getName() != "merge.pasm")
                        assertTrue(f.getName(), errors.size() != 0);
                    else
                        assertTrue(f.getName(), errors.size() == 0);
                }
            }
        }
    }
}
