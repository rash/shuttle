package software.mason.shuttle;

import java.lang.reflect.Method;

public final class Subscriber {

    private final Object declaringClass;
    private final Method method;

    public Subscriber(Object declaringClass, Method method) {
        this.declaringClass = declaringClass;
        this.method = method;
    }

    public Object getDeclaringClass() {
        return declaringClass;
    }

    public Method getMethod() {
        return method;
    }
}
