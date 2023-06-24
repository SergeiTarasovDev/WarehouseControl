package ru.teliontech.warehousecontrol.exception;

public class NegativeStockException extends Throwable {
    public NegativeStockException(String message) {
        super(message);
    }
}
