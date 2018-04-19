package ist.meic.pa.GenericFunctions.tests.domain;

import ist.meic.pa.GenericFunctions.GenericFunction;

import java.util.Vector;

@GenericFunction
public interface Com {
    // Test B and D
    public static Object bine(Object a) { return "Object";}
    public static String bine(String a) { return a; }
    public static Integer bine(Integer a) { return a; }
    public static Object bine(Object[] arr) {
        String res = "";
        for (Object o : arr) res += bine(o);
        return res;
    }
    public static Object bine(Integer[] arr) {
        int res = 0;
        for (int o : arr) res += bine(o);
        return res;
    }

    // Test 17
    public static Object bine(Object a, Object b) {
        Vector<Object> v = new Vector<>();
        v.add(a); v.add(b);
        return v;
    }
    public static Integer bine(Integer a, Integer b) {
        return a+b;
    }
    public static Object bine(String a, Object b) {
        return a + ", " + b + "!";
    }
    public static Object bine(String a, Integer b) {
        return (b==0) ? "" : a + bine(a, b-1);
    }
}
