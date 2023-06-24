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

import javax.persistence.EntityNotFoundException;
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

    public Optional<Integer> getCountSocksWithParams(String color, String operation, Integer cottonPart) {
        validateCottonPart(cottonPart);

        switch (operation) {
            case MORE -> {
                List<Sock> foundSocks = sockRepository.findAllByColorAndCottonPartGreaterThan(color, cottonPart);
                if (null != foundSocks) {
                    return Optional.of(sockRepository.getStockSumByColorAndCottonPartGreaterThan(color, cottonPart));
                }
            }
            case LESS -> {
                List<Sock> foundSocks = sockRepository.findAllByColorAndCottonPartLessThan(color, cottonPart);
                if (null != foundSocks) {
                    return Optional.of(sockRepository.getStockSumByColorAndCottonPartLessThan(color, cottonPart));
                }
            }
        }
        throw new EntityNotFoundException("Entity not found");
    }

    public Optional<SockDto> income(SockDto inputSock) {
        validateCottonPart(inputSock);
        validateQuantity(inputSock);
        return handleOperation(inputSock, OperationType.INCOME);
    }

    public Optional<SockDto> outcome(SockDto inputSock) {
        validateCottonPart(inputSock);
        validateQuantity(inputSock);
        return handleOperation(inputSock, OperationType.OUTCOME);
    }

    public Optional<SockDto> handleOperation(SockDto inputSock, OperationType operationType) {
        Sock foundSock = findSock(inputSock);
        calculateStock(inputSock, foundSock, operationType);

        sockRepository.save(foundSock);
        tradingActionRepository.save(createTradingAction(foundSock, inputSock.getQuantity(), operationType));
        return Optional.ofNullable(mappingUtils.mapToSockDto(foundSock, inputSock.getQuantity()));
    }

    private void calculateStock(SockDto inputSock, Sock foundSock, OperationType operationType) {
        if (operationType.equals(OperationType.INCOME)) {
            foundSock.setStock(foundSock.getStock() + inputSock.getQuantity());
        } else {
            if (inputSock.getQuantity() > foundSock.getStock()) {
                throw new NegativeStockException("Quantity is bigger than stock");
            }
            foundSock.setStock(foundSock.getStock() - inputSock.getQuantity());
        }
    }

    private Sock findSock(SockDto inputSock) {
        List<Sock> socks = sockRepository.findByColorAndCottonPart(inputSock.getColor(), inputSock.getCottonPart());
        if (socks.size() > 1) {
            throw new DuplicateSocksException("Database include a few duplicate socks with current parameters");
        } else if (socks.size() == 0) {
            return mappingUtils.mapToSock(inputSock);
        }
        return socks.get(0);
    }

    private TradingAction createTradingAction(Sock foundSock, int quantity, OperationType operationType) {
        TradingAction tradingAction = new TradingAction();
        tradingAction.setOperationTime(LocalDateTime.now());
        tradingAction.setOperationType(operationType);
        tradingAction.setQuantity(quantity);
        tradingAction.setSock(foundSock);
        return tradingAction;
    }

    public Optional<SockDto> createSock(SockDto sockDto) {
        validateCottonPart(sockDto);
        validateEntryExists(sockDto.getId());
        sockRepository.save(mappingUtils.mapToSock(sockDto));
        return Optional.of(sockDto);
    }

    public Optional<SockDto> updateSock(SockDto sockDto) {
        validateCottonPart(sockDto);
        validateEntryNonExists(sockDto.getId());
        sockRepository.save(mappingUtils.mapToSock((sockDto)));
        return Optional.of(sockDto);
    }

    public Optional<SockDto> deleteSock(long id) {
        Sock foundSock = findSockById(id).orElseThrow(() -> new IllegalArgumentException("No record found with this ID"));
        sockRepository.delete(foundSock);
        return Optional.of(mappingUtils.mapToSockDto(foundSock));
    }

    private Optional<Sock> findSockById(long id) {
        return sockRepository.findById(id);
    }

    private void validateCottonPart(SockDto sockDto) {
        validateCottonPart(sockDto.getCottonPart());
    }

    private void validateCottonPart(Integer cottonPart) {
        if (cottonPart < 0 || cottonPart > 100) {
            throw new IllegalArgumentException("The value of the cottonPart field does not fall within the range from 0 to 100");
        }
    }
    private void validateQuantity(SockDto inputSock) {
        if (inputSock.getQuantity() < 0) {
            throw new IllegalArgumentException("The value of quantity field is less than 0");
        }
    }

    private void validateEntryExists(long id) {
        Optional<Sock> sock = sockRepository.findById(id);
        if (sock.isPresent()) {
            throw new IllegalArgumentException("An entry with this id already exists");
        }
    }

    private void validateEntryNonExists(Long id) {
        Optional<Sock> sock = sockRepository.findById(id);
        if (sock.isEmpty()) {
            throw new IllegalArgumentException("No record found with this ID");
        }
    }
}