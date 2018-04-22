package ist.meic.pa.GenericFunctions;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;

public class WithGenericFunctions {

    private static String DISPATCH = "ist.meic.pa.GenericFunctions.Dispatcher.dispatch";
    private static ArrayList<String> instrumentedClasses = new ArrayList<>();

    //replaces method calls to generic function with calls to dispatch
    private static void instrument(ClassPool cp, CtClass classToInstrument){
        try {
            instrumentedClasses.add(classToInstrument.getName());
            classToInstrument.instrument(new ExprEditor() {
                public void edit(MethodCall m) {
                    try {
                        CtClass calledClass = cp.get(m.getClassName());
                        CtMethod calledMethod = m.getMethod();
                        if (calledClass.getAnnotation(GenericFunction.class) != null){
                            m.replace("$_ = ($r) " + DISPATCH + "($args,\"" + calledClass.getName() + "\",\"" + calledMethod.getName() + "\");");
                            if (!instrumentedClasses.contains(calledClass.getName())){
                                instrument(cp,calledClass);
                                calledClass.toClass();
                            }
                        }
                    } catch (ClassNotFoundException | NotFoundException | CannotCompileException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1){
            System.out.println("Need a class name");
        } else {
            System.out.println("Class : " + args[0]);
            try {
                ClassPool cp = ClassPool.getDefault();
                CtClass ctClass = cp.get(args[0]);
                instrument(cp,ctClass);

                Class<?> rtClass = ctClass.toClass();
                Method main = rtClass.getMethod("main", String[].class);
                main.invoke(null, (Object) Arrays.copyOfRange(args, 1, args.length));
            } catch (NotFoundException | NoSuchMethodException | IllegalAccessException |
                    InvocationTargetException | CannotCompileException e) {
                e.printStackTrace();
            }
        }
    }
}