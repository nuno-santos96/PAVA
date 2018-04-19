package ist.meic.pa.GenericFunctions.tests;


import ist.meic.pa.GenericFunctions.tests.domain.*;

public class TestB {
    public static void main(String[] args) {
        Object[] objects = new Object[] { new Object(), "Foo", 123};
        for(Object c : objects) System.out.println(Com.bine(c));
    }
}
