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
    private final int status;

    private final String title;

    private final String h1;

    @Lob
    private final String description;

    @WhenCreated
    private Instant checkDate;

    @ManyToOne
    private Url url;

    public UrlCheck(int status, String title, String h1, String description) {
        this.status = status;
        this.title = title;
        this.h1 = h1;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public int getStatus() {
        return status;
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

    public Instant getCheckDate() {
        return checkDate;
    }

    public Url getUrl() {
        return url;
    }
}
