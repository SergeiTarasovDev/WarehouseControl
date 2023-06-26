package ru.teliontech.warehousecontrol.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.teliontech.warehousecontrol.dto.SockDto;
import ru.teliontech.warehousecontrol.dto.SockQntDto;
import ru.teliontech.warehousecontrol.entity.OperationType;
import ru.teliontech.warehousecontrol.entity.Sock;
import ru.teliontech.warehousecontrol.entity.TradingAction;
import ru.teliontech.warehousecontrol.exception.DuplicateSocksException;
import ru.teliontech.warehousecontrol.exception.EntityNotFoundException;
import ru.teliontech.warehousecontrol.exception.InvalidArgumentException;
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
    public static final String EXCMSG_NOT_FOUND = "Entity not found";
    public static final String EXCMSG_FOUND = "An entry with this id already exists";
    public static final String EXCMSG_QUANTITY_BIGGER_THAN_STOCK = "Quantity is bigger than stock";
    public static final String EXCMSG_DUPLICATE_ENTITY = "Database include a few duplicate socks with current parameters";
    public static final String EXCMSG_COTTONPART_NOT_RANGE = "The value of the cottonPart field does not fall within the range from 0 to 100";
    public static final String EXCMSG_QUANTITY_LESS_THAN_ZERO = "The value of quantity field is less than 0";

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
                if (!foundSocks.isEmpty()) {
                    return Optional.of(sockRepository.getStockSumByColorAndCottonPartGreaterThan(color, cottonPart));
                }
            }
            case LESS -> {
                List<Sock> foundSocks = sockRepository.findAllByColorAndCottonPartLessThan(color, cottonPart);
                if (!foundSocks.isEmpty()) {
                    return Optional.of(sockRepository.getStockSumByColorAndCottonPartLessThan(color, cottonPart));
                }
            }
        }
        throw new EntityNotFoundException(EXCMSG_NOT_FOUND);
    }

    public Optional<SockQntDto> income(SockQntDto inputSock) {
        return handleOperation(inputSock, OperationType.INCOME);
    }

    public Optional<SockQntDto> outcome(SockQntDto inputSock) {
        return handleOperation(inputSock, OperationType.OUTCOME);
    }

    private Optional<SockQntDto> handleOperation(SockQntDto inputSock, OperationType operationType) {
        validateCottonPart(mappingUtils.mapToSock(inputSock));
        validateQuantity(inputSock);

        Sock foundSock = findSock(inputSock);
        calculateStock(inputSock, foundSock, operationType);

        sockRepository.save(foundSock);
        tradingActionRepository.save(createTradingAction(foundSock, inputSock.getQuantity(), operationType));
        return Optional.ofNullable(mappingUtils.mapToSockQntDto(foundSock, inputSock.getQuantity()));
    }

    private Sock findSock(SockQntDto inputSock) {
        List<Sock> socks = sockRepository.findByColorAndCottonPart(inputSock.getColor(), inputSock.getCottonPart());
        if (socks.size() > 1) {
            throw new DuplicateSocksException(EXCMSG_DUPLICATE_ENTITY);
        } else if (socks.size() == 0) {
            throw new EntityNotFoundException(EXCMSG_NOT_FOUND);
        }
        return socks.get(0);
    }

    private void calculateStock(SockQntDto inputSock, Sock foundSock, OperationType operationType) {
        if (operationType.equals(OperationType.INCOME)) {
            foundSock.setStock(foundSock.getStock() + inputSock.getQuantity());
        } else {
            if (inputSock.getQuantity() > foundSock.getStock()) {
                throw new NegativeStockException(EXCMSG_QUANTITY_BIGGER_THAN_STOCK);
            }
            foundSock.setStock(foundSock.getStock() - inputSock.getQuantity());
        }
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
        validateCottonPart(mappingUtils.mapToSock(sockDto));
        validateEntryExists(sockDto.getId());
        sockRepository.save(mappingUtils.mapToSock(sockDto));
        return Optional.of(sockDto);
    }

    public Optional<SockDto> updateSock(SockDto sockDto) {
        validateCottonPart(mappingUtils.mapToSock(sockDto));
        validateEntryNonExists(sockDto.getId());
        sockRepository.save(mappingUtils.mapToSock((sockDto)));
        return Optional.of(sockDto);
    }

    public Optional<SockDto> deleteSock(Long id) {
        Sock foundSock = findSockById(id).orElseThrow(() -> new EntityNotFoundException(EXCMSG_NOT_FOUND));
        sockRepository.delete(foundSock);
        return Optional.of(mappingUtils.mapToSockDto(foundSock));
    }

    private Optional<Sock> findSockById(Long id) {
        return sockRepository.findById(id);
    }

    private void validateCottonPart(Sock sock) {
        validateCottonPart(sock.getCottonPart());
    }

    private void validateCottonPart(Integer cottonPart) {
        if (cottonPart < 0 || cottonPart > 100) {
            throw new InvalidArgumentException(EXCMSG_COTTONPART_NOT_RANGE);
        }
    }
    private void validateQuantity(SockQntDto inputSock) {
        if (inputSock.getQuantity() < 0) {
            throw new InvalidArgumentException(EXCMSG_QUANTITY_LESS_THAN_ZERO);
        }
    }

    private void validateEntryExists(long id) {
        Optional<Sock> sock = sockRepository.findById(id);
        if (sock.isPresent()) {
            throw new InvalidArgumentException(EXCMSG_FOUND);
        }
    }

    private void validateEntryNonExists(Long id) {
        Optional<Sock> sock = sockRepository.findById(id);
        if (sock.isEmpty()) {
            throw new InvalidArgumentException(EXCMSG_NOT_FOUND);
        }
    }
}