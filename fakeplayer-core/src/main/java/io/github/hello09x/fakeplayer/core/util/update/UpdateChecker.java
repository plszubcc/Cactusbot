package io.github.hello09x.fakeplayer.core.util.update;

import com.google.gson.Gson;
import io.github.hello09x.devtools.core.version.InvalidVersionException;
import io.github.hello09x.devtools.core.version.Version;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.NotNull;

public class UpdateChecker {

    private static final Gson gson = new Gson();
    private final String author;
    private final String repository;

    public UpdateChecker(@NotNull String author, @NotNull String repository) {
        super();
        this.author = author;
        this.repository = repository;
    }

    public static boolean isNew(@NotNull String local, @NotNull String remote) {
        Version a;
        Version b;

        try {
            a = Version.parse(local);
            b = Version.parse(remote);
        } catch (InvalidVersionException invalidversionexception) {
            return false;
        }

        return a.compareTo(b) < 0;
    }

    public @NotNull Release getLastRelease() throws IOException, InterruptedException {
        String url = String.format("https://api.github.com/repos/%s/%s/releases/latest", this.author, this.repository);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != 200) {
            int i = response.statusCode();

            throw new IllegalStateException("Not 200 response: " + i + ": " + (String) response.body());
        } else {
            String body = (String) response.body();

            return (Release) UpdateChecker.gson.fromJson(body, Release.class);
        }
    }
}
