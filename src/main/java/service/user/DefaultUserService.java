package service.user;

import model.utils.Role;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repository.user.UserRepository;
import security.jwt.JwtAuthenticationRequest;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DefaultUserService implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DefaultUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.getByEmail(email);
    }

    @Override
    public User createUser(JwtAuthenticationRequest authenticationRequest) {
        User user=new User();
        user.setEmail(authenticationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(authenticationRequest.getPassword()));
        user.setRegisteredDate(LocalDateTime.now());
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_MANAGER);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findOne(String userId) {
        return userRepository.findOne(userId);
    }

    @Override
    public User update(User currentUser) {
        return userRepository.update(currentUser);
    }

    @Override
    public User changeUserRole(String userId, Role role) {
        User user=userRepository.findOne(userId);
        Set<Role> roles=user.getRoles();
        if (roles.contains(role))
            roles.remove(role);
        else 
            roles.add(role);
        user.setRoles(roles);
        return userRepository.update(user);
    }
}
