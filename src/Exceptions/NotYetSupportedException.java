package Exceptions;

public class NotYetSupportedException extends Exception {
    public NotYetSupportedException() { super(); }
    public NotYetSupportedException(String message) { super(message); }
    public NotYetSupportedException(String message, Throwable cause) { super(message, cause); }
    public NotYetSupportedException(Throwable cause) { super(cause); }
}