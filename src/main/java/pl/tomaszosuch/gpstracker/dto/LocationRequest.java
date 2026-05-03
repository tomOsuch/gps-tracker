package pl.tomaszosuch.gpstracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Schema(description = "Dane pozycji GPS urządzenia")
public record LocationRequest(

        @Schema(description = "Szerokość geograficzna (latitude)",
                example = "52.2297", minimum = "-90", maximum = "90")
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0")
        Double latitude,

        @Schema(description = "Długość geograficzna (longitude)",
                example = "21.0122", minimum = "-180", maximum = "180")
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0")
        Double longitude,

        @Schema(description = "Czas zarejestrowania pozycji przez urządzenie",
                example = "2026-05-01T12:00:00Z")
        @NotNull
        Instant recordedAt
) {}
