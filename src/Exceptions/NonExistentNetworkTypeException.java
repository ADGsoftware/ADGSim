package Exceptions;

public class NonExistentNetworkTypeException extends Exception {
    public NonExistentNetworkTypeException() { super(); }
    public NonExistentNetworkTypeException(String message) { super(message); }
    public NonExistentNetworkTypeException(String message, Throwable cause) { super(message, cause); }
    public NonExistentNetworkTypeException(Throwable cause) { super(cause); }
}