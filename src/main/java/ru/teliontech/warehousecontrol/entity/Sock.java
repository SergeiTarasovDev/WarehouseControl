package ru.teliontech.warehousecontrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "socks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String color;
    private int cottonPart;
    private int stock;

}