package service.user;

import model.utils.Role;
import model.User;
import security.jwt.JwtAuthenticationRequest;

import java.util.List;

public interface UserService {
    
    User getByEmail(String email);

    User createUser(JwtAuthenticationRequest authenticationRequest);

    List<User> findAll();

    User findOne(String userId);

    User update(User currentUser);

    User changeUserRole(String userId, Role role);
}
