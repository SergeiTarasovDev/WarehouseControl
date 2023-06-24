package ru.teliontech.warehousecontrol.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.teliontech.warehousecontrol.dto.SockDto;
import ru.teliontech.warehousecontrol.dto.SockQntDto;
import ru.teliontech.warehousecontrol.service.SockService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/socks")
public class SockController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SockController.class);
    private static final String EXCMSG_UNEXPECTED_VALUE = "Unexpected value";

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
        return ResponseEntity.ok(sockService.getCountSocksWithParams(color, operation, cottonPart).orElseThrow(() -> new IllegalArgumentException(EXCMSG_UNEXPECTED_VALUE)));
    }

    @PostMapping("/income")
    public ResponseEntity<SockQntDto> incomeSock(@RequestBody SockQntDto sockQntDto) {
        return ResponseEntity.ok(sockService.income(sockQntDto).orElseThrow(() -> new IllegalArgumentException(EXCMSG_UNEXPECTED_VALUE)));
    }

    @PostMapping("/outcome")
    public ResponseEntity<SockQntDto> outcomeSock(@RequestBody SockQntDto sockQntDto) {
        return ResponseEntity.ok(sockService.outcome(sockQntDto).orElseThrow(() -> new IllegalArgumentException(EXCMSG_UNEXPECTED_VALUE)));
    }

    @PostMapping()
    public ResponseEntity<SockDto> createSock(@RequestBody SockDto sockDto) {
        return ResponseEntity.ok(sockService.createSock(sockDto).orElseThrow(() -> new IllegalArgumentException(EXCMSG_UNEXPECTED_VALUE)));
    }

    @PatchMapping()
    public ResponseEntity<SockDto> updateSock(@RequestBody SockDto sockDto) {
        return ResponseEntity.ok(sockService.updateSock(sockDto).orElseThrow(() -> new IllegalArgumentException(EXCMSG_UNEXPECTED_VALUE)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SockDto> deleteSock(@PathVariable long id) {
        return ResponseEntity.ok(sockService.deleteSock(id).orElseThrow(() -> new IllegalArgumentException(EXCMSG_UNEXPECTED_VALUE)));
    }
}

