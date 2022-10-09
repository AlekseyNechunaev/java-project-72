package hexlet.code.constants;

public enum FlashType {

    DANGER("danger"),
    WARNING("warning"),
    SUCCESS("success");

    private final String type;

    FlashType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
