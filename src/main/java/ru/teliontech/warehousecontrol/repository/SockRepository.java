package ru.teliontech.warehousecontrol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.teliontech.warehousecontrol.entity.Sock;

import java.util.List;

public interface SockRepository extends JpaRepository<Sock, Long> {

    @Query(
            "SELECT SUM(s.stock) as sum " +
            "FROM Sock s " +
            "WHERE s.color = ?1 AND s.cottonPart > ?2 " +
            "GROUP BY s.color")
    Integer getStockSumByColorAndCottonPartGreaterThan(String color, int cottonPart);

    @Query(
            "SELECT SUM(s.stock) as sum " +
            "FROM Sock s " +
            "WHERE s.color = ?1 AND s.cottonPart < ?2 " +
            "GROUP BY s.color")
    Integer getStockSumByColorAndCottonPartLessThan(String color, int cottonPart);

    List<Sock> findAllByColorAndCottonPartGreaterThan(String color, int cottonPart);

    List<Sock> findAllByColorAndCottonPartLessThan(String color, int cottonPart);

    List<Sock> findByColorAndCottonPart(String color, int cottonPart);
}