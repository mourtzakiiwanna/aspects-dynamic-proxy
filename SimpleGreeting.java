public class SimpleGreeting implements Greeting {
    @Override
    public String greet(String name) {
        //System.out.println("Hello" + name +"!");
        return ("Hello " + name +"!");
    }
}
