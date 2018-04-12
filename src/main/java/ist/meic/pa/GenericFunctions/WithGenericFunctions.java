package ist.meic.pa.GenericFunctions;

import javassist.*;
import java.io.*;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class WithGenericFunctions {
    public String getGreeting() {
        return "Hello world.";
    }

    public static void main(String[] args) {
        if (args.length < 1){
            System.out.println("Need a class name");
            throw new IllegalArgumentException();
        } else {
            Loader classLoader = new Loader();
            try {
                classLoader.run(args[0],new String[] {});
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}