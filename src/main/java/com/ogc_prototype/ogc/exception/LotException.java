package com.ogc_prototype.ogc.exception;

import java.util.Date;
import org.springframework.http.HttpStatus;

public class LotException extends AppException {

    private LotException(HttpStatus status, String message) {
        super(status, message);
    }

    public static LotException notFound(Integer id) {
        return new LotException(HttpStatus.NOT_FOUND, "Lote no encontrado con id: " + id);
    }

    public static LotException stockNotFound(Integer lotId) {
        return new LotException(HttpStatus.NOT_FOUND,
                "Registro de stock no encontrado para el lote con id: " + lotId);
    }

    public static LotException expired(Integer lotId, Date expirationDate) {
        return new LotException(HttpStatus.UNPROCESSABLE_CONTENT,
                "El lote " + lotId + " caducó el " + expirationDate);
    }

    public static LotException alreadyAssigned(Integer lotId) {
        return new LotException(HttpStatus.UNPROCESSABLE_CONTENT,
                "El lote " + lotId + " ya está asignado a una línea de compra");
    }

    public static LotException insufficientStock(Integer lotId, Double requested,
            Double available) {
        return new LotException(HttpStatus.UNPROCESSABLE_CONTENT,
                "Stock insuficiente en el lote " + lotId + ": se solicitaron " + requested
                        + " kg pero solo hay " + available + " kg disponibles");
    }
}

