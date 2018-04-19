package ist.meic.pa.GenericFunctions.tests.domain;

import ist.meic.pa.GenericFunctions.AfterMethod;
import ist.meic.pa.GenericFunctions.BeforeMethod;
import ist.meic.pa.GenericFunctions.GenericFunction;

@GenericFunction
public interface Explain {
    public static void it(Integer i) {
        System.out.print(i + " is an integer");
    }
    public static void it(Double i) {
        System.out.print(i + " is a double");
    }
    public static void it(String s) {
        System.out.print(s + " is a string");
    }

    @BeforeMethod
    public static void it(Number n) {
        System.out.print("The number");
    }

    @AfterMethod
    public static void it(Object o) {
        System.out.println(".");
    }
}
