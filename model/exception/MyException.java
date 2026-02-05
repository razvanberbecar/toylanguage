package model.exception;

public class MyException extends Exception{
    public MyException(String message) {
        super(message);
    }
    public MyException(MyException e) {
        super(e);
    }
}
