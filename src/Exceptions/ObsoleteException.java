package Exceptions;

public class ObsoleteException extends Exception {
    public ObsoleteException() { super(); }
    public ObsoleteException(String message) { super(message); }
    public ObsoleteException(String message, Throwable cause) { super(message, cause); }
    public ObsoleteException(Throwable cause) { super(cause); }
}