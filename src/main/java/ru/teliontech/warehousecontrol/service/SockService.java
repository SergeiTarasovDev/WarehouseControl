package ru.teliontech.warehousecontrol.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.teliontech.warehousecontrol.entity.OperationType;
import ru.teliontech.warehousecontrol.entity.Sock;
import ru.teliontech.warehousecontrol.entity.TradingAction;
import ru.teliontech.warehousecontrol.repository.SockRepository;
import ru.teliontech.warehousecontrol.repository.TradingActionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SockService {
    Logger LOGGER = LoggerFactory.getLogger(SockService.class);

    private final SockRepository sockRepository;
    private final TradingActionRepository tradingActionRepository;

    public SockService(SockRepository sockRepository, TradingActionRepository tradingActionRepository) {
        this.sockRepository = sockRepository;
        this.tradingActionRepository = tradingActionRepository;
    }

    public List<Sock> getAllSocks() {
        return sockRepository.findAll();
    }



    public Sock saveTables(Sock inputSock, OperationType operationType) {
        Sock sock = findSock(inputSock, operationType);
        sockRepository.save(sock);
        tradingActionRepository.save(createTradingAction(inputSock, operationType));
        return sock;
    }

    private TradingAction createTradingAction(Sock inputSock, OperationType operationType) {
        TradingAction tradingAction = new TradingAction();
        tradingAction.setOperationTime(LocalDateTime.now());
        tradingAction.setOperationType(operationType);
        tradingAction.setSock(inputSock);
        tradingAction.setQuantity(inputSock.getStock());
        return tradingAction;
    }

    private Sock findSock(Sock inputSock, OperationType operationType) {
        Sock sock = sockRepository.findFirstByColorAndCottonPart(inputSock.getColor(), inputSock.getCottonPart());
        if (null == sock) {
            return inputSock;
        } else {
            if (operationType.equals(OperationType.INCOME)) {
                sock.setStock(sock.getStock() + inputSock.getStock());
            } else {
                sock.setStock(sock.getStock() - inputSock.getStock());
            }
        }
        return sock;
    }

    public Sock income(Sock inputSock) {
        return saveTables(inputSock, OperationType.INCOME);
    }

    public Sock outcome(Sock inputSock) {
        return saveTables(inputSock, OperationType.OUTCOME);
    }
}