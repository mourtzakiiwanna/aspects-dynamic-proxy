# Aspect Weaver using Dynamic Proxy API
In this project we will be creating a simple version of an Aspect-Oriented mechanism with the use of Dynamic Proxy API for the aspect weaver implementation. 
## Prerequisites
• Make sure you have Java 11 installed

## Implementation testing

In order to test our Aspect implementation, we have created a Main class. <br/> 
Run the following command to see the results: 

```bash
java Main 
```
where, the expected output is the following: 

```bash
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
```

## Testing Description 

First of all, we have created two interfaces: Greeting and Messaging. </br>
The implemented methods are 'greet' and 'deliverMessage' as shown below, and there are used as targets for the aspect implementation. 

**Greeting interface** <br/> 
The return value of the 'greet' method is 'Hello..' with a parameter given from the user. 
```bash
public class SimpleGreeting implements Greeting {
  @Override
  public String greet(String name) {
    return ("Hello " + name +"!");
  }
}
```

**Messaging interface** <br/> 
The return value of the 'deliverMessage' method is 'You have a message:..' with a parameter given from the user. 
```bash
public class SimpleMessaging implements Messaging {
  @Override
  public String deliverMessage(String message) {
    return ("You have a message: " + message);
  }
}
```
**Main class** <br/> 
In the main method, we first create two aspects (one for the 'greeting' and one for the 'messaging' target) using the Aspect Builder. </br>
We define an advice to be applied before the actual execution of the method (before advice), an advice to be applied after the actual execution (after advice), and one advice (around advice) to be applied instead of the actual execution. </br>
The same is done for the 'messaging' aspect. 

```bash
Aspect greetingAspect = new DynamicProxyAspect.AspectBuilder()
  .withTargets(new Class<?>[]{Greeting.class})
  .withBeforeAdviceFor(() -> System.out.println("This is a greeting...."),
      Greeting.class.getDeclaredMethod("greet", String.class))
  .withAfterAdviceFor(() -> System.out.println("The greeting has been done."),
      Greeting.class.getDeclaredMethod("greet", String.class))
  .withAroundAdviceFor(() -> System.out.println("Hello " + aspectName + "! I'm an aspect! "),
      Greeting.class.getDeclaredMethod("greet", String.class))
  .build();
```

Then, we create a weaver and use it to weave the 'greeting' aspect with a target object. </br>
The same is done for the 'messaging' aspect. 
```bash
DynamicProxyAspect.AspectWeaver aspectWeaverGreet = new DynamicProxyAspect.AspectWeaver(greetingAspect);
Greeting aspectGreet = (Greeting) aspectWeaverGreet.weave(new SimpleGreeting());
```

The next step is to call the 'greet' and 'deliverMessage' without applying aspects, in order to see their normal/original output.

```bash
System.out.println("The normal output is: " + "\n" + "\n" + normalGreet.greet("Jo") + "\n" + 
    normalMessage.deliverMessage("Aspect is OK...") + "\n");
```

Then, we call the methods using the aspect objects (weaving the aspects to the target objects) and the output now is based on the above advices.  

```bash
System.out.println("The output after the aspect weaving is:" +"\n");
aspectGreet.greet(aspectName);
aspectMessaging.deliverMessage(aspectMessage);
```
