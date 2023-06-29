package ru.teliontech.warehousecontrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trading_actions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradingAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    LocalDateTime operationTime;
    OperationType operationType;

    @ManyToOne
    Sock sock;

    int quantity;

}