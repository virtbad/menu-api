package ch.virtbad.menuapi.endpoints;

import ch.virtbad.menuapi.auth.LoginManager;
import ch.virtbad.menuapi.database.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(value = "custom.interactions.enabled", havingValue = "true")
public class UserEndpoint {

    private final LoginManager login;

    @GetMapping("")
    public User getCurrentUser(@RequestHeader HttpHeaders headers) {
        return login.login(headers);
    }
}
