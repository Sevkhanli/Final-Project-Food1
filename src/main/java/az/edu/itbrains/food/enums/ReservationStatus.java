package az.edu.itbrains.food.enums;


public enum ReservationStatus {

    GOZLEMEDE("Gözləmədə"),
    TESDIQLENIB("Təsdiqlənib"),
    LEGV_EDILIB("Ləğv edilib");
    // Əlavə statuslar varsa: BITIB("Bitib");

    private final String azName;

    ReservationStatus(String azName) {
        this.azName = azName;
    }

    public String getAzName() {
        return azName;
    }
}