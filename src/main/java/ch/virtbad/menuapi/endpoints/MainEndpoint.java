package ch.virtbad.menuapi.endpoints;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class MainEndpoint {

    private final long startTime = System.currentTimeMillis();

    @GetMapping("/")
    public VersionResponse getVersion() {
        return new VersionResponse("1.1.3", new Date(startTime));
    }
    @AllArgsConstructor
    private static class VersionResponse {
        private String version;
        private Date started;
    }

    /**
     * Specifies a robots.txt so crawlers can ignore the api.
     */
    @GetMapping(value = "/robots.txt", produces = "text/plain")
    public String crawlerExclusion() {
        return "User-agent: *\nDisallow: /";
    }
}
