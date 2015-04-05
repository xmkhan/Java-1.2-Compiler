package java.lang;
public class Object {
    public Object() {
    }
    public boolean equals(Object other) {
        return this == other;
    }
    public int hashCode() {
        return 42;
    }
    protected Object clone() {
        return this;
    }
    public static int test() {
        return 2 + 2;
    }
}
