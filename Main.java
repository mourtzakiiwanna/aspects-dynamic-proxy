/*
 This class is used to test the implementation of the Aspects.
 First, it creates an aspect with target the 'Greeting' interface, and 'before', 'around' and 'after' advices.
 Then, it creates an aspect weaver, in order to weave the aspect with a target object.
 */
public class Main {
    public static void main(String[] args) throws NoSuchMethodException {

        // this object will be used in order to see the normal output of the 'greet' method
        SimpleGreeting normalGreet = new SimpleGreeting();
        String aspectName = "Ioanna";

        // this object will be used in order to see the normal output of the 'deliverMessage' method
        SimpleMessaging normalMessage = new SimpleMessaging();
        String aspectMessage = "Aspect rocks!";

        AspectImplementation.AspectFactory aspectFactory = new AspectImplementation.AspectFactory();

        // creates an aspect that logs a message before and after the 'greet' method is called
        // also it applies an around advice, and prints something else, instead of the method original print value
        AspectImplementation.AspectBuilder greetingBuilder =
                (AspectImplementation.AspectBuilder) aspectFactory.newBuilder();

        Aspect greetingAspect = greetingBuilder
                .withTargets(new Class<?>[]{Greeting.class})
                .withBeforeAdviceFor(() -> System.out.println("This is a greeting...."),
                        Greeting.class.getDeclaredMethod("greet", String.class))
                .withAfterAdviceFor(() -> System.out.println("The greeting has been done."),
                        Greeting.class.getDeclaredMethod("greet", String.class))
                .withAroundAdviceFor(() -> System.out.println("Hello " + aspectName + "! I'm an aspect! "),
                        Greeting.class.getDeclaredMethod("greet", String.class))
                .build();

        // creates an aspect that logs a message before and after the 'deliverMessage' method is called
        // also it applies an around advice, and prints something else, instead of the method original print value
        AspectImplementation.AspectBuilder messageBuilder =
                (AspectImplementation.AspectBuilder) aspectFactory.newBuilder();

        Aspect messageAspect = messageBuilder
                .withTargets(new Class<?>[]{Messaging.class})
                .withBeforeAdviceFor(() -> System.out.println("This is a message deliver...."),
                        Messaging.class.getDeclaredMethod("deliverMessage", String.class))
                .withAfterAdviceFor(() -> System.out.println("The message has been delivered."),
                        Messaging.class.getDeclaredMethod("deliverMessage", String.class))
                .withAroundAdviceFor(() -> System.out.println("You have a message: " + aspectMessage +"!"),
                        Messaging.class.getDeclaredMethod("deliverMessage", String.class))
                .build();


        // creates a weaver and uses it to weave the greeting aspect with a target object
        AspectImplementation.AspectWeaver aspectWeaverGreet =
                (AspectImplementation.AspectWeaver) aspectFactory.newWeaver();
        aspectWeaverGreet.setAspect(greetingAspect);
        Greeting aspectGreet = (Greeting) aspectWeaverGreet.weave(new SimpleGreeting());

        // creates a weaver and uses it to weave the message aspect with a target object
        AspectImplementation.AspectWeaver aspectWeaverMessage =
                (AspectImplementation.AspectWeaver) aspectFactory.newWeaver();
        aspectWeaverMessage.setAspect(messageAspect);
        Messaging aspectMessaging = (Messaging) aspectWeaverMessage.weave(new SimpleMessaging());

        // calls the greet method to see the normal output of the method
        System.out.println("\n" + "-------------------------------------");
        System.out.println("The normal output is: " + "\n" + "\n" + normalGreet.greet("Jo") + "\n" +
                normalMessage.deliverMessage("Aspect is OK...") + "\n");
        System.out.println("The output after the aspect weaving is:" +"\n");

        // calls the target method and see the aspect in action
        aspectGreet.greet(aspectName);
        System.out.println();
        aspectMessaging.deliverMessage(aspectMessage);
        System.out.println("-------------------------------------" + "\n");
    }
}

/*
EXPECTED OUTPUT

-------------------------------------
The normal output is:

Hello Jo!
You have a message: Aspect is OK...

The output after the aspect weaving is:

This is a greeting....
Hello Ioanna! I'm an aspect!
The greeting has been done.

This is a message deliver....
You have a message: Aspect rocks!!
The message has been delivered.
-------------------------------------

 */
