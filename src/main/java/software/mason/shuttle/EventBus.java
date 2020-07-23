package software.mason.shuttle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class EventBus {

    private static final Logger logger = Logger.getLogger(EventBus.class.getName());
    private final Map<Class<?>, Set<Subscriber>> typeToSubscribersMap = new LinkedHashMap<>();
    private final Set<Object> registeredListeners = new HashSet<>();

    public void register(Object listener) {
        if (registeredListeners.contains(listener)) {
            throw new IllegalArgumentException(listener + " is already registered.");
        }
        registeredListeners.add(listener);
        for (var method : listener.getClass().getMethods()) {
            if (isSubscriber(method)) {
                var type = method.getParameters()[0].getType();
                var subscribers = typeToSubscribersMap.computeIfAbsent(type, k -> new LinkedHashSet<>());
                var subscriber = new Subscriber(listener, method);
                subscribers.add(subscriber);
            }
        }
    }

    public void unregister(Object listener) {
        if (registeredListeners.contains(listener)) {
            registeredListeners.remove(listener);
            for (var method : listener.getClass().getMethods()) {
                if (isSubscriber(method)) {
                    var type = method.getParameters()[0].getType();
                    var subscribers = typeToSubscribersMap.get(type);
                    subscribers.removeIf(subscriber -> subscriber.getMethod().equals(method));
                }
            }
        } else {
            throw new IllegalArgumentException(listener + " is not already registered.");
        }
    }

    public void push(Object event) {
        var type = event.getClass();
        var subscribers = typeToSubscribersMap.get(type);
        if (subscribers != null) {
            for (var subscriber : subscribers) {
                try {
                    subscriber.getMethod().invoke(subscriber.getDeclaringClass(), event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }

    private boolean isSubscriber(Method method) {
        return hasSubscribeAnnotation(method) && hasValidModifiers(method) && hasOnlyOneParameter(method);
    }

    private boolean hasSubscribeAnnotation(Method method) {
        return method.getAnnotation(Subscribe.class) != null;
    }

    private boolean hasValidModifiers(Method method) {
        return Modifier.isPublic(method.getModifiers()) && !Modifier.isAbstract(method.getModifiers());
    }

    private boolean hasOnlyOneParameter(Method method) {
        return method.getParameterCount() == 1;
    }
}
