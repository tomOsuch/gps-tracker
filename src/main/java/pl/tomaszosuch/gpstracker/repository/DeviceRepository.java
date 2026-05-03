package pl.tomaszosuch.gpstracker.repository;

import pl.tomaszosuch.gpstracker.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    boolean existsByExternalId(String externalId);
    Optional<Device> findByExternalId(String externalId);
}
