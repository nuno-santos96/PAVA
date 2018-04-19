package ist.meic.pa.GenericFunctions;

import javassist.*;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Dispatcher {

    private static int calculateDistance(Class calledType, Class definedType){
        int dist = 0;
        Class aux = calledType;
        while (!aux.equals(definedType)) {
            boolean fits = false;
            for (Class calledInterface : aux.getInterfaces()) {
                if (calledInterface.equals(definedType)) {
                    fits = true;
                    break;
                }
            }
            if (fits)
                break;

            dist++;
            aux = aux.getSuperclass();
            if (aux == null) {
                return Integer.MAX_VALUE;
            }
        }
        return dist;
    }

    private static int calculateMinimum(ArrayList<Integer> distances){
        int min = distances.get(0);
        for (int i : distances)
            min = min < i ? min : i;
        return min;
    }

    private static void handleBeforeAndAfter(Class typeOfMethod, List<Method> methodsThatFit, List<Class> calledParameters, Object[] args){
        //saving distances for each method
        TreeMap<Integer,Method> overallDistance = new TreeMap<>();
        for (Method me : methodsThatFit) {
            if (me.isAnnotationPresent(typeOfMethod)){
                ArrayList<String> distances = new ArrayList<>();
                int curr = 0;
                for (Class calledType : calledParameters){
                    distances.add(Integer.toString(calculateDistance(calledType,me.getParameterTypes()[curr])));
                    curr++;
                }
                overallDistance.put(Integer.parseInt(String.join("", distances)),me);
            }
        }
        Set resultSet = null;
        if (typeOfMethod.equals(BeforeMethod.class)){
            //execute before methods from most to least specific
            resultSet = overallDistance.keySet();
        } else {//AfterMethod
            //execute after methods from least to most specific
            List list = new ArrayList(overallDistance.keySet());
            Collections.sort(list, Collections.reverseOrder());
            resultSet = new LinkedHashSet(list);
        }
        for(Object key : resultSet) {
            try {
                overallDistance.get(key).invoke(null, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            } 
        }
    }

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
                for (Class calledType : calledParameters) {
                    Iterator<Method> i = methodsThatFit.iterator();
                    while (i.hasNext()) {
                        Class definedType = i.next().getParameterTypes()[curr];
                        int distance = calculateDistance(calledType,definedType);
                        if (distance == Integer.MAX_VALUE)
                            i.remove();
                    }
                    curr++;
                }

                ArrayList<Method> originalMethodsThatFit = new ArrayList<Method>(methodsThatFit);
                //doing before methods
                handleBeforeAndAfter(BeforeMethod.class, originalMethodsThatFit, calledParameters, args);

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
                for (Class calledType : calledParameters){
                    //get distances
                    ArrayList<Integer> distances = new ArrayList<>();
                    for (Method me : methodsThatFit){
                        Class definedType = me.getParameterTypes()[curr];
                        int distance = calculateDistance(calledType,definedType);
                        distances.add(distance);
                    }

                    //if no method fits
                    if (methodsThatFit.size() == 0) {
                        System.out.println("Can't call any method!");
                        found = false;
                        break;
                    }

                    //get minimum distance
                    int min = distances.get(0);
                    for (int i : distances)
                        min = min < i ? min : i;

                    //remove the ones that dont have minimum distance
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
                    rightMethod.setAccessible(true);
                    returnValue = rightMethod.invoke(null, args);

                    //doing after methods
                    handleBeforeAndAfter(AfterMethod.class, originalMethodsThatFit, calledParameters, args);
                }
                return returnValue;
            }
        } catch (ClassNotFoundException | IllegalAccessException |InvocationTargetException e) {
            e.printStackTrace();
        } 
        return 0;
    }
}