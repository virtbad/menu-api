package ch.virtbad.menuapi.auth;

import ch.virtbad.menuapi.database.User;
import ch.virtbad.menuapi.database.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

/**
 * This class handles the authentication process.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "custom.interactions.enabled", havingValue = "true")
public class LoginManager {

    private final AuthValidator validator;
    private final UserRepository users;

    @Value("${custom.external.validation.do:false}")
    private boolean useExternal;
    @Value("${custom.external.validation.url:}")
    private String externalUrl;

    /**
     * Checks whether the necessary headers for login have been provided and then checks whether the provided token is valid for a user in the database. Throws a runtime exception otherwise, containing more information for the requester.
     * @param headers headers of the incoming request
     * @return logged in user
     */
    public User login(HttpHeaders headers) {
        String jwt = extractToken(headers);
        TokenPayload payload = validator.validateToken(jwt);

        Optional<User> user = users.findById(payload.getTag());
        if (user.isPresent()) return user.get();
        else {

            if (!isValidExternally(jwt)) throw new WrongOrganisation(); // Make sure that the user is of the required sub-organisation

            // Create new user
            return users.save(new User(payload.getTag(), payload.getFirstname(), payload.getLastname()));
        }
    }

    /**
     * This method checks whether a token is of a user which is of a specific sub-organisation, specified in the configuration. It can also be disabled.
     * The validation is done by sending a request to an API which only accepts tokens of the right sub-organisation. If that request fails, the token seems to be invalid, hence the user is not of the required organisation.
     * @param token token to check
     * @return whether the user can log in
     */
    private boolean isValidExternally(String token) {
        if (!useExternal) return true; // Skip if not set to use external validation endpoints.

        try {
            HttpResponse<Void> response = HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(externalUrl))
                            .header("Authorization", "Bearer " + token) // Send auth token
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.discarding());

            return response.statusCode() == 200; // Not valid if not OK response

        } catch (IOException | InterruptedException e) {
            throw new ExternalRemoteUnavailable();
        }

    }

    /**
     * Extract the token from the http headers.
     * @param headers headers to extract jwt from
     * @return extracted jwt
     */
    private String extractToken(HttpHeaders headers) {
        if (headers.containsKey("Authorization")){
            String header = headers.getFirst("Authorization");
            if (header == null || !header.startsWith("Bearer ")) throw new NoTokenProvided();

            return header.substring("Bearer ".length());

        } else throw new NoTokenProvided();
    }

    /**
     * This is thrown when a user tries to authenticate, but is not in the required sub-organisation
     */
    @ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "This user does not belong to the needed organisation.")
    public static class WrongOrganisation extends RuntimeException { }

    /**
     * This is thrown when a user tries to authenticate, but the api of the required sub-organisation is not available.
     */
    @ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE, reason = "External token validation service is unavailable.")
    private static class ExternalRemoteUnavailable extends RuntimeException { }

    /**
     * This is thrown when no token has been provided to authenticate.
     */
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "No access token was provided.")
    private static class NoTokenProvided extends RuntimeException { }
}
