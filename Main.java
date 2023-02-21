import java.lang.reflect.Proxy;

public class Main {
    public static void main(String[] args) throws NoSuchMethodException {
        SimpleGreeting greet = new SimpleGreeting();
        String name = "Jo";

        // Create an aspect that logs a message before and after the "greet" method is called
        Aspect aspect = new DynamicProxyAspect.AspectBuilder()
                .withTargets(new Class<?>[]{Greeting.class})
                .withBeforeAdviceFor(() -> System.out.println("This is gonna be a log...."),
                        Greeting.class.getDeclaredMethod("greet", String.class))
                .withAfterAdviceFor(() -> System.out.println("The log is logged!"),
                        Greeting.class.getDeclaredMethod("greet", String.class))
                .withAroundAdviceFor(() -> System.out.println("Hello " + name),
                        Greeting.class.getDeclaredMethod("greet", String.class))
                .build();

        // Create a weaver and use it to weave the aspect with a target object
        DynamicProxyAspect.AspectWeaver aspectWeaver = new DynamicProxyAspect.AspectWeaver(aspect);

        Greeting greeting = (Greeting) aspectWeaver.weave(new SimpleGreeting());

        // Call the target method and see the aspect in action
        System.out.println("The normal output is: " + greet.greet("Joanna"));
        greeting.greet("Joanna");
    }
}
