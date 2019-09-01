package service.jwt;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.stereotype.Service;
import repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DefaultJwtAuthService implements JwtAuthService{

    private final UserRepository userRepository;
    private final FacebookConnectionFactory facebookConnectionFactory;

    @Autowired
    public DefaultJwtAuthService(UserRepository userRepository, FacebookConnectionFactory facebookConnectionFactory) {
        this.userRepository = userRepository;
        this.facebookConnectionFactory = facebookConnectionFactory;
    }

    @Override
    public User getUserWithTokenForGoogle(GoogleIdToken idToken) {
        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        return getCurrentUser(email);
    }

    @Override
    public User getUserWithTokenForFacebook(String tokenFacebook) {
        AccessGrant accessGrant = new AccessGrant(tokenFacebook);
        Connection<Facebook> connection = facebookConnectionFactory.createConnection(accessGrant);
        Facebook facebook = connection.getApi();
        String[] fields = {"id", "email"};
        org.springframework.social.facebook.api.User userProfile =
                facebook.fetchObject("me", org.springframework.social.facebook.api.User.class, fields);
        
        String email = userProfile.getEmail();
        if (email == null) {
            return null;
        }

        return getCurrentUser(email);
    }

    private User getCurrentUser(String email) {
        User user = userRepository.getByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setRegisteredDate(LocalDateTime.now());
            user.setPassword(UUID.randomUUID().toString());
            userRepository.save(user);
        }

        return user;
    }
}
