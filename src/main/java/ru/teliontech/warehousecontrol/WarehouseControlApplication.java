package ru.teliontech.warehousecontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ru.teliontech")
public class WarehouseControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseControlApplication.class, args);
    }

}
