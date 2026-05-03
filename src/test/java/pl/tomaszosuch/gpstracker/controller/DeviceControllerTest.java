package pl.tomaszosuch.gpstracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.tomaszosuch.gpstracker.dto.DeviceRequest;
import pl.tomaszosuch.gpstracker.dto.DeviceResponse;
import pl.tomaszosuch.gpstracker.exception.DeviceAlreadyExistsException;
import pl.tomaszosuch.gpstracker.exception.DeviceNotFoundException;
import pl.tomaszosuch.gpstracker.service.DeviceService;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeviceController.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeviceService deviceService;

    private static final Instant NOW = Instant.parse("2026-05-01T12:00:00Z");

    @Test
    void register_shouldReturn201WithLocation_whenRequestIsValid() throws Exception {
        DeviceRequest request = new DeviceRequest("Tracker A", "MOBILE", "ext-001");
        DeviceResponse response = new DeviceResponse(1L, "Tracker A", "MOBILE", "ext-001", NOW);

        when(deviceService.register(any(DeviceRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/devices/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Tracker A"))
                .andExpect(jsonPath("$.type").value("MOBILE"))
                .andExpect(jsonPath("$.externalId").value("ext-001"));
    }

    @Test
    void register_shouldReturn409_whenExternalIdAlreadyExists() throws Exception {
        DeviceRequest request = new DeviceRequest("Tracker A", "MOBILE", "ext-001");

        when(deviceService.register(any(DeviceRequest.class)))
                .thenThrow(new DeviceAlreadyExistsException("ext-001"));

        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("ext-001")));
    }

    @Test
    void register_shouldReturn400_whenNameIsBlank() throws Exception {
        DeviceRequest request = new DeviceRequest("", "MOBILE", "ext-001");

        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void register_shouldReturn400_whenExternalIdIsBlank() throws Exception {
        DeviceRequest request = new DeviceRequest("Tracker A", "MOBILE", "");

        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.externalId").exists());
    }

    @Test
    void findAll_shouldReturn200WithList() throws Exception {
        List<DeviceResponse> devices = List.of(
                new DeviceResponse(1L, "Tracker A", "MOBILE", "ext-001", NOW),
                new DeviceResponse(2L, "Tracker B", "IOT", "ext-002", NOW)
        );

        when(deviceService.findAll()).thenReturn(devices);

        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void findAll_shouldReturn200WithEmptyList() throws Exception {
        when(deviceService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void findById_shouldReturn200WithDevice_whenDeviceExists() throws Exception {
        DeviceResponse response = new DeviceResponse(1L, "Tracker A", "MOBILE", "ext-001", NOW);

        when(deviceService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/devices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Tracker A"))
                .andExpect(jsonPath("$.externalId").value("ext-001"));
    }

    @Test
    void findById_shouldReturn404_whenDeviceDoesNotExist() throws Exception {
        when(deviceService.findById(99L)).thenThrow(new DeviceNotFoundException(99L));

        mockMvc.perform(get("/api/devices/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("99")));
    }
}
