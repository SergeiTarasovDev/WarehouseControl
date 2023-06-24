package ru.teliontech.warehousecontrol.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.teliontech.warehousecontrol.dto.SockDto;
import ru.teliontech.warehousecontrol.entity.OperationType;
import ru.teliontech.warehousecontrol.entity.Sock;
import ru.teliontech.warehousecontrol.entity.TradingAction;
import ru.teliontech.warehousecontrol.exception.DuplicateSocksException;
import ru.teliontech.warehousecontrol.exception.NegativeStockException;
import ru.teliontech.warehousecontrol.repository.SockRepository;
import ru.teliontech.warehousecontrol.repository.TradingActionRepository;
import ru.teliontech.warehousecontrol.utils.MappingUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SockService {
    public static final Logger LOGGER = LoggerFactory.getLogger(SockService.class);
    public static final String MORE = "moreThan";
    public static final String LESS = "lessThan";

    private final SockRepository sockRepository;
    private final TradingActionRepository tradingActionRepository;
    private final MappingUtils mappingUtils;

    public SockService(SockRepository sockRepository, TradingActionRepository tradingActionRepository, MappingUtils mappingUtils) {
        this.sockRepository = sockRepository;
        this.tradingActionRepository = tradingActionRepository;
        this.mappingUtils = mappingUtils;
    }

    public List<SockDto> getAllSocks() {
        List<Sock> list = sockRepository.findAll();
        return list.stream().map(mappingUtils::mapToSockDto).toList();
    }

    public Integer getCountSocksWithParams(String color, String operation, Integer cottonPart) {
        if (cottonPart < 0 || cottonPart > 100) {
            return null;
        }

        switch (operation) {
            case MORE -> {
                List<Sock> foundSocks = sockRepository.findAllByColorAndCottonPartGreaterThan(color, cottonPart);
                if (null != foundSocks) {
                    return sockRepository.getStockSumByColorAndCottonPartGreaterThan(color, cottonPart);
                }
            }
            case LESS -> {
                List<Sock> foundSocks = sockRepository.findAllByColorAndCottonPartLessThan(color, cottonPart);
                if (null != foundSocks) {
                    return sockRepository.getStockSumByColorAndCottonPartLessThan(color, cottonPart);
                }
            }
        }
        return null;
    }

    public SockDto income(SockDto inputSock) {
        if (isCottonPartInvalid(inputSock) || inputSock.getQuantity() < 0) {
            return null;
        }
        return handleOperation(inputSock, OperationType.INCOME);
    }

    public SockDto outcome(SockDto inputSock) {
        if (isCottonPartInvalid(inputSock) || inputSock.getQuantity() < 0) {
            return null;
        }
        return handleOperation(inputSock, OperationType.OUTCOME);
    }

    public SockDto handleOperation(SockDto inputSock, OperationType operationType) {
        Sock sock;
        try {
            sock = findSock(inputSock);
        } catch (DuplicateSocksException e) {
            LOGGER.info(e.getMessage());
            return null;
        }

        try {
            calculateStock(inputSock, sock, operationType);
        } catch (NegativeStockException e) {
            LOGGER.info(e.getMessage());
            return null;
        }

        sockRepository.save(sock);
        tradingActionRepository.save(createTradingAction(sock, inputSock.getQuantity(), operationType));
        return mappingUtils.mapToSockDto(sock, inputSock.getQuantity());
    }

    private void calculateStock(SockDto inputSock, Sock sock, OperationType operationType) throws NegativeStockException {
        if (operationType.equals(OperationType.INCOME)) {
            sock.setStock(sock.getStock() + inputSock.getQuantity());
        } else {
            if (inputSock.getQuantity() > sock.getStock()) {
                throw new NegativeStockException("Quantity is bigger than stock");
            }
            sock.setStock(sock.getStock() - inputSock.getQuantity());
        }
    }

    private Sock findSock(SockDto inputSock) throws DuplicateSocksException {
        List<Sock> socks = sockRepository.findByColorAndCottonPart(inputSock.getColor(), inputSock.getCottonPart());
        if (socks.size() > 1) {
            throw new DuplicateSocksException("Database include a few duplicate socks with current parameters");
        } else if (socks.size() == 0) {
            return mappingUtils.mapToSock(inputSock);
        }
        return socks.get(0);
    }

    private TradingAction createTradingAction(Sock sock, int quantity, OperationType operationType) {
        TradingAction tradingAction = new TradingAction();
        tradingAction.setOperationTime(LocalDateTime.now());
        tradingAction.setOperationType(operationType);
        tradingAction.setQuantity(quantity);
        tradingAction.setSock(sock);
        return tradingAction;
    }

    public SockDto createSock(SockDto sockDto) {
        if (isCottonPartInvalid(sockDto)) {
            return null;
        }

        Optional<Sock> sock = sockRepository.findById(sockDto.getId());
        if (sock.isPresent()) {
            return null;
        }

        sockRepository.save(mappingUtils.mapToSock(sockDto));
        return sockDto;
    }

    public SockDto updateSock(SockDto sockDto) {
        if (isCottonPartInvalid(sockDto)) {
            return null;
        }
        Optional<Sock> foundSock = findSockById(sockDto.getId());
        if (foundSock.isPresent()) {
            sockRepository.save(mappingUtils.mapToSock((sockDto)));
            return sockDto;
        }
        return null;
    }

    private boolean isCottonPartInvalid(SockDto sockDto) {
        int cottonPart = sockDto.getCottonPart();
        return cottonPart < 0 || cottonPart > 100;
    }

    public SockDto deleteSock(long id) {
        Optional<Sock> foundSock = findSockById(id);
        if (foundSock.isPresent()) {
            sockRepository.delete(foundSock.get());
            return mappingUtils.mapToSockDto(foundSock.get());
        }
        return null;
    }

    private Optional<Sock> findSockById(long id) {
        return sockRepository.findById(id);
    }


}