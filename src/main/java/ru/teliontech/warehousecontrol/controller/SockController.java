package ru.teliontech.warehousecontrol.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.teliontech.warehousecontrol.entity.Sock;
import ru.teliontech.warehousecontrol.service.SockService;

import java.util.List;

@RestController
@RequestMapping("/api/socks")
public class SockController {

    private final SockService tradingActionService;

    public SockController(SockService tradingActionService) {
        this.tradingActionService = tradingActionService;
    }

    @GetMapping()
    public ResponseEntity<List<Sock>> getAllSocks() {
        return ResponseEntity.ok(tradingActionService.getAllSocks());
    }

    @PostMapping("/income")
    public ResponseEntity<Sock> incomeSock(@RequestBody Sock sock) {
        Sock result = tradingActionService.income(sock);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/outcome")
    public ResponseEntity<Sock> outcomeSock(@RequestBody Sock sock) {
        Sock result = tradingActionService.outcome(sock);

        return ResponseEntity.ok(result);
    }
}
