package pl.tomaszosuch.gpstracker.repository;

import pl.tomaszosuch.gpstracker.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("""
            SELECT l FROM Location l
            WHERE l.deviceId = :deviceId
            ORDER BY l.recordedAt DESC
            LIMIT 1
            """)
    Optional<Location> findLatestByDeviceId(@Param("deviceId") Long deviceId);
}
