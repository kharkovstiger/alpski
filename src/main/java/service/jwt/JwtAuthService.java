package service.jwt;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import model.User;

public interface JwtAuthService {
    User getUserWithTokenForGoogle(GoogleIdToken idToken);

    User getUserWithTokenForFacebook(String tokenFacebook);
}
