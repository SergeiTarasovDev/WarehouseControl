package ru.teliontech.warehousecontrol.utils;

import org.springframework.stereotype.Service;
import ru.teliontech.warehousecontrol.dto.SockDto;
import ru.teliontech.warehousecontrol.entity.Sock;

@Service
public class MappingUtils {

    public SockDto mapToSockDto(Sock sock) {
        SockDto sockDto = new SockDto();
        sockDto.setId(sock.getId());
        sockDto.setColor(sock.getColor());
        sockDto.setCottonPart(sock.getCottonPart());
        sockDto.setStock(sock.getStock());
        return sockDto;
    }

    public SockDto mapToSockDto(Sock sock, int quantity) {
        SockDto sockDto = new SockDto();
        sockDto.setId(sock.getId());
        sockDto.setColor(sock.getColor());
        sockDto.setCottonPart(sock.getCottonPart());
        sockDto.setQuantity(quantity);
        sockDto.setStock(sock.getStock());
        return sockDto;
    }

    public Sock mapToSock(SockDto sockDto) {
        Sock sock = new Sock();
        sock.setId(sockDto.getId());
        sock.setColor(sockDto.getColor());
        sock.setCottonPart(sockDto.getCottonPart());
        sock.setStock(sockDto.getStock());
        return sock;
    }

}