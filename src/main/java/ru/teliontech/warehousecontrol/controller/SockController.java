package ru.teliontech.warehousecontrol.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.teliontech.warehousecontrol.dto.SockDto;
import ru.teliontech.warehousecontrol.service.SockService;

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
        Integer countSocks = sockService.getCountSocksWithParams(color, operation, cottonPart);
        if (countSocks == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(countSocks);
    }

    @PostMapping("/income")
    public ResponseEntity<SockDto> incomeSock(@RequestBody SockDto sockDto) {
        SockDto result = sockService.income(sockDto);
        if (null == result) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/outcome")
    public ResponseEntity<SockDto> outcomeSock(@RequestBody SockDto sockDto) {
        SockDto result = sockService.outcome(sockDto);
        if (null == result) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping()
    public ResponseEntity<SockDto> createSock(@RequestBody SockDto sockDto) {
        SockDto createdSock = sockService.createSock(sockDto);
        if (null == createdSock) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(createdSock);
    }

    @PatchMapping()
    public ResponseEntity<SockDto> updateSock(@RequestBody SockDto sockDto) {
        SockDto editedSock = sockService.updateSock(sockDto);
        if (null == editedSock) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(editedSock);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SockDto> deleteSock(@PathVariable long id) {
        SockDto deletedSock = sockService.deleteSock(id);
        if (null != deletedSock) {
            return ResponseEntity.ok(deletedSock);
        }

        return ResponseEntity.notFound().build();
    }
}

