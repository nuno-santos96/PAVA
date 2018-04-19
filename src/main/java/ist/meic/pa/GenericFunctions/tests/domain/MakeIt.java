package ist.meic.pa.GenericFunctions.tests.domain;

import ist.meic.pa.GenericFunctions.AfterMethod;
import ist.meic.pa.GenericFunctions.BeforeMethod;
import ist.meic.pa.GenericFunctions.GenericFunction;

@GenericFunction
public class MakeIt {
    public static void ddouble(C1 c) {System.out.println("C1");}

    @BeforeMethod
    @AfterMethod
    public static void ddouble(Object c) {System.out.println("Object");}

    @BeforeMethod
    @AfterMethod
    public static void ddouble(Foo c) {System.out.println("Foo");}

}

