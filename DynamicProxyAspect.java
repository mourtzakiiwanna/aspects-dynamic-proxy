import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class DynamicProxyAspect implements Aspect {
    private final Class<?>[] targets;
    private final Map<Method, Runnable> beforeAdviceMap;
    private final Map<Method, Runnable> afterAdviceMap;
    private final Map<Method, Runnable> aroundAdviceMap;

    public DynamicProxyAspect(Class<?>[] targets, Map<Method, Runnable> beforeAdviceMap, Map<Method, Runnable> afterAdviceMap, Map<Method, Runnable> aroundAdviceMap) {
        this.targets = targets;
        this.beforeAdviceMap = beforeAdviceMap;
        this.afterAdviceMap = afterAdviceMap;
        this.aroundAdviceMap = aroundAdviceMap;
    }

    @Override
    public Class<?>[] getTargets() {
        return targets;
    }

    @Override
    public Runnable beforeAdviceFor(Method method) {
        return beforeAdviceMap.get(method);
    }

    @Override
    public Runnable afterAdviceFor(Method method) {
        return afterAdviceMap.get(method);
    }

    @Override
    public Runnable aroundAdviceFor(Method method) {
        return aroundAdviceMap.get(method);
    }

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

        private final Aspect aspect;

        public AspectWeaver(Aspect aspect) {
            this.aspect = aspect;
        }

        public Object weave(Object target) {
            Class<?>[] interfaces = target.getClass().getInterfaces();
            InvocationHandler handler = new ProxyHandler(target, aspect);
            return Proxy.newProxyInstance(target.getClass().getClassLoader(), interfaces, handler);
        }

    }

    public static class ProxyHandler implements InvocationHandler {

        public Object target;
        public Aspect aspect;

        public ProxyHandler(Object target, Aspect aspect) {
            this.target = target;
            this.aspect = aspect;
        }


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            Runnable beforeAdvice = aspect.beforeAdviceFor(method);
            if (beforeAdvice != null) {
                beforeAdvice.run();
            }

            Object result;
            if (aspect.aroundAdviceFor(method) != null) {
                Runnable aroundAdvice = aspect.aroundAdviceFor(method);
                Callable<Object> callable = () -> {
                    aroundAdvice.run();
                    return null;
                };
                result = callable.call();
            } else {
                result = method.invoke(target, args);
            }

            Runnable afterAdvice = aspect.afterAdviceFor(method);
            if (afterAdvice != null) {
                afterAdvice.run();
            }
            return result;
        }


    }


//    public static class AspectFactory implements Factory {
//        @Override
//        public Builder newBuilder() {
//            return new AspectBuilder();
//        }
//
//        @Override
//        public Weaver newWeaver() {
//            return new AspectWeaver(this.newBuilder().build());
//        }
//    }
}


