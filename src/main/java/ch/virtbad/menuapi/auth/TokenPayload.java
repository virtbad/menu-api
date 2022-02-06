package ch.virtbad.menuapi.auth;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Locale;

/**
 * *Copied from another project*
 * Holds the data that can be extracted from an access token.
 */
@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TokenPayload {

    @JsonProperty("given_name")
    private String firstname;

    @JsonProperty("family_name")
    private String lastname;

    @Getter(AccessLevel.NONE)
    @JsonProperty("unique_name")
    private String email;

    /**
     * Automatically creates a tag from the email.
     * @return tag of the user
     */
    public String getTag(){
        if (email == null) return null;
        return email.substring(0, email.indexOf('@')).toLowerCase(Locale.ROOT);
    }
}
