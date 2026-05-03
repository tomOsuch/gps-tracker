package pl.tomaszosuch.gpstracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.tomaszosuch.gpstracker.dto.LocationRequest;
import pl.tomaszosuch.gpstracker.dto.LocationResponse;
import pl.tomaszosuch.gpstracker.exception.DeviceNotFoundException;
import pl.tomaszosuch.gpstracker.exception.LocationNotFoundException;
import pl.tomaszosuch.gpstracker.model.Device;
import pl.tomaszosuch.gpstracker.model.Location;
import pl.tomaszosuch.gpstracker.repository.LocationRepository;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private LocationServiceImpl locationService;

    private Device device;
    private Location location;
    private LocationRequest request;
    private final Instant recordedAt = Instant.parse("2026-05-01T12:00:00Z");
    private final Instant createdAt = Instant.parse("2026-05-01T12:00:01Z");

    @BeforeEach
    void setUp() {
        device = Device.builder()
                .id(1L)
                .name("Tracker A")
                .type("MOBILE")
                .externalId("ext-001")
                .createdAt(Instant.now())
                .build();
        request = new LocationRequest(52.2297, 21.0122, recordedAt);
        location = Location.builder()
                .id(10L)
                .deviceId(1L)
                .latitude(52.2297)
                .longitude(21.0122)
                .recordedAt(recordedAt)
                .createdAt(createdAt)
                .build();
    }

    @Test
    void save_shouldSaveAndReturnLocationResponse_whenDeviceExists() {
        when(deviceService.getEntityById(1L)).thenReturn(device);
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        LocationResponse response = locationService.save(1L, request);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.deviceId()).isEqualTo(1L);
        assertThat(response.latitude()).isEqualTo(52.2297);
        assertThat(response.longitude()).isEqualTo(21.0122);
        assertThat(response.recordedAt()).isEqualTo(recordedAt);
        verify(locationRepository).save(any(Location.class));
    }

    @Test
    void save_shouldThrowDeviceNotFoundException_whenDeviceDoesNotExist() {
        when(deviceService.getEntityById(99L)).thenThrow(new DeviceNotFoundException(99L));

        assertThatThrownBy(() -> locationService.save(99L, request))
                .isInstanceOf(DeviceNotFoundException.class)
                .hasMessageContaining("99");

        verify(locationRepository, never()).save(any());
    }

    @Test
    void findLatest_shouldReturnLatestLocationResponse_whenLocationExists() {
        when(deviceService.getEntityById(1L)).thenReturn(device);
        when(locationRepository.findLatestByDeviceId(1L)).thenReturn(Optional.of(location));

        LocationResponse response = locationService.findLatest(1L);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.deviceId()).isEqualTo(1L);
        assertThat(response.latitude()).isEqualTo(52.2297);
        assertThat(response.longitude()).isEqualTo(21.0122);
        assertThat(response.recordedAt()).isEqualTo(recordedAt);
        assertThat(response.createdAt()).isEqualTo(createdAt);
    }

    @Test
    void findLatest_shouldThrowLocationNotFoundException_whenNoLocationExists() {
        when(deviceService.getEntityById(1L)).thenReturn(device);
        when(locationRepository.findLatestByDeviceId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.findLatest(1L))
                .isInstanceOf(LocationNotFoundException.class)
                .hasMessageContaining("1");
    }

    @Test
    void findLatest_shouldThrowDeviceNotFoundException_whenDeviceDoesNotExist() {
        when(deviceService.getEntityById(99L)).thenThrow(new DeviceNotFoundException(99L));

        assertThatThrownBy(() -> locationService.findLatest(99L))
                .isInstanceOf(DeviceNotFoundException.class)
                .hasMessageContaining("99");

        verifyNoInteractions(locationRepository);
    }
}
