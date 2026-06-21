package de.ControlClosure.Utils;

public class MathUtils {

    public static int lg2f(int n) {
        return (int) lg2(n);
    }

    public static double lg2c(int n) {
        return Math.ceil(Math.log(n) / Math.log(2));
    }

    public static double lg2(int n) {
        return Math.log(n) / Math.log(2);
    }

}