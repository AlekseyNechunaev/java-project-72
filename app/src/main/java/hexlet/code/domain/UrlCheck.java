package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
public class UrlCheck extends Model {

    @Id
    private long id;

    @NotNull
    private final int statusCode;

    private final String title;

    private final String h1;

    @Lob
    private final String description;

    @WhenCreated
    private Instant createdAt;

    @ManyToOne
    private final Url url;

    public UrlCheck(int statusCode, String title, String h1, String description, Url url) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getTitle() {
        return title;
    }

    public String getH1() {
        return h1;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Url getUrl() {
        return url;
    }
}
