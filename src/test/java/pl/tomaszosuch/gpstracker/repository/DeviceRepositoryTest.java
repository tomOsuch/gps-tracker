package pl.tomaszosuch.gpstracker.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pl.tomaszosuch.gpstracker.model.Device;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DeviceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DeviceRepository deviceRepository;

    private Device buildDevice(String name, String type, String externalId) {
        return Device.builder()
                .name(name)
                .type(type)
                .externalId(externalId)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void existsByExternalId_shouldReturnTrue_whenDeviceWithExternalIdExists() {
        entityManager.persistAndFlush(buildDevice("Tracker A", "MOBILE", "ext-001"));

        assertThat(deviceRepository.existsByExternalId("ext-001")).isTrue();
    }

    @Test
    void existsByExternalId_shouldReturnFalse_whenDeviceWithExternalIdDoesNotExist() {
        assertThat(deviceRepository.existsByExternalId("nonexistent")).isFalse();
    }

    @Test
    void findByExternalId_shouldReturnDevice_whenExists() {
        entityManager.persistAndFlush(buildDevice("Tracker A", "MOBILE", "ext-001"));

        Optional<Device> result = deviceRepository.findByExternalId("ext-001");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Tracker A");
        assertThat(result.get().getType()).isEqualTo("MOBILE");
    }

    @Test
    void findByExternalId_shouldReturnEmpty_whenDoesNotExist() {
        Optional<Device> result = deviceRepository.findByExternalId("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void save_shouldPersistDeviceAndGenerateId() {
        Device device = buildDevice("Tracker B", "IOT", "ext-002");

        Device saved = deviceRepository.save(device);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Tracker B");
        assertThat(saved.getExternalId()).isEqualTo("ext-002");
    }

    @Test
    void findAll_shouldReturnAllPersistedDevices() {
        entityManager.persistAndFlush(buildDevice("Tracker A", "MOBILE", "ext-001"));
        entityManager.persistAndFlush(buildDevice("Tracker B", "IOT", "ext-002"));

        List<Device> devices = deviceRepository.findAll();

        assertThat(devices).hasSize(2);
    }

    @Test
    void findById_shouldReturnDevice_whenExists() {
        Device persisted = entityManager.persistAndFlush(buildDevice("Tracker A", "MOBILE", "ext-001"));

        Optional<Device> result = deviceRepository.findById(persisted.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getExternalId()).isEqualTo("ext-001");
    }

    @Test
    void findById_shouldReturnEmpty_whenDoesNotExist() {
        Optional<Device> result = deviceRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void deleteById_shouldRemoveDevice() {
        Device persisted = entityManager.persistAndFlush(buildDevice("Tracker A", "MOBILE", "ext-001"));

        deviceRepository.deleteById(persisted.getId());
        entityManager.flush();

        assertThat(deviceRepository.findById(persisted.getId())).isEmpty();
    }
}
