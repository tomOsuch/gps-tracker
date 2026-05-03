package pl.tomaszosuch.gpstracker.service;

import pl.tomaszosuch.gpstracker.dto.LocationRequest;
import pl.tomaszosuch.gpstracker.dto.LocationResponse;

public interface LocationService {
    LocationResponse save(Long deviceId, LocationRequest request);
    LocationResponse findLatest(Long deviceId);
}
