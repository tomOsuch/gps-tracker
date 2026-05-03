package pl.tomaszosuch.gpstracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.tomaszosuch.gpstracker.dto.LocationRequest;
import pl.tomaszosuch.gpstracker.dto.LocationResponse;
import pl.tomaszosuch.gpstracker.exception.DeviceNotFoundException;
import pl.tomaszosuch.gpstracker.exception.LocationNotFoundException;
import pl.tomaszosuch.gpstracker.service.LocationService;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationController.class)
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LocationService locationService;

    private static final Instant RECORDED_AT = Instant.parse("2026-05-01T12:00:00Z");
    private static final Instant CREATED_AT  = Instant.parse("2026-05-01T12:00:01Z");

    @Test
    void save_shouldReturn201WithLocation_whenRequestIsValid() throws Exception {
        LocationRequest request = new LocationRequest(52.2297, 21.0122, RECORDED_AT);
        LocationResponse response = new LocationResponse(10L, 1L, 52.2297, 21.0122, RECORDED_AT, CREATED_AT);

        when(locationService.save(eq(1L), any(LocationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/devices/1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/devices/1/locations/latest")))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.deviceId").value(1))
                .andExpect(jsonPath("$.latitude").value(52.2297))
                .andExpect(jsonPath("$.longitude").value(21.0122));
    }

    @Test
    void save_shouldReturn404_whenDeviceDoesNotExist() throws Exception {
        LocationRequest request = new LocationRequest(52.2297, 21.0122, RECORDED_AT);

        when(locationService.save(eq(99L), any(LocationRequest.class)))
                .thenThrow(new DeviceNotFoundException(99L));

        mockMvc.perform(post("/api/devices/99/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("99")));
    }

    @Test
    void save_shouldReturn400_whenLatitudeIsNull() throws Exception {
        LocationRequest request = new LocationRequest(null, 21.0122, RECORDED_AT);

        mockMvc.perform(post("/api/devices/1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.latitude").exists());
    }

    @Test
    void save_shouldReturn400_whenLatitudeOutOfRange() throws Exception {
        LocationRequest request = new LocationRequest(91.0, 21.0122, RECORDED_AT);

        mockMvc.perform(post("/api/devices/1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.latitude").exists());
    }

    @Test
    void save_shouldReturn400_whenLongitudeOutOfRange() throws Exception {
        LocationRequest request = new LocationRequest(52.2297, 181.0, RECORDED_AT);

        mockMvc.perform(post("/api/devices/1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.longitude").exists());
    }

    @Test
    void save_shouldReturn400_whenRecordedAtIsNull() throws Exception {
        LocationRequest request = new LocationRequest(52.2297, 21.0122, null);

        mockMvc.perform(post("/api/devices/1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.recordedAt").exists());
    }

    @Test
    void findLatest_shouldReturn200WithLatestLocation_whenLocationExists() throws Exception {
        LocationResponse response = new LocationResponse(10L, 1L, 52.2297, 21.0122, RECORDED_AT, CREATED_AT);

        when(locationService.findLatest(1L)).thenReturn(response);

        mockMvc.perform(get("/api/devices/1/locations/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.deviceId").value(1))
                .andExpect(jsonPath("$.latitude").value(52.2297))
                .andExpect(jsonPath("$.longitude").value(21.0122));
    }

    @Test
    void findLatest_shouldReturn404_whenNoLocationExists() throws Exception {
        when(locationService.findLatest(1L)).thenThrow(new LocationNotFoundException(1L));

        mockMvc.perform(get("/api/devices/1/locations/latest"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("1")));
    }

    @Test
    void findLatest_shouldReturn404_whenDeviceDoesNotExist() throws Exception {
        when(locationService.findLatest(99L)).thenThrow(new DeviceNotFoundException(99L));

        mockMvc.perform(get("/api/devices/99/locations/latest"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("99")));
    }
}
