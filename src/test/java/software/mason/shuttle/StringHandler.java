package software.mason.shuttle;

public class StringHandler {
    private final StringBuilder sum = new StringBuilder();

    @Subscribe
    public void handleString(String addend) {
        sum.append(addend);
    }

    public StringBuilder getSum() {
        return sum;
    }
}
