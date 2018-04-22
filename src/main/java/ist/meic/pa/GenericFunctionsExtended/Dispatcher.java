package ist.meic.pa.GenericFunctionsExtended;

import javassist.*;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Dispatcher {
    private static ArrayList<Method> methodsThatFit = new ArrayList<>();
    private static HashMap<ArrayList<Class>,Method> methodCache = new HashMap<>();
    private static HashMap<ArrayList<Class>,ArrayList<Method>> beforeCache = new HashMap<>();
    private static HashMap<ArrayList<Class>,ArrayList<Method>> afterCache = new HashMap<>();
    //compares 2 parameter types (of the called method and the defined method), returning their distance
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

    //calculates the order for the execution and invokes before/after methods
    private static void handleBeforeAndAfter(Class typeOfMethod, List<Method> methodsThatFit, ArrayList<Class> calledParameters, Object[] args){
        //savins distances for each method
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
        ArrayList<Method> methodsToCache = new ArrayList<>();
        for(Object key : resultSet) {
            try {
                Method m =overallDistance.get(key);
                methodsToCache.add(m);
                m.invoke(null, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            } 
        }
        if (typeOfMethod.equals(BeforeMethod.class))
            beforeCache.put(calledParameters, methodsToCache);
        else
            afterCache.put(calledParameters, methodsToCache);
    }

    //choosing the method with the closest parameters to the one that was called
    private static Method chooseClosestMethod(ArrayList<Class> calledParameters){
        int curr = 0;
        for (Class calledType : calledParameters){

            //if no method fits
            if (methodsThatFit.size() == 0) {
                System.out.println("Can't call any method!");
                return null;
            }

            //get distances
            ArrayList<Integer> distances = new ArrayList<>();
            for (Method me : methodsThatFit){
                Class definedType = me.getParameterTypes()[curr];
                int distance = calculateDistance(calledType,definedType);
                distances.add(distance);
            }

            int min = calculateMinimum(distances);

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
        return methodsThatFit.get(0);
    }

    //choose which methods to call, and invokes them
    public static Object dispatch(Object[] args, String calledClassName, String calledMethodName){
        ArrayList<Class> calledParameters = new ArrayList<>();  //types of the parameters that were in the method call
        Object returnValue=null;

        //get parameters types of the method call
        for (Object arg : args)
            calledParameters.add(arg.getClass());

        //checking cache
        if (methodCache.containsKey(calledParameters)){
            Method method =methodCache.get(calledParameters);
            if (method==null){
                System.out.println("Can't call any method!");
                return null;
            }
            try {
                for (Method m : beforeCache.get(calledParameters)){
                    m.invoke(null, args);
                }
                returnValue = method.invoke(null, args);
                for (Method m : afterCache.get(calledParameters)){
                    m.invoke(null, args);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            } 
            return returnValue;
        }
        
        try {
            Class calledClass = Class.forName(calledClassName);

            //get all methods that possibly fit the method call
            for (Method method : calledClass.getDeclaredMethods()){
                if (method.getName().equals(calledMethodName) &&
                        method.getParameterCount() == args.length &&
                        Modifier.isStatic(method.getModifiers())){
                    methodsThatFit.add(method);
                }
            }

            //removing incompatible methods
            int curr = 0;
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

            ArrayList<Method> originalMethodsThatFit = new ArrayList<>(methodsThatFit);

            //removing before and after methods from the methodsThatFit
            //this methods are not considered on the search of the closest method
            Iterator<Method> ite = methodsThatFit.iterator();
            while (ite.hasNext()) {
                Method meth = ite.next();
                if (meth.isAnnotationPresent(BeforeMethod.class) ||
                        meth.isAnnotationPresent(AfterMethod.class)) {
                    ite.remove();
                }
            }

            //only execute before methods if there's at least one compatible method that fits the call
            if (!methodsThatFit.isEmpty()) {
                handleBeforeAndAfter(BeforeMethod.class, originalMethodsThatFit, calledParameters, args);
            }

            //calculating and invoking method with the closest parameters
            Method rightMethod = chooseClosestMethod(calledParameters);
            if (rightMethod != null) {
                rightMethod.setAccessible(true);
                returnValue = rightMethod.invoke(null, args);
                
                //doing after methods
                handleBeforeAndAfter(AfterMethod.class, originalMethodsThatFit, calledParameters, args);
            }
            methodCache.put(calledParameters,rightMethod);
        } catch (ClassNotFoundException | IllegalAccessException |InvocationTargetException e) {
            e.printStackTrace();
        }
        return returnValue;
    }
}