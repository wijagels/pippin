package pippin;

import java.io.File;
import java.io.FileNotFoundException;

public class Runner {

    /*public static void main(String[] args) {
      MachineModel model = new MachineModel();
      class Triple {
      int opcode;
      int arg;
      int level;
      public Triple(int opcode, int arg, int level) {
      super();
      this.opcode = opcode;
      this.arg = arg;
      this.level = level;
      }
      public String toString() {
      return opcode + " " + arg + " " + level;
      }
      }
      Triple[] programCode = {
      new Triple(1, 0, 1),
      new Triple(3, 1, 1),
      new Triple(2, 2, 1),
      };

      model.setData(0, 8);
      model.setData(1, 7);

      for(Triple t : programCode) {
      System.out.println(t);
      Instruction instr = model.INSTRUCTION_MAP.get(t.opcode);
      instr.execute(t.arg, t.level);
      System.out.println("memory[0] = " + model.getData(0));
      System.out.println("memory[1] = " + model.getData(1));
      System.out.println("memory[2] = " + model.getData(2));
      }
      }*/
    public static void main(String[] args) throws FileNotFoundException {
        MachineModel model = new MachineModel();
        Code program1 = new Code();
        Loader.load(model, program1, new File("test1.pexe"));
        System.out.println("program1");
        int pc = 0;
        while(pc < 3) {
            System.out.println(program1.getCodeText(pc));
            int op = program1.getOp(pc);
            int arg = program1.getArg(pc);
            int lev = program1.getIndirectionLevel(pc);
            Instruction instr = model.INSTRUCTION_MAP.get(op);
            instr.execute(arg, lev);
            pc = model.getProgramCounter();
            System.out.println("memory[0] = " + model.getData(0));
            System.out.println("memory[1] = " + model.getData(1));
            System.out.println("memory[2] = " + model.getData(2));
        }
        System.out.println("======================");
        System.out.println("program2");
        pc = 0;
        model.clearMemory();
        model.setAccumulator(0);
        model.setProgramCounter(0);
        Code program2 = new Code();
        Loader.load(model, program2, new File("test2.pexe"));
        while (true){
            pc = model.getProgramCounter();
            System.out.println(program2.getCodeText(pc));
            int op = program2.getOp(pc);
            int arg = program2.getArg(pc);
            int lev = program2.getIndirectionLevel(pc);
            Instruction instr = model.INSTRUCTION_MAP.get(op);
            instr.execute(arg, lev);
            System.out.println("memory[0] = " + model.getData(0));
            System.out.println("memory[1] = " + model.getData(1));
            System.out.println("acc = " + model.getAccumulator());
        }
    }
}
