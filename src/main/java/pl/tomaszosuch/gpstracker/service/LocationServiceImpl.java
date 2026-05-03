package pl.tomaszosuch.gpstracker.service;

import pl.tomaszosuch.gpstracker.dto.LocationRequest;
import pl.tomaszosuch.gpstracker.dto.LocationResponse;
import pl.tomaszosuch.gpstracker.exception.LocationNotFoundException;
import pl.tomaszosuch.gpstracker.model.Location;
import pl.tomaszosuch.gpstracker.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final DeviceService deviceService;

    @Override
    @Transactional
    public LocationResponse save(Long deviceId, LocationRequest request) {
        log.info("Saving location for deviceId={}", deviceId);
        deviceService.getEntityById(deviceId);
        Location location = Location.builder()
                .deviceId(deviceId)
                .latitude(request.latitude())
                .longitude(request.longitude())
                .recordedAt(request.recordedAt())
                .build();
        Location saved = locationRepository.save(location);
        log.info("Location saved id={} deviceId={}", saved.getId(), deviceId);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LocationResponse findLatest(Long deviceId) {
        deviceService.getEntityById(deviceId);
        return locationRepository.findLatestByDeviceId(deviceId)
                .map(this::toResponse)
                .orElseThrow(() -> new LocationNotFoundException(deviceId));
    }

    private LocationResponse toResponse(Location l) {
        return new LocationResponse(l.getId(), l.getDeviceId(), l.getLatitude(),
                l.getLongitude(), l.getRecordedAt(), l.getCreatedAt());
    }
}
