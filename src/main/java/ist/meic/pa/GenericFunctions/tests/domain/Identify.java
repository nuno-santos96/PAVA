package ist.meic.pa.GenericFunctions.tests.domain;

import ist.meic.pa.GenericFunctions.AfterMethod;
import ist.meic.pa.GenericFunctions.BeforeMethod;
import ist.meic.pa.GenericFunctions.GenericFunction;

import java.awt.print.Book;
import java.io.Reader;

@GenericFunction
public class Identify {

    public static String it(Object o) { return "Object"; }
    public static String it(String s) { return "String"; }
    private static String it(Integer a) { return "Integer"; }
    public static String it(Object[] arr) {
        String res = "";
        for (Object o : arr)
            res += it(o);
        return res;
    }
}
