package ru.teliontech.warehousecontrol.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.teliontech.warehousecontrol.dto.SockDto;
import ru.teliontech.warehousecontrol.dto.SockQntDto;
import ru.teliontech.warehousecontrol.entity.Sock;
import ru.teliontech.warehousecontrol.exception.DuplicateSocksException;
import ru.teliontech.warehousecontrol.exception.EntityNotFoundException;
import ru.teliontech.warehousecontrol.exception.InvalidArgumentException;
import ru.teliontech.warehousecontrol.exception.NegativeStockException;
import ru.teliontech.warehousecontrol.repository.SockRepository;
import ru.teliontech.warehousecontrol.repository.TradingActionRepository;
import ru.teliontech.warehousecontrol.utils.MappingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;
import static ru.teliontech.warehousecontrol.service.SockService.*;

@ExtendWith(MockitoExtension.class)
public class SockServiceTests {

    @Mock
    private SockRepository sockRepository;

    @Spy
    private MappingUtils mappingUtils;

    @InjectMocks
    private SockService sockService;

    @Test
    public void testGetAllSocks_Positive() {
        List<Sock> sockList = getSockList();
        List<SockDto> expectedSockDtoList = convertSockListToSockDtoList(sockList);
        when(sockRepository.findAll()).thenReturn(sockList);
        List<SockDto> actualSockDtoList = sockService.getAllSocks();
        assertThat(actualSockDtoList)
                .containsExactlyInAnyOrderElementsOf(expectedSockDtoList);
    }

    @Test
    public void testGetCountSocksWithParams_Positive_OperationIsMore() {
        String color = "white";
        int cottonPartGreaterThan = 40;
        List<Sock> sockList = getSockList();
        Integer expectedSum = sockList.stream()
                .filter(sock -> sock.getColor().equals(color) && sock.getCottonPart() > cottonPartGreaterThan)
                .mapToInt(Sock::getStock)
                .sum();
        when(sockRepository.findAllByColorAndCottonPartGreaterThan(color, cottonPartGreaterThan)).thenReturn(sockList);
        when(sockRepository.getStockSumByColorAndCottonPartGreaterThan(color, cottonPartGreaterThan)).thenReturn(expectedSum);
        Integer actualSum = sockService.getCountSocksWithParams(color, MORE, cottonPartGreaterThan).orElse(null);
        assertThat(actualSum).isEqualTo(expectedSum);
    }

    @Test
    public void testGetCountSocksWithParams_Positive_OperationIsLess() {
        String color = "white";
        int cottonPartLessThan = 60;
        List<Sock> sockList = getSockList();
        Integer expectedSum = sockList.stream()
                .filter(sock -> sock.getColor().equals(color) && sock.getCottonPart() < cottonPartLessThan)
                .mapToInt(Sock::getStock)
                .sum();
        when(sockRepository.findAllByColorAndCottonPartLessThan(color, cottonPartLessThan)).thenReturn(sockList);
        when(sockRepository.getStockSumByColorAndCottonPartLessThan(color, cottonPartLessThan)).thenReturn(expectedSum);
        Integer actualSum = sockService.getCountSocksWithParams(color, LESS, cottonPartLessThan).orElse(null);
        assertThat(actualSum).isEqualTo(expectedSum);
    }

    @Test
    public void testGetCountSocksWithParams_Negative_EntityNotFound() {
        String color = "blue";
        int cottonPartLessThan = 60;
        Throwable thrown = catchThrowable(() -> sockService.getCountSocksWithParams(color, LESS, cottonPartLessThan));
        assertThat(thrown)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(EXCMSG_NOT_FOUND);
    }

    @Test
    public void testGetCountSocksWithParams_Negative_UndefinedOperation() {
        String color = "blue";
        int cottonPartLessThan = 60;
        Throwable thrown = catchThrowable(() -> sockService.getCountSocksWithParams(color, "undefined", cottonPartLessThan));
        assertThat(thrown)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(EXCMSG_NOT_FOUND);
    }

    @Test
    public void testGetCountSockWithParams_Negative_CottonPartOverRange() {
        String color = "white";
        int cottonPartGreaterThan = 120;
        Throwable thrown = catchThrowable(() -> sockService.getCountSocksWithParams(color, MORE, cottonPartGreaterThan));
        assertThat(thrown)
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining(EXCMSG_COTTONPART_NOT_RANGE);
    }

    @Test
    public void testGetCountSockWithParams_Negative_CottonPartLessThanRange() {
        String color = "white";
        int cottonPartGreaterThan = -9990;
        Throwable thrown = catchThrowable(() -> sockService.getCountSocksWithParams(color, MORE, cottonPartGreaterThan));
        assertThat(thrown)
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining(EXCMSG_COTTONPART_NOT_RANGE);
    }

