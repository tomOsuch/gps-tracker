package pl.tomaszosuch.gpstracker.controller;

import pl.tomaszosuch.gpstracker.dto.DeviceRequest;
import pl.tomaszosuch.gpstracker.dto.DeviceResponse;
import pl.tomaszosuch.gpstracker.exception.ErrorResponse;
import pl.tomaszosuch.gpstracker.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
@Tag(name = "Devices", description = "Zarządzanie urządzeniami mobilnymi")
public class DeviceController {

    private final DeviceService deviceService;

    @Operation(summary = "Rejestracja nowego urządzenia",
            description = "Rejestruje nowe urządzenie mobilne w systemie. ExternalId musi być unikalny.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Urządzenie zarejestrowane pomyślnie",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DeviceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Urządzenie o podanym externalId już istnieje",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<DeviceResponse> register(@Valid @RequestBody DeviceRequest request) {
        DeviceResponse response = deviceService.register(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Lista wszystkich urządzeń",
            description = "Zwraca listę wszystkich zarejestrowanych urządzeń w systemie.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista urządzeń",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = DeviceResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<DeviceResponse>> findAll() {
        return ResponseEntity.ok(deviceService.findAll());
    }

    @Operation(summary = "Szczegóły urządzenia",
            description = "Zwraca szczegółowe informacje o urządzeniu na podstawie jego ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dane urządzenia",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DeviceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Urządzenie nie zostało znalezione",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> findById(
            @Parameter(description = "ID urządzenia", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(deviceService.findById(id));
    }
}
