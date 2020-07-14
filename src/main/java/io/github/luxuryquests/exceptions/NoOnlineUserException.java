package io.github.luxuryquests.exceptions;

public class NoOnlineUserException extends RuntimeException {
    private static final long serialVersionUID = -2643890184038L;

    public NoOnlineUserException(String message) {
        super(message);
    }
}
