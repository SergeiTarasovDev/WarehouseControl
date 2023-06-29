package ru.teliontech.warehousecontrol.utils;

import org.springframework.stereotype.Service;
import ru.teliontech.warehousecontrol.dto.SockDto;
import ru.teliontech.warehousecontrol.dto.SockQntDto;
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

    public SockQntDto mapToSockQntDto(Sock sock, int quantity) {
        SockQntDto sockQntDto = new SockQntDto();
        sockQntDto.setId(sock.getId());
        sockQntDto.setColor(sock.getColor());
        sockQntDto.setCottonPart(sock.getCottonPart());
        sockQntDto.setQuantity(quantity);
        sockQntDto.setStock(sock.getStock());
        return sockQntDto;
    }

    public Sock mapToSock(SockDto sockDto) {
        Sock sock = new Sock();
        sock.setId(sockDto.getId());
        sock.setColor(sockDto.getColor());
        sock.setCottonPart(sockDto.getCottonPart());
        sock.setStock(sockDto.getStock());
        return sock;
    }

    public Sock mapToSock(SockQntDto sockQntDto) {
        Sock sock = new Sock();
        sock.setId(sockQntDto.getId());
        sock.setColor(sockQntDto.getColor());
        sock.setCottonPart(sockQntDto.getCottonPart());
        sock.setStock(sockQntDto.getStock());
        return sock;
    }
}