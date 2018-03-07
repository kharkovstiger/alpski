package controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import security.SecUserDetailsService;
import security.jwt.JwtAuthenticationRequest;
import security.jwt.JwtTokenUtil;
import service.user.UserService;
import service.jwt.JwtAuthService;
import utils.AccessToken;
import utils.UserWithJwt;

import javax.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;

@RestController
@RequestMapping(value = AuthController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class AuthController {

    static final String REST_URL = "/api/auth";

    @Value("${jwt.header}")
    private String tokenHeader;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    private final SecUserDetailsService secUserDetailsService;

    private final JwtAuthService jwtAuthService;

    private final UserService userService;
    
    private final String CLIENT_ID_IOS = "88033795714-8s4o1oet0sklm2psk28oksq3j5fcvvh9.apps.googleusercontent.com";
    private final String CLIENT_ID_ANDROID = "194299677119-htja39orrub97t9ita40dsnf253nm3mh.apps.googleusercontent.com";
    private final String CLIENT_ID_WEB = "588816424784-15reots8ukm66tedavq6g2vc98uaq8pj.apps.googleusercontent.com";

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, SecUserDetailsService secUserDetailsService, JwtAuthService jwtAuthService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.secUserDetailsService = secUserDetailsService;
        this.jwtAuthService = jwtAuthService;
        this.userService = userService;
    }

    @PostMapping(value = "/registration", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registration(@Valid @RequestBody JwtAuthenticationRequest authenticationRequest, Device device) {
        String email = authenticationRequest.getEmail();
        boolean valid = org.apache.commons.validator.EmailValidator.getInstance().isValid(email);
        if (!valid) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        if (userService.getByEmail(email)!=null)
            return new ResponseEntity<>("User with this email already exist",HttpStatus.FORBIDDEN);
        User createdUser = userService.createUser(authenticationRequest);
        final UserDetails userDetails = secUserDetailsService.loadUserByUsername(email);
        final String token = jwtTokenUtil.generateToken(userDetails, device);

        UserWithJwt userWithJwt = new UserWithJwt(token, createdUser);

        // Return the token
        return new ResponseEntity<>(userWithJwt, HttpStatus.CREATED);
    }
    
    @PostMapping(value = "/login")
    public ResponseEntity<UserWithJwt> login(@RequestBody JwtAuthenticationRequest authenticationRequest, Device device){

        String emailLowerCase = authenticationRequest.getEmail().toLowerCase();
        // Perform the security
        try {
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            emailLowerCase,
                            authenticationRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        // Reload password post-security so we can generate token
        final UserDetails userDetails = secUserDetailsService.loadUserByUsername(emailLowerCase);
        final String token = jwtTokenUtil.generateToken(userDetails, device);
        User currentUser = userService.getByEmail(emailLowerCase);
        currentUser.setLastLoginDate(LocalDateTime.now());
        userService.update(currentUser);
        
        UserWithJwt userWithJwt = new UserWithJwt(token, currentUser);
        
        return ResponseEntity.ok(userWithJwt);
    }

    @PostMapping(value = "/facebook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAuthenticationTokenFacebook(@RequestBody AccessToken accessToken, Device device) throws AuthenticationException {
        String facebookAccessToken = accessToken.getAccessToken();
        User user = jwtAuthService.getUserWithTokenForFacebook(facebookAccessToken);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Reload password post-security so we can generate token
        final UserDetails userDetails = secUserDetailsService.loadUserByUsername(user.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails, device);

        UserWithJwt userWithJwt = new UserWithJwt(token, userService.getByEmail(user.getEmail()));

        // Return the token
        return ResponseEntity.ok(userWithJwt);
    }

    @PostMapping(value = "/google", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAuthenticationTokenGoogleWithToken(@RequestBody AccessToken accessToken, Device device) 
            throws AuthenticationException, GeneralSecurityException, IOException {

        System.err.println(accessToken.getAccessToken());
        final HttpTransport transport = new NetHttpTransport();
        final JsonFactory jsonFactory = new JacksonFactory();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID_WEB))
//                .setAudience(Arrays.asList(CLIENT_ID_IOS, CLIENT_ID_ANDROID, CLIENT_ID_WEB))
//                .setIssuer("https://accounts.google.com")
                .build();

        GoogleIdToken idToken = verifier.verify(accessToken.getAccessToken());
        if (idToken != null) {
            User user = jwtAuthService.getUserWithTokenForGoogle(idToken);

            // Reload password post-security so we can generate token
            final UserDetails userDetails = secUserDetailsService.loadUserByUsername(user.getEmail());
            final String token = jwtTokenUtil.generateToken(userDetails, device);

            UserWithJwt userWithJwt = new UserWithJwt(token, userService.getByEmail(user.getEmail()));

            // Return the token
            return ResponseEntity.ok(userWithJwt);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
