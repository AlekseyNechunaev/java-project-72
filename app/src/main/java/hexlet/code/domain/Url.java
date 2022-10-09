package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;
import org.apache.commons.validator.routines.UrlValidator;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;

@Entity
public final class Url extends Model {

    @Id
    private long id;

    @NotNull
    private String name;

    @WhenCreated
    private Instant createdAt;

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


    public boolean isValid() {
        return new UrlValidator().isValid(this.name);
    }

    public void toUrlFormat() throws MalformedURLException {
        URL url = new URL(this.name);
        String port = url.getPort() == -1 ? "" : ":" + url.getPort();
        this.name = url.getProtocol() + "://" + url.getHost() + port;
    }
}
