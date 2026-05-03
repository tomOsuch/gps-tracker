package pl.tomaszosuch.gpstracker.service;

import pl.tomaszosuch.gpstracker.dto.DeviceRequest;
import pl.tomaszosuch.gpstracker.dto.DeviceResponse;
import pl.tomaszosuch.gpstracker.exception.DeviceAlreadyExistsException;
import pl.tomaszosuch.gpstracker.exception.DeviceNotFoundException;
import pl.tomaszosuch.gpstracker.model.Device;
import pl.tomaszosuch.gpstracker.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;

    @Override
    @Transactional
    public DeviceResponse register(DeviceRequest request) {
        log.info("Registering device externalId={}", request.externalId());
        if (deviceRepository.existsByExternalId(request.externalId())) {
            log.warn("Device already exists externalId={}", request.externalId());
            throw new DeviceAlreadyExistsException(request.externalId());
        }
        Device device = Device.builder()
                .name(request.name())
                .type(request.type())
                .externalId(request.externalId())
                .build();
        Device saved = deviceRepository.save(device);
        log.info("Device registered id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceResponse> findAll() {
        return deviceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceResponse findById(Long id) {
        return toResponse(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Device getEntityById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
    }

    private DeviceResponse toResponse(Device d) {
        return new DeviceResponse(d.getId(), d.getName(), d.getType(),
                d.getExternalId(), d.getCreatedAt());
    }
}
