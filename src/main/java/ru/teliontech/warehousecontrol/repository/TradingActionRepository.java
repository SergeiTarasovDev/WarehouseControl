package ru.teliontech.warehousecontrol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.teliontech.warehousecontrol.entity.TradingAction;

public interface TradingActionRepository extends JpaRepository<TradingAction, Long> {
}
