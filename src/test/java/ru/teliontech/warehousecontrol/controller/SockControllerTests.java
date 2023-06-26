package ru.teliontech.warehousecontrol.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.teliontech.warehousecontrol.dto.SockDto;
import ru.teliontech.warehousecontrol.dto.SockQntDto;
import ru.teliontech.warehousecontrol.entity.Sock;
import ru.teliontech.warehousecontrol.repository.SockRepository;
import ru.teliontech.warehousecontrol.repository.TradingActionRepository;
import ru.teliontech.warehousecontrol.service.SockService;
import ru.teliontech.warehousecontrol.utils.MappingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static ru.teliontech.warehousecontrol.service.SockService.LESS;
import static ru.teliontech.warehousecontrol.service.SockService.MORE;

@WebMvcTest(controllers = SockControllerTests.class)
public class SockControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SockRepository sockRepository;

    @MockBean
    private TradingActionRepository tradingActionRepository;

    @SpyBean
    private SockService sockService;

    @SpyBean
    private MappingUtils mappingUtils;

    @InjectMocks
    private SockController sockController;

    private final String path = "/api/socks";

    @Test
    public void testFindAllSocks() throws Exception {
        List<Sock> socks = new ArrayList<>(List.of(
                new Sock(1L, "white", 50, 10),
                new Sock(2L, "white", 50, 7),
                new Sock(3L, "red", 30, 5),
                new Sock(4L, "white", 30, 1)
        ));
        when(sockRepository.findAll()).thenReturn(socks);
        mockMvc.perform(get(path + "/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCountSocksWithParams_WhiteMoreThan40() throws Exception {
        String color = "white";
        int cottonPart = 40;

        List<Sock> socks = new ArrayList<>(List.of(
                new Sock(1L, color, 50, 10),
                new Sock(2L, color, 20, 5),
                new Sock(3L, color, 10, 3),
                new Sock(4L, color, 50, 7)
        ));
        Integer expectedSum = socks.stream()
                .filter(sock -> sock.getCottonPart() > cottonPart)
                .mapToInt(Sock::getStock)
                .sum();

        when(sockRepository.findAllByColorAndCottonPartGreaterThan(color, cottonPart)).thenReturn(socks);
        when(sockRepository.getStockSumByColorAndCottonPartGreaterThan(color, cottonPart)).thenReturn(expectedSum);
        String response = mockMvc.perform(MockMvcRequestBuilders
                        .get(path)
                        .queryParam("color", color)
                        .queryParam("operation", MORE)
                        .queryParam("cottonPart", String.valueOf(cottonPart))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Integer actual = new ObjectMapper().readValue(response, Integer.class);
        assertEquals(expectedSum, actual);
    }

    @Test
    public void testGetCountSocksWithParams_WhiteLessThan40() throws Exception {
        String color = "white";
        int cottonPart = 40;

        List<Sock> socks = new ArrayList<>(List.of(
                new Sock(1L, color, 50, 10),
                new Sock(2L, color, 20, 5),
                new Sock(3L, color, 10, 3),
                new Sock(4L, color, 50, 7)
        ));
        Integer expectedSum = socks.stream()
                .filter(sock -> sock.getCottonPart() < cottonPart)
                .mapToInt(Sock::getStock)
                .sum();

        when(sockRepository.findAllByColorAndCottonPartLessThan(color, cottonPart)).thenReturn(socks);
        when(sockRepository.getStockSumByColorAndCottonPartLessThan(color, cottonPart)).thenReturn(expectedSum);

        String response = mockMvc.perform(MockMvcRequestBuilders
                        .get(path)
                        .queryParam("color", color)
                        .queryParam("operation", LESS)
                        .queryParam("cottonPart", String.valueOf(cottonPart))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Integer actual = new ObjectMapper().readValue(response, Integer.class);
        assertEquals(expectedSum, actual);
    }

    @Test
    public void testIncomeSock_Positive() throws Exception {
        SockQntDto sockQntDto = new SockQntDto(1L, "white", 40, 11, 0);
        int expectedStock = sockQntDto.getStock() + sockQntDto.getQuantity();
        Sock sock = mappingUtils.mapToSock(sockQntDto);
        List<Sock> socks = new ArrayList<>(List.of(
                sock
        ));

        JSONObject sockObj = new JSONObject();
        sockObj.put("id", sockQntDto.getId());
        sockObj.put("color", sockQntDto.getColor());
        sockObj.put("cottonPart", sockQntDto.getCottonPart());
        sockObj.put("quantity", sockQntDto.getQuantity());
        sockObj.put("stock", sockQntDto.getStock());

        when(sockRepository.findByColorAndCottonPart(sockQntDto.getColor(), sockQntDto.getCottonPart())).thenReturn(socks);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch(path + "/income")
                        .content(sockObj.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sockQntDto.getId()))
                .andExpect(jsonPath("$.color").value(sockQntDto.getColor()))
                .andExpect(jsonPath("$.cottonPart").value(sockQntDto.getCottonPart()))
                .andExpect(jsonPath("$.quantity").value(sockQntDto.getQuantity()))
                .andExpect(jsonPath("$.stock").value(expectedStock))
        ;
    }

    @Test
    public void testOutcomeSock_Positive() throws Exception {
        SockQntDto sockQntDto = new SockQntDto(1L, "white", 40, 11, 20);
        int expectedStock = sockQntDto.getStock() - sockQntDto.getQuantity();
        Sock sock = mappingUtils.mapToSock(sockQntDto);
        List<Sock> socks = new ArrayList<>(List.of(
                sock
        ));

        JSONObject sockObj = new JSONObject();
        sockObj.put("id", sockQntDto.getId());
        sockObj.put("color", sockQntDto.getColor());
        sockObj.put("cottonPart", sockQntDto.getCottonPart());
        sockObj.put("quantity", sockQntDto.getQuantity());
        sockObj.put("stock", sockQntDto.getStock());

        when(sockRepository.findByColorAndCottonPart(sockQntDto.getColor(), sockQntDto.getCottonPart())).thenReturn(socks);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch(path + "/outcome")
                        .content(sockObj.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sockQntDto.getId()))
                .andExpect(jsonPath("$.color").value(sockQntDto.getColor()))
                .andExpect(jsonPath("$.cottonPart").value(sockQntDto.getCottonPart()))
                .andExpect(jsonPath("$.quantity").value(sockQntDto.getQuantity()))
                .andExpect(jsonPath("$.stock").value(expectedStock))
        ;
    }

    @Test
    public void testCreateSock_Positive() throws Exception {
        SockDto sockDto = new SockDto(1L, "white", 40, 20);
        JSONObject sockObj = getJsonObject(sockDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(path)
                        .content(sockObj.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sockDto.getId()))
                .andExpect(jsonPath("$.color").value(sockDto.getColor()))
                .andExpect(jsonPath("$.cottonPart").value(sockDto.getCottonPart()))
                .andExpect(jsonPath("$.stock").value(sockDto.getStock()))
        ;
    }

    @Test
    public void testUpdateSock_Positive() throws Exception {
        SockDto sockDto = new SockDto(1L, "white", 40, 20);

        JSONObject sockObj = new JSONObject();
        sockObj.put("id", sockDto.getId());
        sockObj.put("color", sockDto.getColor());
        sockObj.put("cottonPart", sockDto.getCottonPart());
        sockObj.put("stock", sockDto.getStock());

        Optional<Sock> sock = Optional.of(mappingUtils.mapToSock(sockDto));
        when(sockRepository.findById(sockDto.getId())).thenReturn(sock);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch(path)
                        .content(sockObj.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sockDto.getId()))
                .andExpect(jsonPath("$.color").value(sockDto.getColor()))
                .andExpect(jsonPath("$.cottonPart").value(sockDto.getCottonPart()))
                .andExpect(jsonPath("$.stock").value(sockDto.getStock()))
        ;
    }

    @Test
    public void testDeleteSock_Positive() throws Exception {
        String color = "white";
        List<Sock> expectedSocks = new ArrayList<>(List.of(
                new Sock(1L, color, 50, 10),
                new Sock(2L, color, 20, 5),
                new Sock(3L, color, 10, 3),
                new Sock(4L, color, 50, 7)
        ));
        SockDto sockDto = mappingUtils.mapToSockDto(expectedSocks.get(0));
        Sock sock = mappingUtils.mapToSock(sockDto);
        Optional<Sock> optionalSock = Optional.of(sock);

        when(sockRepository.findById(any(Long.class))).thenReturn(optionalSock);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(path + "/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        ;
    }

    private JSONObject getJsonObject(SockDto sockDto) throws JSONException {
        JSONObject sockObj = new JSONObject();
        sockObj.put("id", sockDto.getId());
        sockObj.put("color", sockDto.getColor());
        sockObj.put("cottonPart", sockDto.getCottonPart());
        sockObj.put("stock", sockDto.getStock());
        return sockObj;
    }

}