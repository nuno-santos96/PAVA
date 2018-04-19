package ist.meic.pa.GenericFunctions.tests.domain;

import ist.meic.pa.GenericFunctions.AfterMethod;
import ist.meic.pa.GenericFunctions.BeforeMethod;
import ist.meic.pa.GenericFunctions.GenericFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@GenericFunction
public class Bug {
    // Test F
    public static void bug(Object o) {
        System.out.println("Object");
    }
    public static void bug(Foo f){
        System.out.println("Foo");
    }
    public static void bug(Bar b){ System.out.println("Bar"); }
}
