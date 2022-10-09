package hexlet.code.constants;

public enum Message {

    SUCCESS("Страница успешно сохранена"),
    URL_ALREADY_EXISTS("Страница уже существует"),

    INCORRECT_URL("Некорректный URL");

    private final String message;

    Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
