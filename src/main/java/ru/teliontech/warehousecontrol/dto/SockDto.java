package ru.teliontech.warehousecontrol.dto;

import lombok.Data;

@Data
public class SockDto {
    private Long id;
    private String color;
    private int cottonPart;
    private int quantity;
    private int stock;
}