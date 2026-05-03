package pl.tomaszosuch.gpstracker.service;

import pl.tomaszosuch.gpstracker.dto.DeviceRequest;
import pl.tomaszosuch.gpstracker.dto.DeviceResponse;
import pl.tomaszosuch.gpstracker.model.Device;

import java.util.List;

public interface DeviceService {
    DeviceResponse register(DeviceRequest request);
    List<DeviceResponse> findAll();
    DeviceResponse findById(Long id);
    Device getEntityById(Long id);
}
