package pl.tomaszosuch.gpstracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Zarejestrowana pozycja GPS urządzenia")
public record LocationResponse(

        @Schema(description = "Identyfikator wpisu lokalizacji", example = "1")
        Long id,

        @Schema(description = "Identyfikator urządzenia", example = "1")
        Long deviceId,

        @Schema(description = "Szerokość geograficzna", example = "52.2297")
        Double latitude,

        @Schema(description = "Długość geograficzna", example = "21.0122")
        Double longitude,

        @Schema(description = "Czas zarejestrowania pozycji przez urządzenie",
                example = "2026-05-01T12:00:00Z")
        Instant recordedAt,

        @Schema(description = "Czas zapisu do bazy danych", example = "2026-05-01T12:00:01Z")
        Instant createdAt
) {}
