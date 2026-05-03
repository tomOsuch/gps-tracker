package pl.tomaszosuch.gpstracker.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pl.tomaszosuch.gpstracker.model.Location;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LocationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LocationRepository locationRepository;

    private static final Long DEVICE_ID = 1L;

    private Location buildLocation(Long deviceId, double lat, double lon, Instant recordedAt) {
        return Location.builder()
                .deviceId(deviceId)
                .latitude(lat)
                .longitude(lon)
                .recordedAt(recordedAt)
                .createdAt(Instant.now())
                .build();
    }

    @BeforeEach
    void setUp() {
        locationRepository.deleteAll();
        entityManager.flush();
    }

    @Test
    void findLatestByDeviceId_shouldReturnMostRecentLocation() {
        Instant older  = Instant.parse("2026-05-01T10:00:00Z");
        Instant newer  = Instant.parse("2026-05-01T12:00:00Z");

        entityManager.persistAndFlush(buildLocation(DEVICE_ID, 52.0, 21.0, older));
        entityManager.persistAndFlush(buildLocation(DEVICE_ID, 53.0, 22.0, newer));

        Optional<Location> result = locationRepository.findLatestByDeviceId(DEVICE_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getLatitude()).isEqualTo(53.0);
        assertThat(result.get().getLongitude()).isEqualTo(22.0);
        assertThat(result.get().getRecordedAt()).isEqualTo(newer);
    }

    @Test
    void findLatestByDeviceId_shouldReturnEmpty_whenNoLocationsForDevice() {
        Optional<Location> result = locationRepository.findLatestByDeviceId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findLatestByDeviceId_shouldReturnOnlyLocationsForGivenDevice() {
        Instant t1 = Instant.parse("2026-05-01T10:00:00Z");
        Instant t2 = Instant.parse("2026-05-01T11:00:00Z");

        entityManager.persistAndFlush(buildLocation(DEVICE_ID, 52.0, 21.0, t1));
        entityManager.persistAndFlush(buildLocation(2L, 48.0, 16.0, t2));

        Optional<Location> result = locationRepository.findLatestByDeviceId(DEVICE_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getDeviceId()).isEqualTo(DEVICE_ID);
        assertThat(result.get().getLatitude()).isEqualTo(52.0);
    }

    @Test
    void findLatestByDeviceId_shouldReturnSingleLocation_whenOnlyOneExists() {
        Instant t = Instant.parse("2026-05-01T10:00:00Z");
        entityManager.persistAndFlush(buildLocation(DEVICE_ID, 52.2297, 21.0122, t));

        Optional<Location> result = locationRepository.findLatestByDeviceId(DEVICE_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getLatitude()).isEqualTo(52.2297);
    }

    @Test
    void save_shouldPersistLocationAndGenerateId() {
        Location location = buildLocation(DEVICE_ID, 52.2297, 21.0122, Instant.now());

        Location saved = locationRepository.save(location);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDeviceId()).isEqualTo(DEVICE_ID);
        assertThat(saved.getLatitude()).isEqualTo(52.2297);
    }
}
