package software.mason.shuttle;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventBusTest {

    private static final String EVENT_MESSAGE = "Working as expected.";
    private final EventBus eventBus = new EventBus();

    @Test
    public void registeredListenerShouldHandleSubscribedEventType() {
        var stringHandler = new StringHandler();
        eventBus.register(stringHandler);
        eventBus.push(EVENT_MESSAGE);
        assertEquals(EVENT_MESSAGE, stringHandler.getSum().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void registeredInstanceShouldNotRegisterTwice() {
        var stringHandler = new StringHandler();
        eventBus.register(stringHandler);
        eventBus.register(stringHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonRegisteredInstanceShouldNotAttemptUnregister() {
        var stringHandler = new StringHandler();
        eventBus.unregister(stringHandler);
    }
}
