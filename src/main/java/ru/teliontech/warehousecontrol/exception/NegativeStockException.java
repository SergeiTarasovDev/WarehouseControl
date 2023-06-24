package ru.teliontech.warehousecontrol.exception;

public class NegativeStockException extends RuntimeException {
    public NegativeStockException(String message) {
        super(message);
    }
}
