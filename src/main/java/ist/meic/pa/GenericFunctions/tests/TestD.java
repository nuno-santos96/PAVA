package ist.meic.pa.GenericFunctions.tests;

import ist.meic.pa.GenericFunctions.tests.domain.*;


public class TestD {
    public static void main(String[] args) {
        Object objects = new Object[] { "Foo", new Integer[] {123, -12}};
        System.out.println(Com.bine(objects));

        Object numbers = new Object[] { 123, new Integer[] {456 , 21}};
        System.out.println(Com.bine(numbers));
    }
}
