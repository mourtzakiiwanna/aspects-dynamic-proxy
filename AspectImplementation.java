import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

// added final to some fields
public class AspectImplementation implements Aspect {

    private final Class<?>[] targets;
    private final Map<Method, Runnable> beforeAdvice;
    private final Map<Method, Runnable> afterAdvice;
    private final Map<Method, Runnable> aroundAdvice;

    private AspectImplementation(Builder builder) {
        this.targets = builder.targets;
        this.beforeAdvice = builder.beforeAdvice;
        this.afterAdvice = builder.afterAdvice;
        this.aroundAdvice = builder.aroundAdvice;
    }

    @Override
    public Class<?>[] getTargets() {
        return targets;
    }

    @Override
    public Runnable beforeAdviceFor(Method method) {
        return beforeAdvice.get(method);
    }

    @Override
    public Runnable afterAdviceFor(Method method) {
        return afterAdvice.get(method);
    }

    @Override
    public Runnable aroundAdviceFor(Method method) {
        return aroundAdvice.get(method);
    }

    public static class Builder implements Aspect.Builder {
        private Class<?>[] targets;
        private final Map<Method, Runnable> beforeAdvice;
        private final Map<Method, Runnable> afterAdvice;
        private final Map<Method, Runnable> aroundAdvice;

        public Builder() {
            beforeAdvice = new HashMap<>();
            afterAdvice = new HashMap<>();
            aroundAdvice = new HashMap<>();
        }

        @Override
        public Builder withTargets(Class<?>[] targets) {
            this.targets = targets;
            return this;
        }

        @Override
        public Builder withBeforeAdviceFor(Runnable beforeAdvice, Method... methods) {
            for (Method method : methods) {
                this.beforeAdvice.put(method, beforeAdvice);
            }
            return this;
        }

        @Override
        public Builder withAfterAdviceFor(Runnable afterAdvice, Method... methods) {
            for (Method method : methods) {
                this.afterAdvice.put(method, afterAdvice);
            }
            return this;
        }

        @Override
        public Builder withAroundAdviceFor(Runnable aroundAdvice, Method... methods) {
            for (Method method : methods) {
                this.aroundAdvice.put(method, aroundAdvice);
            }
            return this;
        }

        @Override
        public Aspect build() {
            return new AspectImplementation(this);
        }
    }

    public static class Weaver implements Aspect.Weaver {
        @Override
        public Object weave(final Object target) {
            Aspect aspect = (Aspect) target;
            Class<?>[] targets = aspect.getTargets();

            InvocationHandler handler = (proxy, method, args) -> {
                Runnable beforeAdvice = aspect.beforeAdviceFor(method);
                Runnable aroundAdvice = aspect.aroundAdviceFor(method);
                Runnable afterAdvice = aspect.afterAdviceFor(method);

                if (beforeAdvice != null) {
                    beforeAdvice.run();
                }

                Object result = null;
                if (aroundAdvice != null) {
                    aroundAdvice.run();
                } else {
                    result = method.invoke(target, args);
                }

                if (afterAdvice != null) {
                    afterAdvice.run();
                }

                return result;
            };
            return Proxy.newProxyInstance(target.getClass().getClassLoader(), targets, handler);
        }

    }
}
