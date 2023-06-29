package ru.teliontech.warehousecontrol.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.teliontech.warehousecontrol.dto.SockDto;
import ru.teliontech.warehousecontrol.dto.SockQntDto;
import ru.teliontech.warehousecontrol.exception.InvalidResultException;
import ru.teliontech.warehousecontrol.service.SockService;

import java.util.List;

@RestController
@RequestMapping("/api/socks")
public class SockController {
    private static final String EXCMSG_UNEXPECTED_VALUE = "Unexpected value";

    private final SockService sockService;

    public SockController(SockService sockService) {
        this.sockService = sockService;
    }

    @Operation(
            tags = "Учет носков на складе",
            summary = "Получить список товаров",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = List.class)
                            )
                    )
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<SockDto>> getAllSocks() {
        return ResponseEntity.ok(sockService.getAllSocks());
    }

    @Operation(
            tags = "Учет носков на складе",
            summary = "Возвращает общее количество носков на складе, соответствующих переданным в параметрах критериям запроса.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Integer.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content()
                    )
            }
    )
    @GetMapping()
    public ResponseEntity<Integer> getCountSocksWithParams(
            @RequestParam String color,
            @RequestParam String operation,
            @RequestParam int cottonPart) {
        return ResponseEntity.ok(sockService.getCountSocksWithParams(color, operation, cottonPart).orElseThrow(() -> new InvalidResultException(EXCMSG_UNEXPECTED_VALUE)));
    }

    @Operation(
            tags = "Учет носков на складе",
            summary = "Регистрирует приход носков на склад.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SockQntDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content()
                    )
            }
    )
    @PatchMapping("/income")
    public ResponseEntity<SockQntDto> incomeSock(@RequestBody SockQntDto sockQntDto) {
        return ResponseEntity.ok(sockService.income(sockQntDto).orElseThrow(() -> new InvalidResultException(EXCMSG_UNEXPECTED_VALUE)));
    }

    @Operation(
            tags = "Учет носков на складе",
            summary = "Регистрирует отпуск носков со склада.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SockQntDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content()
                    )
            }
    )
    @PatchMapping("/outcome")
    public ResponseEntity<SockQntDto> outcomeSock(@RequestBody SockQntDto sockQntDto) {
        return ResponseEntity.ok(sockService.outcome(sockQntDto).orElseThrow(() -> new InvalidResultException(EXCMSG_UNEXPECTED_VALUE)));
    }

    @Operation(
            tags = "Учет носков на складе",
            summary = "Добавление нового вида носков в базу данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SockDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content()
                    )
            }
    )
    @PostMapping()
    public ResponseEntity<SockDto> createSock(@RequestBody SockDto sockDto) {
        return ResponseEntity.ok(sockService.createSock(sockDto).orElseThrow(() -> new InvalidResultException(EXCMSG_UNEXPECTED_VALUE)));
    }

    @Operation(
            tags = "Учет носков на складе",
            summary = "Обновление данных в базе данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SockDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content()
                    )
            }
    )
    @PatchMapping()
    public ResponseEntity<SockDto> updateSock(@RequestBody SockDto sockDto) {
        return ResponseEntity.ok(sockService.updateSock(sockDto).orElseThrow(() -> new InvalidResultException(EXCMSG_UNEXPECTED_VALUE)));
    }

    @Operation(
            tags = "Учет носков на складе",
            summary = "Удаление записи из базы данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SockDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content()
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<SockDto> deleteSock(@PathVariable Long id) {
        return ResponseEntity.ok(sockService.deleteSock(id).orElseThrow(() -> new InvalidResultException(EXCMSG_UNEXPECTED_VALUE)));
    }
}

