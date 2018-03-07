package utils;

import lombok.Data;
import model.User;

@Data
public class UserWithJwt {
    
    private String accessToken;
    private User user;

    public UserWithJwt(String accessToken, User user) {
        this.accessToken = accessToken;
        this.user = user;
    }
}
