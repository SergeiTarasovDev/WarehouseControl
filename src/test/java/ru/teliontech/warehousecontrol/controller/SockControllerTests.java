package ru.teliontech.warehousecontrol.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.teliontech.warehousecontrol.entity.Sock;
import ru.teliontech.warehousecontrol.repository.SockRepository;
import ru.teliontech.warehousecontrol.repository.TradingActionRepository;
import ru.teliontech.warehousecontrol.service.SockService;
import ru.teliontech.warehousecontrol.utils.MappingUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
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

    @Test
    public void testFindAllSocks() throws Exception {
        List<Sock> socks = new ArrayList<>(List.of(
                new Sock(1L, "white", 50, 10),
                new Sock(2L, "white", 50, 7),
                new Sock(3L, "red", 30, 5),
                new Sock(4L, "white", 30, 1)
        ));
        when(sockRepository.findAll()).thenReturn(socks);
        mockMvc.perform(get("/api/socks/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();
    }

    @Test
    public void testGetCountSocksWithParams() throws Exception {
        List<Sock> socks = new ArrayList<>(List.of(
                new Sock(1L, "white", 50, 10),
                new Sock(2L, "white", 50, 7)
        ));

        when(sockRepository.findAllByColorAndCottonPartGreaterThan("white", 40)).thenReturn(socks);
        String response = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/socks")
                        .queryParam("color", "white")
                        .queryParam("operation", "moreThan")
                        .queryParam("cottonPart", "40")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Integer actual = new ObjectMapper().readValue(response, Integer.class);
        assertEquals(17, actual);
    }
}