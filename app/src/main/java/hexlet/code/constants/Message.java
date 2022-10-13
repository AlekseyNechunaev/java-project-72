package hexlet.code.constants;

public enum Message {

    SUCCESS_CREATE_URL("Страница успешно добавлена"),
    URL_ALREADY_EXISTS("Страница уже существует"),

    INCORRECT_URL("Некорректный URL"),

    SUCCESS_CHECK("Страница успешно проверена");

    private final String message;

    Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
