package ru.teliontech.warehousecontrol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.teliontech.warehousecontrol.entity.Sock;

public interface SockRepository extends JpaRepository<Sock, Long> {

    public Sock findFirstByColorAndCottonPart(String color, int cottonPart);

}