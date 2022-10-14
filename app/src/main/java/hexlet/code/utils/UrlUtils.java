package hexlet.code.utils;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static org.apache.commons.validator.routines.UrlValidator.ALLOW_LOCAL_URLS;

public final class UrlUtils {

    private UrlUtils() {

    }

    public static boolean isValid(String urlName) {
        return new UrlValidator(ALLOW_LOCAL_URLS).isValid(urlName);
    }

    public static String normalize(String urlName) throws MalformedURLException {
        URL url = new URL(urlName);
        String port = url.getPort() == -1 ? "" : ":" + url.getPort();
        return url.getProtocol() + "://" + url.getHost() + port;
    }

    public static boolean isReachable(Url url) {
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL(url.getName()).openConnection();
            connection.connect();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static UrlCheck runCheck(Url url) {
        HttpResponse<String> response = Unirest.get(url.getName()).asString();
        Document doc = Jsoup.parse(response.getBody());
        int status = response.getStatus();
        String title = doc.title();
        Element h1Element = doc.selectFirst("h1");
        Elements metaDescription = doc.select("meta[name=description]");
        String h1ElementText = h1Element == null ? null : h1Element.text();
        String description = metaDescription.isEmpty() ? null : metaDescription.attr("content");
        return new UrlCheck(status, title, h1ElementText, description, url);
    }
}
