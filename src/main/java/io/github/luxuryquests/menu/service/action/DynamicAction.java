package io.github.luxuryquests.menu.service.action;

public class DynamicAction extends Action {

    public DynamicAction(String condition, String value) {
        super(condition, value);
    }

    public synchronized void accept(String value, Runnable runnable) {
        if (this.value.equalsIgnoreCase(value)) {
            runnable.run();
        }
    }
}
