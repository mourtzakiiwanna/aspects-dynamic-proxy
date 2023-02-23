import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class DynamicProxyAspect implements Aspect {

    // targets are the interfaces that this aspect will be applied upon
    private final Class<?>[] targets;

    // creates one map for every advice type that maps a Method with a Runnable
    private final Map<Method, Runnable> beforeMap;
    private final Map<Method, Runnable> afterMap;
    private final Map<Method, Runnable> aroundMap;

    // constructor
    public DynamicProxyAspect(Class<?>[] targets, Map<Method, Runnable> beforeAdviceMap, Map<Method, Runnable> afterAdviceMap, Map<Method, Runnable> aroundAdviceMap) {
        this.targets = targets;
        this.beforeMap = beforeAdviceMap;
        this.afterMap = afterAdviceMap;
        this.aroundMap = aroundAdviceMap;
    }

    // returns the array of Classes that this aspect should be applied upon
    @Override
    public Class<?>[] getTargets() {
        return targets;
    }

    // returns the Runnable, if any, that should be run as before advice for the given method
    @Override
    public Runnable beforeAdviceFor(Method method) {
        return beforeMap.get(method);
    }

    // returns the Runnable, if any, that should be run as after advice for the given method
    @Override
    public Runnable afterAdviceFor(Method method) {
        return afterMap.get(method);
    }

    // returns the Runnable, if any, that should be run as around advice for the given method
    @Override
    public Runnable aroundAdviceFor(Method method) {
        return aroundMap.get(method);
    }

    // creates an aspect object implementing the "Builder" interface
    public static class AspectBuilder implements Builder {

        private Class<?>[] targets;
        private final Map<Method, Runnable> beforeAdviceMap = new HashMap<>();
        private final Map<Method, Runnable> afterAdviceMap = new HashMap<>();
        private final Map<Method, Runnable> aroundAdviceMap = new HashMap<>();

        @Override
        public Builder withTargets(Class<?>[] targets) {
            this.targets = targets;
            return this;
        }

        @Override
        public Builder withBeforeAdviceFor(Runnable beforeAdvice, Method... methods) {
            for (Method method : methods) {
                beforeAdviceMap.put(method, beforeAdvice);
            }
            return this;
        }

        @Override
        public Builder withAfterAdviceFor(Runnable afterAdvice, Method... methods) {
            for (Method method : methods) {
                afterAdviceMap.put(method, afterAdvice);
            }
            return this;
        }

        @Override
        public Builder withAroundAdviceFor(Runnable aroundAdvice, Method... methods) {
            for (Method method : methods) {
                aroundAdviceMap.put(method, aroundAdvice);
            }
            return this;
        }

        @Override
        public Aspect build() {
            return new DynamicProxyAspect(targets, beforeAdviceMap, afterAdviceMap, aroundAdviceMap);
        }
    }

    public static class AspectWeaver implements Weaver {

        // the aspect that will be applied on the interfaces
        private final Aspect aspect;

        public AspectWeaver(Aspect aspect) {
            this.aspect = aspect;
        }

        public Object weave(Object target) {
            // all the interfaces that this aspect will be applied upon
            Class<?>[] interfaces = target.getClass().getInterfaces();
            // creates an Invocation Handler
            ProxyHandler proxyHandler = new ProxyHandler(target, aspect);
            // returns a proxy instance with the specified parameters
            return Proxy.newProxyInstance(target.getClass().getClassLoader(), interfaces, proxyHandler);
        }
    }

    public static class ProxyHandler implements InvocationHandler {

        public Object target;
        public Aspect aspect;

        public ProxyHandler(Object target, Aspect aspect) {
            this.target = target;
            this.aspect = aspect;
        }

        // invocation of the dynamic proxy
        // the args parameter is only used in the case of the 'around' advice
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            // case of 'before' advice
            Runnable beforeAdvice = aspect.beforeAdviceFor(method);
            if (beforeAdvice != null) {
                beforeAdvice.run();
            }

            // case of 'around' advice
            Object result;
            Runnable aroundAdvice = aspect.aroundAdviceFor(method);
            if (aroundAdvice != null) {
                Callable<Object> callable = () -> {
                    aroundAdvice.run();
                    return null;
                };
                result = callable.call();
            } else {
                result = method.invoke(target, args);
            }

            // case of 'after' advice
            Runnable afterAdvice = aspect.afterAdviceFor(method);
            if (afterAdvice != null) {
                afterAdvice.run();
            }

            return result;
        }
    }

    // this Factory is not used in our implementation
    @Deprecated
    public static class AspectFactory implements Factory {
        @Override
        public Builder newBuilder() {
            return new AspectBuilder();
        }

        @Override
        public Weaver newWeaver() {
            return new AspectWeaver(this.newBuilder().build());
        }
    }
}


