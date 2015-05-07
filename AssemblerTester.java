package pippin;

import java.util.Map;
import java.util.TreeMap;
import java.io.File;

public class AssemblerTester{
	public static void main(String[] args){
		Map<Integer, String> errors = new TreeMap<>();
		File fileIn = new File("assemblertester.pexe");
		File fileOut = new File("out.pexe");
		Assembler.assemble(fileIn, fileOut, errors);
	}
}
