package pippin;

import java.util.ArrayList;


public class Code {
    public static final int CODE_MAX = 256;
    private ArrayList<IntTriple> program = new ArrayList<IntTriple>();

    class IntTriple {
        int op, arg, indirectionLevel;

        public IntTriple(int op, int arg, int indirectionLevel) {
            this.op = op;
            this.arg = arg;
            this.indirectionLevel = indirectionLevel;
        }
    }

    public String getCodeText(int i) {
        StringBuilder builder = new StringBuilder();
        if(i < program.size()) {
            builder.append(InstructionMap.mnemonics.get(program.get(i).op));
            builder.append(' ');
            for(int j = 0; j < program.get(i).indirectionLevel; j++) {
                builder.append('[');
            }
            builder.append(program.get(i).arg);
        }
        return builder.toString();
    }

    public int getOp(int i) {
        return this.program.get(i).op;
    }

    public int getArg(int i) {
        return this.program.get(i).arg;
    }

    public int getIndirectionLevel(int i) {
        return this.program.get(i).indirectionLevel;
    }

    public void clear() {
        this.program.clear();
    }

    public void setCode(int op, int arg, int level) {
        this.program.add(new IntTriple(op, arg, level));
    }

}
