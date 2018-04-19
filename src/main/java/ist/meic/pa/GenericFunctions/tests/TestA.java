package ist.meic.pa.GenericFunctions.tests;

import ist.meic.pa.GenericFunctions.tests.domain.*;

public class TestA {
    public static void main(String[] args) {
        Color[] colors = new Color[] { new Red(), new Blue(), new Black()};
        for(Color c : colors) System.out.println(Color.mix(c));
    }

}
