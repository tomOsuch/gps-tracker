package pl.tomaszosuch.gpstracker.exception;

public class DeviceAlreadyExistsException extends RuntimeException {
    public DeviceAlreadyExistsException(String externalId) {
        super("Device with externalId '" + externalId + "' already exists");
    }
}
