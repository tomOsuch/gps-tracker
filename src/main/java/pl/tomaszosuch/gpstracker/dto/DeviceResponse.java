package pl.tomaszosuch.gpstracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Dane zarejestrowanego urządzenia")
public record DeviceResponse(

        @Schema(description = "Identyfikator urządzenia", example = "1")
        Long id,

        @Schema(description = "Nazwa urządzenia", example = "Tracker Warszawa A")
        String name,

        @Schema(description = "Typ urządzenia", example = "MOBILE")
        String type,

        @Schema(description = "Unikalny identyfikator zewnętrzny", example = "device-imei-123456789")
        String externalId,

        @Schema(description = "Data i czas rejestracji urządzenia", example = "2026-05-01T12:00:00Z")
        Instant createdAt
) {}
