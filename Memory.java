package pippin;

public class Memory {
    public static final int DATA_SIZE = 512;
    private int[] data = new int[DATA_SIZE];
    private int changedIndex = -1;

    public int getChangedIndex() {
        return this.changedIndex;
    }

    public int getData(int index) {
        return this.data[index];
    }

    public void setData(int index, int value) {
        this.changedIndex = index;
        this.data[index] = value;
    }

    protected int[] getData() {
        return this.data;
    }

    public void clear() {
        for(int i=0;i<data.length;i++)
            data[i] = 0;
        this.changedIndex = -1;
    }
}
