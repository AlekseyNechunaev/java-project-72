package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.List;

@Entity
public final class Url extends Model {

    @Id
    private long id;

    @NotNull
    private String name;

    @WhenCreated
    private Instant createdAt;

    @OneToMany(cascade = CascadeType.ALL)
    List<UrlCheck> checks;

    public Url(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<UrlCheck> getChecks() {
        return checks;
    }

    public boolean isValid() {
        return new UrlValidator().isValid(this.name);
    }

    public void normalize() throws MalformedURLException {
        URL url = new URL(this.name);
        String port = url.getPort() == -1 ? "" : ":" + url.getPort();
        this.name = url.getProtocol() + "://" + url.getHost() + port;
    }

    public boolean isReachable() {
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL(this.name).openConnection();
            connection.connect();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public UrlCheck runCheck() {
        HttpResponse<String> response = Unirest.get(this.name).asString();
        Document doc = Jsoup.parse(response.getBody());
        int status = response.getStatus();
        String title = doc.title();
        Element h1Element = doc.selectFirst("h1");
        Elements metaDescription = doc.select("meta[name=description]");
        String h1ElementText = h1Element == null ? null : h1Element.text();
        String description = metaDescription.isEmpty() ? null : metaDescription.attr("content");
        return new UrlCheck(status, title, h1ElementText, description, this);
    }
}
