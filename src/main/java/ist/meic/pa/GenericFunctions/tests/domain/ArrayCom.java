package ist.meic.pa.GenericFunctions.tests.domain;

import ist.meic.pa.GenericFunctions.GenericFunction;

import java.lang.reflect.Array;
import java.util.*;


@GenericFunction
public class ArrayCom {

    public static Object bine(Object a, Object b) {
        return new Object[] { a, b };
    }
    public static Object bine(Object[] a, Object[] b) {
        Object[] r = new Object[a.length];
        for (int i = 0; i < a.length; i++) {
            r[i] = ArrayCom.bine(a[i], b[i]);
        }
        return r;
    }
    private static Object bine(String a, String b) {
        System.out.println(a+b); return a + b;
    }
    private static Object bine(String a, Float b) { return a + "-(float): " + b; }
    public static Integer bine(Integer a, Integer b) {
        return a + b;
    }

    // Test K
    public static Object bine(List<Object> a, List<Object> b) {
        System.out.println("List, List");
        Object[][] r = new Object[a.size()][b.size()];
        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < b.size(); j++)
                r[i][j] = ArrayCom.bine(a.get(i), b.get(j));
        }
        return Arrays.deepToString(r);
    }
    public static Object bine(ArrayList<Object> a, ArrayList<Object> b) {
        System.out.println("ArrayList");
        Object[][] r = new Object[a.size()][b.size()];
        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < b.size(); j++)
                r[i][j] = ArrayCom.bine(a.get(i), b.get(j));
        }
        return Arrays.deepToString(r);
    }

}
