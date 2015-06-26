package Exceptions;

public class InfiniteLoopException extends Exception {
    public InfiniteLoopException() { super(); }
    public InfiniteLoopException(String message) { super(message); }
    public InfiniteLoopException(String message, Throwable cause) { super(message, cause); }
    public InfiniteLoopException(Throwable cause) { super(cause); }
}