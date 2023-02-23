public class SimpleMessaging implements Messaging {
    @Override
    public String deliverMessage(String message) {
        return ("You have a message: " + message);
    }
}
