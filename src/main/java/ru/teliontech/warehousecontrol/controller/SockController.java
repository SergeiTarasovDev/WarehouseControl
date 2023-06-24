package ru.teliontech.warehousecontrol.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.teliontech.warehousecontrol.dto.SockDto;
import ru.teliontech.warehousecontrol.exception.DuplicateSocksException;
import ru.teliontech.warehousecontrol.exception.NegativeStockException;
import ru.teliontech.warehousecontrol.service.SockService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/socks")
public class SockController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SockController.class);

    private final SockService sockService;

    public SockController(SockService sockService) {
        this.sockService = sockService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<SockDto>> getAllSocks() {
        return ResponseEntity.ok(sockService.getAllSocks());
    }

    @GetMapping()
    public ResponseEntity<Integer> getCountSocksWithParams(
            @RequestParam String color,
            @RequestParam String operation,
            @RequestParam int cottonPart) {
        try {
            return ResponseEntity.ok(sockService.getCountSocksWithParams(color, operation, cottonPart).orElseThrow(() -> new NullPointerException("Unexpected null value")));
        } catch (IllegalArgumentException | EntityNotFoundException | NullPointerException e) {
            LOGGER.warn(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/income")
    public ResponseEntity<SockDto> incomeSock(@RequestBody SockDto sockDto) {
        try {
            return ResponseEntity.ok(sockService.income(sockDto).orElseThrow(() -> new NullPointerException("Unexpected null value")));
        } catch (IllegalArgumentException | DuplicateSocksException | NegativeStockException | NullPointerException e) {
            LOGGER.warn(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/outcome")
    public ResponseEntity<SockDto> outcomeSock(@RequestBody SockDto sockDto) {
        try {
            return ResponseEntity.ok(sockService.outcome(sockDto).orElseThrow(() -> new NullPointerException("Unexpected null value")));
        } catch (IllegalArgumentException | DuplicateSocksException | NegativeStockException | NullPointerException e) {
            LOGGER.warn(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping()
    public ResponseEntity<SockDto> createSock(@RequestBody SockDto sockDto) {
        try {
            return ResponseEntity.ok(sockService.createSock(sockDto).orElseThrow(() -> new NullPointerException("Unexpected null value")));
        } catch (IllegalArgumentException | NullPointerException e) {
            LOGGER.warn(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping()
    public ResponseEntity<SockDto> updateSock(@RequestBody SockDto sockDto) {
        try {
            return ResponseEntity.ok(sockService.updateSock(sockDto).orElseThrow(() -> new NullPointerException("Unexpected null value")));
        } catch (IllegalArgumentException | NullPointerException e) {
            LOGGER.warn(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SockDto> deleteSock(@PathVariable long id) {
        try {
            return ResponseEntity.ok(sockService.deleteSock(id).orElseThrow(() -> new NullPointerException("Unexpected null value")));
        } catch (IllegalArgumentException | NullPointerException e) {
            LOGGER.warn(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}

