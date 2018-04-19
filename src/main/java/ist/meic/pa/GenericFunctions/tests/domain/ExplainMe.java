package ist.meic.pa.GenericFunctions.tests.domain;

import ist.meic.pa.GenericFunctions.AfterMethod;
import ist.meic.pa.GenericFunctions.BeforeMethod;
import ist.meic.pa.GenericFunctions.GenericFunction;

@GenericFunction
public class ExplainMe {
    @AfterMethod
    public static void twoThings(Number o1, Number o2) {
        System.out.println("Sniff, Sniff! Why am I the last? I'm more specific than Obj-Obj!");
    }
    @AfterMethod
    public static void twoThings(Object o1, Object o2) {
        System.out.println("Muahaha! I knew I would run after the primary!");
    }

    public static void twoThings(Number o1, Integer o2) {
        System.out.printf("Woho!! I'm the primary!\n");

    }

    @BeforeMethod
    public static void twoThings(Integer o1, Number o2) {
        System.out.println("How come Integer-Integer is more specific than me?");
    }
    @BeforeMethod
    public static void twoThings(Integer o1, Integer o2) {
        System.out.println("Let me be the first!");

    }
}
