package pl.tomaszosuch.gpstracker.exception;

public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException(Long deviceId) {
        super("No location data found for device id: " + deviceId);
    }
}
