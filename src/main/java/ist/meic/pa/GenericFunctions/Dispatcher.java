package ist.meic.pa.GenericFunctions;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

public class Dispatcher {
    public static void dispatch(Object[] args, String className){
        ArrayList<Method> methodsThatFit = new ArrayList<>();
        try {
            ClassPool cp = ClassPool.getDefault();
            CtClass ctClass = cp.get(className);
            //The class TestCombine was frozen because it's currently loaded
            ctClass.defrost();
            CtMethod method = ctClass.getDeclaredMethod("main");
            method.instrument(new ExprEditor() {
                public void edit(MethodCall m) {
                    try {
                        Class calledClass = Class.forName(m.getClassName());
                        CtMethod calledMethod = m.getMethod();

                        if (calledClass.isAnnotationPresent(GenericFunction.class) ){
                            System.out.println("Possible methods that fit the method call:");
                            for (Method method : calledClass.getDeclaredMethods()){
                                if (method.getName().equals(calledMethod.getName()) &&
                                        method.getParameterCount() == calledMethod.getParameterTypes().length &&
                                        Modifier.isStatic(method.getModifiers())){
                                    methodsThatFit.add(method);
                                    System.out.println(method);
                                }
                            }

                            ArrayList<Class> calledParameters = new ArrayList<>();
                            for (Object o : args)
                                calledParameters.add(o.getClass());

                            int curr = 0;
                            Boolean found = true;
                            ArrayList<Method> toRemove = new ArrayList<>();
                            for (Class a : calledParameters) {
                                for (Method me : methodsThatFit) {
                                    Class b = me.getParameterTypes()[curr];
                                    Class aux = a;
                                    while (!aux.equals(b)) {
                                        aux = aux.getSuperclass();
                                        if (aux == null) {
                                            toRemove.add(me);
                                            break;
                                        }
                                    }
                                }
                                curr++;
                            }
                            for (Method method : toRemove)
                                methodsThatFit.remove(method);

                            curr = 0;

                            for (Class a : calledParameters){
                                ArrayList<Integer> distances = new ArrayList<>();
                                for (Method me : methodsThatFit){
                                    Class b = me.getParameterTypes()[curr];
                                    int dist = 0;
                                    Class aux = a;
                                    while (!aux.equals(b)){
                                        dist++;
                                        aux = aux.getSuperclass();
                                        if (aux == null){
                                            dist = Integer.MAX_VALUE;
                                            break;
                                        }
                                    }
                                    distances.add(dist);
                                }

                                int min = distances.get(0);
                                for (int i : distances){
                                    min = min < i ? min : i;
                                }

                                //if no method fits
                                if (min == Integer.MAX_VALUE) {
                                    System.out.println("Can't call any method!");
                                    found = false;
                                    break;
                                }

                                Iterator<Integer> it = distances.iterator();
                                Iterator<Method> it2 = methodsThatFit.iterator();
                                while (it.hasNext()) {
                                    it2.next();
                                    if (it.next() != min) {
                                        it.remove();
                                        it2.remove();
                                    }
                                }

                                //if no method fits
                                if (methodsThatFit.size() == 0) {
                                    System.out.println("Can't call any method!");
                                    found = false;
                                    break;
                                }
                                curr++;
                            }

                            if (found) {
                                for (Method aba : methodsThatFit) {
                                    System.out.println("Chosen method:");
                                    System.out.println(aba);
                                }

                                //TODO: change method call name and chosen method name
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    } 
                }
            });
/*
            Class<?> rtClass = ctClass.toClass();
                Method main = rtClass.getMethod("main", String[].class);
                String[] params = null; // init params accordingly
                main.invoke(null, (Object) params);*/


        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }      
    }
}