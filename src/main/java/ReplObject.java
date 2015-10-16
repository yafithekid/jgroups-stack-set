import java.io.Serializable;

public class ReplObject<T> implements Serializable{
    public static final int STACK_PUSH = 1;
    public static final int STACK_POP = 2;
    public static final int STACK_TOP = 3;

    public static final int SET_ADD = 4;
    public static final int SET_CONTAINS = 5;
    public static final int SET_REMOVE = 6;

    int type;
    T value;

    public ReplObject(int type){
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }
}
