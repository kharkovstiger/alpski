package controller;

import model.utils.Role;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import security.AuthorizedUser;
import service.user.UserService;

import java.util.List;

@RestController
@RequestMapping(value = UserController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class UserController {

    static final String REST_URL = "/api/user";

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/all")
    public List<User> getAll() {
        return userService.findAll();
    }

    @PostMapping(value = "/email")
    public User getUserByEmail(@RequestBody String email) {
        return userService.getByEmail(email);
    }

    @GetMapping(value = "/me")
    public User getMe(){
        String userId = AuthorizedUser.id();
        return userService.findOne(userId);
    }

    @PutMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        String userId = AuthorizedUser.id();
        User currentUser = userService.findOne(userId);
        if (currentUser == null) {
            System.out.println("User with id " + userId + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        currentUser.setAlias(user.getAlias()==null?currentUser.getAlias():user.getAlias());
        currentUser.setCountry(user.getCountry()==null?currentUser.getCountry():user.getCountry());
        currentUser.getTeam().setTeamName(user.getTeam().getTeamName()==null?currentUser.getTeam().getTeamName():user.getTeam().getTeamName());

        currentUser = userService.update(currentUser);
        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }
    
    @Secured("ROLE_GM")
    @PostMapping(value = "/changeRole/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changeUserRole(@RequestBody Role role, @PathVariable String userId){
        User user=userService.changeUserRole(userId, role);
        if (user==null)
            return new ResponseEntity(HttpStatus.EXPECTATION_FAILED);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
