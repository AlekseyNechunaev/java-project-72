package hexlet.code;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.query.QUrlCheck;
import io.ebean.DB;
import io.ebean.Transaction;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {

    private static Javalin app;
    private static String baseUrl;
    private static Transaction transaction;
    private static final String EXISTS_URL_NAME = "https://www.example.com";
    private static final MockWebServer SERVER = new MockWebServer();
    private static final Path MOCK_FILE_PATH = Paths.get("src/test/resources/mock/mock.html");

    private static Url mockUrlName;

    @BeforeAll
    static void initApp() throws IOException {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        Url existingUrl = new Url(EXISTS_URL_NAME);
        existingUrl.save();
        String contentMock = Files.readString(MOCK_FILE_PATH, StandardCharsets.US_ASCII);
        SERVER.enqueue(new MockResponse().setBody(contentMock));
        SERVER.start();
        String mockHost = SERVER.url("/").toString();
        mockUrlName = new Url(mockHost);
        mockUrlName.normalize();
        mockUrlName.save();

    }


    @AfterAll
    static void stopApp() throws IOException {
        app.stop();
        SERVER.shutdown();
    }

    @BeforeEach
    void beforeEach() {
        transaction = DB.beginTransaction();
    }

    @AfterEach
    void afterEach() {
        transaction.rollback();
    }

    @Nested
    class RootTest {

        @Test
        void indexTest() {
            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/")
                    .asString();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).contains("Анализатор страниц", "Пример: https://www.example.com");
        }
    }

    @Nested
    class UrlTest {
        @Test
        void indexTest() {
            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls")
                    .asString();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).contains("Сайты")
                    .contains("https://www.example.com");
        }

        @Test
        void showCurrentUrlTest() {
            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls/1")
                    .asString();
            assertThat(response.getStatus()).isEqualTo(200);
            Url url = new QUrl()
                    .id.eq(1)
                    .findOne();
            assertThat(url).isNotNull();
            assertThat(response.getBody()).contains(url.getName());
        }

        @Test
        void createInvalidUrlTest() {
            String invalidUrl = "htta:example.asd";
            HttpResponse<?> responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", invalidUrl)
                    .asEmpty();
            assertThat(responsePost.getStatus()).isEqualTo(302);
            assertThat(responsePost.getHeaders().getFirst("Location")).contains("/");
            HttpResponse<String> responseGet = Unirest
                    .get(baseUrl)
                    .asString();
            assertThat(responseGet.getStatus()).isEqualTo(200);
            assertThat(responseGet.getBody()).contains("Некорректный URL");
            Url url = new QUrl()
                    .name.eq(invalidUrl)
                    .findOne();
            assertThat(url).isNull();
        }

        @Test
        void createExistsUrlTest() {
            HttpResponse<?> responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", EXISTS_URL_NAME)
                    .asEmpty();
            assertThat(responsePost.getStatus()).isEqualTo(302);
            assertThat(responsePost.getHeaders().getFirst("Location")).contains("/");
            HttpResponse<String> responseGet = Unirest
                    .get(baseUrl)
                    .asString();
            assertThat(responseGet.getStatus()).isEqualTo(200);
            assertThat(responseGet.getBody()).contains("Страница уже существует");
            int urlCount = new QUrl()
                    .name.eq(EXISTS_URL_NAME)
                    .findCount();
            assertThat(urlCount).isEqualTo(1);
        }

        @Test
        void successCreateUrlWithNormalizeTest() {
            String name = "https://github.com/hexlet-components/";
            String normalizeName = "https://github.com";
            HttpResponse<?> responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", name)
                    .asEmpty();
            assertThat(responsePost.getStatus()).isEqualTo(302);
            assertThat(responsePost.getHeaders().getFirst("Location")).contains("urls");
            HttpResponse<String> responseGet = Unirest
                    .get(baseUrl)
                    .asString();
            assertThat(responseGet.getStatus()).isEqualTo(200);
            assertThat(responseGet.getBody()).contains("Страница успешно добавлена");
            Url url = new QUrl()
                    .name.eq(normalizeName)
                    .findOne();
            assertThat(url).isNotNull();
        }

        @Test
        void invalidUrlCheckTest() {
            String unknownHostUrl = "https://unkonown-host-example.com";
            HttpResponse<?> responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", unknownHostUrl)
                    .asEmpty();
            assertThat(responsePost.getStatus()).isEqualTo(302);
            assertThat(responsePost.getHeaders().getFirst("Location")).contains("urls");
            Url url = new QUrl()
                    .name.eq(unknownHostUrl)
                    .findOne();
            assertThat(url).isNotNull();
            HttpResponse<?> responsePostCheck = Unirest
                    .post(baseUrl + "/urls/" + url.getId() + "/checks")
                    .asEmpty();
            assertThat(responsePostCheck.getStatus()).isEqualTo(302);
            HttpResponse<String> responseGet = Unirest
                    .get(baseUrl + "/urls/" + url.getId())
                    .asString();
            assertThat(responseGet.getStatus()).isEqualTo(200);
            assertThat(responseGet.getBody()).contains("Некорректный URL");
            List<UrlCheck> checks = new QUrlCheck()
                    .url.id.eq(url.getId())
                    .findList();
            assertThat(checks).isEmpty();

        }

        @Test
        void successUrlCheckTest() {
            String exceptedH1 = "Example H1";
            String exceptedTitle = "Example title";
            String exceptedDescription = "Example description";
            HttpResponse<?> responsePostCheck = Unirest
                    .post(baseUrl + "/urls/" + mockUrlName.getId() + "/checks")
                    .asEmpty();
            assertThat(responsePostCheck.getStatus()).isEqualTo(302);
            HttpResponse<String> responseGet = Unirest
                    .get(baseUrl + "/urls/" + mockUrlName.getId())
                    .asString();
            assertThat(responseGet.getStatus()).isEqualTo(200);
            assertThat(responseGet.getBody()).contains("Страница успешно проверена")
                    .contains(exceptedTitle)
                    .contains(exceptedH1)
                    .contains(exceptedDescription);
            List<UrlCheck> checks = new QUrlCheck()
                    .url.id.eq(mockUrlName.getId())
                    .findList();
            assertThat(checks).hasSize(1);
        }
    }

}
