package ru.teliontech.warehousecontrol.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.teliontech.warehousecontrol.dto.SockDto;
import ru.teliontech.warehousecontrol.entity.Sock;
import ru.teliontech.warehousecontrol.exception.EntityNotFoundException;
import ru.teliontech.warehousecontrol.repository.SockRepository;
import ru.teliontech.warehousecontrol.repository.TradingActionRepository;
import ru.teliontech.warehousecontrol.utils.MappingUtils;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;
import static ru.teliontech.warehousecontrol.service.SockService.*;

@ExtendWith(MockitoExtension.class)
public class SockServiceTests {

    @Mock
    private SockRepository sockRepository;

    @Mock
    private TradingActionRepository tradingActionRepository;

    @Spy
    private MappingUtils mappingUtils;

    @InjectMocks
    private SockService sockService;

    @Test
    public void testGetAllSocks() {
        List<Sock> sockList = getSockList();
        List<SockDto> expectedSockDtoList = convertSockListToSockDtoList(sockList);
        when(sockRepository.findAll()).thenReturn(sockList);
        List<SockDto> actualSockDtoList = sockService.getAllSocks();
        assertThat(actualSockDtoList)
                .containsExactlyInAnyOrderElementsOf(expectedSockDtoList);
    }

    @Test
    public void testGetCountSocksWithParams_OperationIsMore() {
        String color = "white";
        String operation = MORE;
        int cottonPartGreaterThan = 40;
        List<Sock> sockList = getSockList();
        Integer expectedSum = sockList.stream()
                .filter(sock -> sock.getColor().equals(color) && sock.getCottonPart() > cottonPartGreaterThan)
                .mapToInt(Sock::getStock)
                .sum();

        when(sockRepository.findAllByColorAndCottonPartGreaterThan(color, cottonPartGreaterThan)).thenReturn(sockList);
        when(sockRepository.getStockSumByColorAndCottonPartGreaterThan(color, cottonPartGreaterThan)).thenReturn(expectedSum);

        Integer actualSum = sockService.getCountSocksWithParams(color, operation, cottonPartGreaterThan).orElse(null);
        assertThat(actualSum).isEqualTo(expectedSum);
    }

    @Test
    public void testGetCountSocksWithParams_OperationIsLess() {
        String color = "white";
        String operation = LESS;
        int cottonPartLessThan = 60;
        List<Sock> sockList = getSockList();
        Integer expectedSum = sockList.stream()
                .filter(sock -> sock.getColor().equals(color) && sock.getCottonPart() < cottonPartLessThan)
                .mapToInt(Sock::getStock)
                .sum();

        when(sockRepository.findAllByColorAndCottonPartLessThan(color, cottonPartLessThan)).thenReturn(sockList);
        when(sockRepository.getStockSumByColorAndCottonPartLessThan(color, cottonPartLessThan)).thenReturn(expectedSum);

        Integer actualSum = sockService.getCountSocksWithParams(color, operation, cottonPartLessThan).orElse(null);
        assertThat(actualSum).isEqualTo(expectedSum);
    }

    @Test
    public void testGetCountSocksWithParams_EntityNotFound() {
        String color = "blue";
        String operation = LESS;
        int cottonPartLessThan = 60;
        List<Sock> emptyList = new ArrayList<>();

        when(sockRepository.findAllByColorAndCottonPartLessThan(color, cottonPartLessThan)).thenReturn(emptyList);

        Throwable thrown = catchThrowable(() -> sockService.getCountSocksWithParams(color, operation, cottonPartLessThan));
        assertThat(thrown)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(EXCMSG_NOT_FOUND);
    }

    @Test
    public void testGetCountSockWithParams_CottonPartIsNotRange() {

    }


    private List<Sock> getSockList() {
        return new ArrayList<>(List.of(
                new Sock(1L, "white", 50, 12),
                new Sock(2L, "white", 20, 1),
                new Sock(3L, "red", 50, 20),
                new Sock(4L, "white", 90, 7)
        ));
    }

    private List<SockDto> convertSockListToSockDtoList(List<Sock> sockList) {
        return sockList.stream()
                .map(e -> mappingUtils.mapToSockDto(e))
                .toList();
    }
}