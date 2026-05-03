package pl.tomaszosuch.gpstracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.tomaszosuch.gpstracker.dto.DeviceRequest;
import pl.tomaszosuch.gpstracker.dto.DeviceResponse;
import pl.tomaszosuch.gpstracker.exception.DeviceAlreadyExistsException;
import pl.tomaszosuch.gpstracker.exception.DeviceNotFoundException;
import pl.tomaszosuch.gpstracker.model.Device;
import pl.tomaszosuch.gpstracker.repository.DeviceRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceImplTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceServiceImpl deviceService;

    private Device device;
    private DeviceRequest request;

    @BeforeEach
    void setUp() {
        request = new DeviceRequest("Tracker A", "MOBILE", "ext-001");
        device = Device.builder()
                .id(1L)
                .name("Tracker A")
                .type("MOBILE")
                .externalId("ext-001")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void register_shouldSaveAndReturnResponse_whenExternalIdIsUnique() {
        when(deviceRepository.existsByExternalId("ext-001")).thenReturn(false);
        when(deviceRepository.save(any(Device.class))).thenReturn(device);

        DeviceResponse response = deviceService.register(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Tracker A");
        assertThat(response.type()).isEqualTo("MOBILE");
        assertThat(response.externalId()).isEqualTo("ext-001");
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    void register_shouldThrowDeviceAlreadyExistsException_whenExternalIdExists() {
        when(deviceRepository.existsByExternalId("ext-001")).thenReturn(true);

        assertThatThrownBy(() -> deviceService.register(request))
                .isInstanceOf(DeviceAlreadyExistsException.class)
                .hasMessageContaining("ext-001");

        verify(deviceRepository, never()).save(any());
    }

    @Test
    void findAll_shouldReturnAllDevicesAsMappedResponses() {
        Device device2 = Device.builder()
                .id(2L)
                .name("Tracker B")
                .type("IOT")
                .externalId("ext-002")
                .createdAt(Instant.now())
                .build();
        when(deviceRepository.findAll()).thenReturn(List.of(device, device2));

        List<DeviceResponse> result = deviceService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(2L);
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoDevicesExist() {
        when(deviceRepository.findAll()).thenReturn(List.of());

        List<DeviceResponse> result = deviceService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findById_shouldReturnDeviceResponse_whenDeviceExists() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

        DeviceResponse response = deviceService.findById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.externalId()).isEqualTo("ext-001");
    }

    @Test
    void findById_shouldThrowDeviceNotFoundException_whenDeviceDoesNotExist() {
        when(deviceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deviceService.findById(99L))
                .isInstanceOf(DeviceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getEntityById_shouldReturnDevice_whenDeviceExists() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

        Device result = deviceService.getEntityById(1L);

        assertThat(result).isEqualTo(device);
    }

    @Test
    void getEntityById_shouldThrowDeviceNotFoundException_whenDeviceDoesNotExist() {
        when(deviceRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deviceService.getEntityById(42L))
                .isInstanceOf(DeviceNotFoundException.class)
                .hasMessageContaining("42");
    }
}
