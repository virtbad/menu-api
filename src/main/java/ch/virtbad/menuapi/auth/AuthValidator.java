package ch.virtbad.menuapi.auth;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.creator.TokenValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.ParseException;

/**
 * *Copied from another project*
 * Validates the access tokens.
 */
@Component
@ConditionalOnProperty(value = "custom.interactions.enabled", havingValue = "true")
public class AuthValidator {

    @Value("${custom.microsoft.tenant}")
    private String tenant;

    @Value("${custom.microsoft.client}")
    private String client;

    private TokenValidator validator;

    @EventListener
    public void load(ContextRefreshedEvent event){

        OidcConfiguration config = new OidcConfiguration();
        config.setDiscoveryURI("https://login.microsoftonline.com/" + tenant + "/.well-known/openid-configuration");
        config.setClientId(client);

        this.validator = new TokenValidator(config);

    }

    /**
     * This method validates a token and extracts its body.
     * @param string token to use
     * @return payload of the token
     */
    public TokenPayload validateToken(String string) {

        try {
            JWT jwt = JWTParser.parse(string);
            IDTokenClaimsSet claims = validator.validate(jwt, null);
            return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).convertValue(claims.toJSONObject(), TokenPayload.class);
        } catch (ParseException e) {
            throw new MalformedTokenException();
        } catch (BadJOSEException | JOSEException e) {
            throw new InvalidTokenException(e.getMessage());
        }

    }

    /**
     * Exception when a token is malformed.
     */
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Access Token is Malformed")
    private static class MalformedTokenException extends RuntimeException {}

    /**
     * Exception when a token is invalid.
     */
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Access Token is Invalid")
    private static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String message) {
            super(message);
        }
    }

}
