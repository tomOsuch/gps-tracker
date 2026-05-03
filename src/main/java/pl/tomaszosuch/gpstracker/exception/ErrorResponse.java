package pl.tomaszosuch.gpstracker.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Odpowiedź błędu API")
public record ErrorResponse(

        @Schema(description = "Kod statusu HTTP", example = "404")
        int status,

        @Schema(description = "Opis błędu", example = "Device not found with id: 1")
        String message,

        @Schema(description = "Czas wystąpienia błędu", example = "2026-05-01T12:00:00Z")
        String timestamp
) {}
