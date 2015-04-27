package pippin;

import java.util.Observable;
import java.util.Map;
import java.util.TreeMap;


public class MachineModel extends Observable{
    public final Map<Integer, Instruction> INSTRUCTION_MAP = new TreeMap<Integer, Instruction>();
    private Registers cpu = new Registers();
    private Memory memory = new Memory();
    private boolean withGUI;
    private Code code;

    public MachineModel() {
        this(false);
    }

    public MachineModel(boolean withGUI) {
        this.withGUI = withGUI;
        populate(); //I like concise constructors
    }

    public Code getCode() {
        return this.code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public int getData(int index) {
        return memory.getData(index);
    }
    public void setData(int index, int value) {
        memory.setData(index, value);
    }
    public Instruction get(Integer key) {
        return INSTRUCTION_MAP.get(key);
    }
    int[] getData() { // only need for JUnit tester
        return memory.getData();
    }
    public int getProgramCounter() {
        return cpu.programCounter;
    }
    public int getAccumulator() {
        return cpu.accumulator;
    }
    public void setAccumulator(int i) {
        cpu.accumulator = i;
    }

    public void setProgramCounter(int i) {
        cpu.programCounter = i;
    }

    public int getChangedIndex() {
        return this.memory.getChangedIndex();
    }

    public void step() {
        //TODO implement
    }

    public void clear() {
        //TODO implement
    }


    public void halt() {
        if(withGUI) {
            // the code needed here will come later
        } else {
            System.exit(0);
        }
    }

    public void clearMemory() {
        memory.clear();
    }

    class Registers {
        private int accumulator, programCounter;
    }

    private void populate() {

        //INSTRUCTION_MAP entry for "NOP"
        INSTRUCTION_MAP.put(0x0,(arg,level) -> {
            if(level != 0)
                throw new IllegalArgumentException(
                        "Ay, level isn't zero!");
            cpu.programCounter++;
        });

        //INSTRUCTION_MAP entry for "LOD"
        INSTRUCTION_MAP.put(0x1,(arg,level) -> {
            if(level < 0 || level > 2) {
                throw new IllegalArgumentException(
                        "Illegal indirection level in LOD instruction");
            }
            if(level == 0) {
                this.cpu.accumulator = arg;
                this.cpu.programCounter++;
            }
            else
                INSTRUCTION_MAP.get(0x1).execute(memory.getData(arg), level-1);
        });

        //INSTRUCTION_MAP entry for "STO"
        INSTRUCTION_MAP.put(0x2,(arg,level) -> {
            if(level != 1 && level != 2)
                throw new IllegalArgumentException(
                        "Level must be 1 or 2 for STO instruction");
            if(level == 1) {
                memory.setData(arg, cpu.accumulator);
                this.cpu.programCounter++;
            }
            else
                INSTRUCTION_MAP.get(0x2).execute(memory.getData(arg), level-1);
        });

        //INSTRUCTION_MAP entry for "ADD"
        INSTRUCTION_MAP.put(0x3,(arg, level) -> {
            if(level < 0 || level > 2) {
                throw new IllegalArgumentException(
                        "Illegal indirection level in ADD instruction");
            }
            if (level > 0) {
                INSTRUCTION_MAP.get(0x3).execute(memory.getData(arg), level-1);
            } else {
                cpu.accumulator += arg;
                cpu.programCounter ++;
            }
        });

        //INSTRUCTION_MAP entry for "SUB"
        INSTRUCTION_MAP.put(0x4,(arg,level) -> {
            if(level < 0 || level > 2) {
                throw new IllegalArgumentException(
                        "Illegal indirection level in SUB instruction");
            }
            if (level > 0) {
                INSTRUCTION_MAP.get(0x4).execute(memory.getData(arg), level-1);
            } else {
                cpu.accumulator -= arg;
                cpu.programCounter ++;
            }
        });

        //INSTRUCTION_MAP entry for "MUL"
        INSTRUCTION_MAP.put(0x5,(arg,level) -> {
            if(level < 0 || level > 2) {
                throw new IllegalArgumentException(
                        "Illegal indirection level in MUL instruction");
            }
            if (level > 0) {
                INSTRUCTION_MAP.get(0x5).execute(memory.getData(arg), level-1);
            } else {
                cpu.accumulator *= arg;
                cpu.programCounter ++;
            }
        });

        //INSTRUCTION_MAP entry for "DIV"
        INSTRUCTION_MAP.put(0x6,(arg,level) -> {
            if(level < 0 || level > 2) {
                throw new IllegalArgumentException(
                        "Illegal indirection level in DIV instruction");
            }
            if (level > 0) {
                INSTRUCTION_MAP.get(0x6).execute(memory.getData(arg), level-1);
            } else {
                cpu.accumulator /= arg;
                cpu.programCounter ++;
            }
        });

        //INSTRUCTION_MAP entry for "AND"
        INSTRUCTION_MAP.put(0x7,(arg,level) -> {
            if(level != 1 && level != 0)
                throw new IllegalArgumentException(
                        "Illegal indirection level in AND instruction");
            if(level == 0) {
                cpu.accumulator = cpu.accumulator != 0 && arg != 0 ? 1 : 0;
                cpu.programCounter++;
            }
            else
                INSTRUCTION_MAP.get(0x7).execute(memory.getData(arg), level-1);
        });

        //INSTRUCTION_MAP entry for "NOT"
        INSTRUCTION_MAP.put(0x8,(arg,level) -> {
            if(level != 0)
                throw new IllegalArgumentException(
                        "Illegal indirection level in NOT instruction");
            cpu.accumulator = cpu.accumulator == 0 ? 1 : 0;
            cpu.programCounter++;
        });

        //INSTRUCTION_MAP entry for "CMPZ"
        INSTRUCTION_MAP.put(0x9,(arg,level) -> {
            if(level != 1)
                throw new IllegalArgumentException(
                        "Illegal indirection level in CMPZ instruction");
            cpu.accumulator = memory.getData(arg) == 0 ? 1 : 0;
            cpu.programCounter++;
        });

        //INSTRUCTION_MAP entry for "CMPL"
        INSTRUCTION_MAP.put(0xA,(arg,level) -> {
            if(level != 1)
                throw new IllegalArgumentException(
                        "Illegal indirection level in CMPL instruction");
            cpu.accumulator = memory.getData(arg) < 0 ? 1 : 0;
            cpu.programCounter++;
        });

        //INSTRUCTION_MAP entry for "JUMP"
        INSTRUCTION_MAP.put(0xB,(arg,level) -> {
            if(level != 0 && level != 1)
                throw new IllegalArgumentException(
                        "Illegal indirection level in JUMP instruction");
            if(level == 0)
                cpu.programCounter = arg;
            else
                INSTRUCTION_MAP.get(0xB).execute(memory.getData(arg), level-1);
        });

        //INSTRUCTION_MAP entry for "JMPZ"
        INSTRUCTION_MAP.put(0xC,(arg,level) -> {
            if(level != 0 && level != 1)
                throw new IllegalArgumentException(
                        "Illegal indirection level in JMPZ instruction");
            if(level == 0) {
                if(cpu.accumulator == 0)
                    cpu.programCounter = arg;
                else
                    cpu.programCounter++;
            }
            else
                INSTRUCTION_MAP.get(0xC).execute(memory.getData(arg), level-1);
        });

        //INSTRUCTION_MAP entry for "HALT"
        INSTRUCTION_MAP.put(0xF,(arg,level) -> {
            halt();
        });
    }
}