    @Test
    public void testIncome_Positive() {
        Sock sock = getSockList().get(0);
        List<Sock> socks = new ArrayList<>(List.of(sock));
        SockQntDto sockQntDto = mappingUtils.mapToSockQntDto(sock, 7);
        SockQntDto expected = new SockQntDto();
        expected.setId(sock.getId());
        expected.setColor(sock.getColor());
        expected.setCottonPart(sock.getCottonPart());
        expected.setQuantity(sockQntDto.getQuantity());
        expected.setStock(sock.getStock() + sockQntDto.getQuantity());
        when(sockRepository.findByColorAndCottonPart(sock.getColor(), sock.getCottonPart())).thenReturn(socks);
        SockQntDto actual = sockService.income(sockQntDto).orElseThrow();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testOutcome_Positive() {
        Sock sock = getSockList().get(0);
        List<Sock> socks = new ArrayList<>(List.of(sock));
        SockQntDto sockQntDto = mappingUtils.mapToSockQntDto(sock, 7);
        SockQntDto expected = new SockQntDto();
        expected.setId(sock.getId());
        expected.setColor(sock.getColor());
        expected.setCottonPart(sock.getCottonPart());
        expected.setQuantity(sockQntDto.getQuantity());
        expected.setStock(sock.getStock() - sockQntDto.getQuantity());
        when(sockRepository.findByColorAndCottonPart(sock.getColor(), sock.getCottonPart())).thenReturn(socks);
        SockQntDto actual = sockService.outcome(sockQntDto).orElseThrow();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testOutcome_Negative_DuplicateSocks() {
        Sock sock = getSockList().get(0);
        List<Sock> socks = new ArrayList<>(List.of(sock, getSockList().get(1)));
        SockQntDto sockQntDto = mappingUtils.mapToSockQntDto(sock, 7);
        when(sockRepository.findByColorAndCottonPart(sock.getColor(), sock.getCottonPart())).thenReturn(socks);
        Throwable thrown = catchThrowable(() -> sockService.income(sockQntDto));
        assertThat(thrown)
                .isInstanceOf(DuplicateSocksException.class)
                .hasMessageContaining(EXCMSG_DUPLICATE_ENTITY);
    }

    @Test
    public void testOutcome_Negative_SockNotFound() {
        Sock sock = getSockList().get(0);
        List<Sock> socks = new ArrayList<>();
        SockQntDto sockQntDto = mappingUtils.mapToSockQntDto(sock, 7);
        when(sockRepository.findByColorAndCottonPart(sock.getColor(), sock.getCottonPart())).thenReturn(socks);
        Throwable thrown = catchThrowable(() -> sockService.income(sockQntDto));
        assertThat(thrown)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(EXCMSG_NOT_FOUND);
    }

    @Test
    public void testOutcome_Negative_NegativeStock() {
        Sock sock = getSockList().get(0);
        List<Sock> socks = new ArrayList<>(List.of(sock));
        SockQntDto sockQntDto = mappingUtils.mapToSockQntDto(sock, 50);
        when(sockRepository.findByColorAndCottonPart(sock.getColor(), sock.getCottonPart())).thenReturn(socks);
        Throwable thrown = catchThrowable(() -> sockService.outcome(sockQntDto));
        assertThat(thrown)
                .isInstanceOf(NegativeStockException.class)
                .hasMessageContaining(EXCMSG_QUANTITY_BIGGER_THAN_STOCK);
    }

    @Test
    public void testOutcome_Negative_NegativeQuantity() {
        int negativeQnt = -10;
        Sock sock = getSockList().get(0);
        SockQntDto sockQntDto = mappingUtils.mapToSockQntDto(sock, negativeQnt);
        Throwable thrown = catchThrowable(() -> sockService.outcome(sockQntDto));
        assertThat(thrown)
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining(EXCMSG_QUANTITY_LESS_THAN_ZERO);
    }

    @Test
    public void testCreateSock_Positive() {
        Sock sock = getSockList().get(0);
        SockDto expected = mappingUtils.mapToSockDto(sock);
        SockDto actual = sockService.createSock(expected).orElseThrow();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testCreateSock_Negative_EntryExists() {
        Sock sock = getSockList().get(0);
        SockDto expected = mappingUtils.mapToSockDto(sock);
        when(sockRepository.findById(sock.getId())).thenReturn(Optional.of(sock));
        Throwable thrown = catchThrowable(() -> sockService.createSock(expected));
        assertThat(thrown)
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining(EXCMSG_FOUND);
    }

    @Test
    public void testUpdateSock_Positive() {
        Sock sock = getSockList().get(0);
        SockDto expected = mappingUtils.mapToSockDto(sock);
        when(sockRepository.findById(sock.getId())).thenReturn(Optional.of(sock));
        SockDto actual = sockService.updateSock(expected).orElseThrow();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testUpdateSock_Negative_EntryNonExists() {
        Sock sock = getSockList().get(0);
        SockDto expected = mappingUtils.mapToSockDto(sock);
        Throwable thrown = catchThrowable(() -> sockService.updateSock(expected));
        assertThat(thrown)
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining(EXCMSG_NOT_FOUND);
    }

    @Test
    public void testDeleteSock_Positive() {
        List<Sock> sockList = getSockList();
        Sock sock = sockList.get(0);
        SockDto expected = mappingUtils.mapToSockDto(sock);
        when(sockRepository.findById(0L)).thenReturn(Optional.of(sock));
        SockDto actual = sockService.deleteSock(0L).orElseThrow();
        assertThat(actual).isEqualTo(expected);
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