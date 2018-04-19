package ist.meic.pa.GenericFunctions.tests.domain;

import ist.meic.pa.GenericFunctions.GenericFunction;

@GenericFunction
public class Color {

    // Test A
    public static String mix(Object o) { return "I'm just an object."; }
    public static String mix(Color c1) { return "What color am I?"; }
    public static String mix(Red c1) { return "Red"; }
    public static String mix(Object[] arr) {
        String res = "";
        for (Object o : arr) res += mix(o);
        return res;
    }

    // Test M
    public static String mix(Color c1, Color c2) { return mix(c2, c1); }
    public static String mix(Red c1, Red c2) {
        return "More red";
    }
    public static String mix(Blue c1, Blue c2) {
        return "More blue";
    }
    public static String mix(Yellow c1, Yellow c2) {
        return "More yellow";
    }

    public static String mix(Red c1, Blue c2) {
        return "Magenta";
    }
    public static String mix(Red c1, Yellow c2) {
        return "Orange";
    }
    public static String mix(Blue c1, Yellow c2) {
        return "Green";
    }

    // Test N
    public static String mix(Object c1, Object c2, Object c3) { return "Object-Object-Object"; }
    public static String mix(Color c1, Color c2, Color c3) {
        return "Color-Color-Color";
    }
    public static String mix(Object c1, Color c2, Color c3) {
        return "Object-Color-Color";
    }
    public static String mix(Color c1, Object c2, Color c3) {
        return "Color-Object-Color";
    }
    public static String mix(Color c1, Color c2, Object c3) {
        return "Color-Color-Object";
    }
    public static String mix(Red c1, Color c2, Color c3) {
        return "Red-Color-Color";
    }
    public static String mix(Color c1, Red c2, Color c3) {
        return "Color-Red-Color";
    }
    public static String mix(Color c1, Color c2, Red c3) {
        return "Color-Color-Red";
    }
    public static String mix(Red c1, Color c2, Red c3) {
        return "Red-Color-Red";
    }
    public static String mix(Color c1, SuperBlack c2, Color c3) {
        return "Color-SuperBlack-Color";
    }
    public static String mix(Black c1, SuperBlack c2, Color c3) {
        return "Black-SuperBlack-Color";
    }
    public static String mix(SuperBlack c1, Black c2, Color c3) {
        return "SuperBlack-Black-Color";
    }
    public static String mix(SuperBlack c1, Black c2, Black c3) {
        return "SuperBlack-Black-Black";
    }
    public static String mix(SuperBlack c1, Black c2, SuperBlack c3) {
        return "SuperBlack-Black-SuperBlack";
    }
}
