package ru.teliontech.warehousecontrol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SockDto {
    private Long id;
    private String color;
    private int cottonPart;
    private int stock;
}