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

                //removing incompatible methods
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
                
                //doing before methods
                for (Method me : methodsThatFit) {
                    if (me.isAnnotationPresent(BeforeMethod.class)){
                        me.invoke(null, args);
                    }
                }
                ArrayList<Method> originalMethodsThatFit = new ArrayList<Method>(methodsThatFit);

                Iterator<Method> ite = methodsThatFit.iterator();
                while (ite.hasNext()) {
                    Method meth = ite.next();
                    if (meth.isAnnotationPresent(BeforeMethod.class) ||
                            meth.isAnnotationPresent(AfterMethod.class)) {
                        ite.remove();
                    }
                }

                //calculating method with the closest parameters
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
                                //dist = Integer.MAX_VALUE;
                                break;
                            }
                        }
                        distances.add(dist);
                    }

                    //if no method fits
                    if (methodsThatFit.size() == 0) {
                        System.out.println("Can't call any method!");
                        found = false;
                        break;
                    }

                    int min = distances.get(0);
                    for (int i : distances){
                        min = min < i ? min : i;
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

                    curr++;
                }
                Object returnValue=null;
                if (found) {
                    Method rightMethod = methodsThatFit.get(0);
                    returnValue = rightMethod.invoke(null, args);

                    //doing after methods
                    for (Method me : originalMethodsThatFit) {
                        if (me.isAnnotationPresent(AfterMethod.class)){
                            me.invoke(null, args);
                        }
                    }
                }


                return returnValue;
            }
        } catch (ClassNotFoundException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
        } 
        return 0;
    }
}