package ist.meic.pa.GenericFunctions;

import javassist.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

public class Dispatcher {
    public static Object dispatch(Object[] args, String calledClassName, String calledMethodName){
        ArrayList<Method> methodsThatFit = new ArrayList<>();
        try {
            Class calledClass = Class.forName(calledClassName);
            if (calledClass.isAnnotationPresent(GenericFunction.class) ){
                for (Method method : calledClass.getDeclaredMethods()){
                    if (method.getName().equals(calledMethodName) &&
                            method.getParameterCount() == args.length &&
                            Modifier.isStatic(method.getModifiers())){
                        methodsThatFit.add(method);
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
                    Method rightMethod = methodsThatFit.get(0);
                    return rightMethod.invoke(null, args);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return 0;
    }
}