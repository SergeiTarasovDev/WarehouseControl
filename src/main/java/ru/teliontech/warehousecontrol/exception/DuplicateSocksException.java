package ru.teliontech.warehousecontrol.exception;

public class DuplicateSocksException extends Throwable {
    public DuplicateSocksException() {
        super();
    }

    public DuplicateSocksException(String message) {
        super(message);
    }
}