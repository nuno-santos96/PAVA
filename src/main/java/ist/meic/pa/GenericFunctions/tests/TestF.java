package ist.meic.pa.GenericFunctions.tests;

import ist.meic.pa.GenericFunctions.tests.domain.*;

public class TestF {
    public static void main(String[] args) {
        Object c1 = new C1(), c2 = new C2();
        Bug.bug(c1);
        Bug.bug(c2);
    }
}
