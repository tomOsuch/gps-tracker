package pl.tomaszosuch.gpstracker.controller;

import pl.tomaszosuch.gpstracker.dto.LocationRequest;
import pl.tomaszosuch.gpstracker.dto.LocationResponse;
import pl.tomaszosuch.gpstracker.exception.ErrorResponse;
import pl.tomaszosuch.gpstracker.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/devices/{deviceId}/locations")
@RequiredArgsConstructor
@Tag(name = "Locations", description = "Zarządzanie danymi GPS urządzeń")
public class LocationController {

    private final LocationService locationService;

    @Operation(summary = "Wyślij pozycję GPS",
            description = "Zapisuje nową pozycję GPS dla wskazanego urządzenia.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pozycja zapisana pomyślnie",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LocationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane GPS",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Urządzenie nie zostało znalezione",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<LocationResponse> save(
            @Parameter(description = "ID urządzenia", example = "1")
            @PathVariable Long deviceId,
            @Valid @RequestBody LocationRequest request) {
        LocationResponse response = locationService.save(deviceId, request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/latest")
                .build()
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Pobierz ostatnią pozycję GPS",
            description = "Zwraca ostatnią zarejestrowaną pozycję GPS urządzenia.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ostatnia pozycja GPS",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LocationResponse.class))),
            @ApiResponse(responseCode = "404", description = "Brak danych GPS lub urządzenie nie istnieje",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/latest")
    public ResponseEntity<LocationResponse> findLatest(
            @Parameter(description = "ID urządzenia", example = "1")
            @PathVariable Long deviceId) {
        return ResponseEntity.ok(locationService.findLatest(deviceId));
    }
}
