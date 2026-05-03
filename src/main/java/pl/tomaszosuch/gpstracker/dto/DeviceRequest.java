package pl.tomaszosuch.gpstracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dane wymagane do rejestracji nowego urządzenia")
public record DeviceRequest(

        @Schema(description = "Nazwa urządzenia", example = "Tracker Warszawa A")
        @NotBlank @Size(max = 255)
        String name,

        @Schema(description = "Typ urządzenia", example = "MOBILE",
                allowableValues = {"MOBILE", "TABLET", "IOT"})
        @NotBlank @Size(max = 100)
        String type,

        @Schema(description = "Unikalny identyfikator urządzenia nadany przez producenta",
                example = "device-imei-123456789")
        @NotBlank @Size(max = 255)
        String externalId
) {}
