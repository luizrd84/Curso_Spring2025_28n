package Luizrd.Curso_Spring2025.services;

import Luizrd.Curso_Spring2025.data.dto.PersonDTO;
import Luizrd.Curso_Spring2025.data.dto.security.AccountCredentialsDTO;
import Luizrd.Curso_Spring2025.data.dto.security.TokenDTO;
import Luizrd.Curso_Spring2025.exception.RequiredObjectIsNullException;
import Luizrd.Curso_Spring2025.model.Person;
import Luizrd.Curso_Spring2025.model.User;
import Luizrd.Curso_Spring2025.repository.UserRepository;
import Luizrd.Curso_Spring2025.security.jwt.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static Luizrd.Curso_Spring2025.mapper.ObjectMapper.parseObject;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository repository;

    Logger logger = LoggerFactory.getLogger(AuthService.class);

    public ResponseEntity<TokenDTO> signIn(AccountCredentialsDTO credentials) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.getUsername(), credentials.getPassword()
                )
        );

        var user = repository.findByUsername(credentials.getUsername());

        if(user == null) {
            throw new UsernameNotFoundException("Username " + credentials.getUsername() + "not found!");
        }

        var token = tokenProvider.createAccessToken(
                credentials.getUsername(), user.getRoles()
        );

        return ResponseEntity.ok(token);
    }

    public ResponseEntity<TokenDTO> refreshToken(String username, String refreshToken) {
        var user = repository.findByUsername(username);
        TokenDTO token;
        if(user != null) {
            token = tokenProvider.refreshToken(refreshToken);
        } else {
            throw new UsernameNotFoundException("Username " + user + " not found.");
        }
        return ResponseEntity.ok(token);
    }

    //Método para gerar senhas, falou que tem que ter um cuidado muito maior se isso estiver na aplicação final.
    private String generateHashedPassword(String password) {
        PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder("", 8, 185000,
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);

        Map<String , PasswordEncoder> encoders = new HashMap<>();
        encoders.put("pbkdf2", pbkdf2Encoder);

        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);

        passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);

        return passwordEncoder.encode(password);
    }

    public AccountCredentialsDTO create(AccountCredentialsDTO user) {
        if (user == null) throw new RequiredObjectIsNullException();

        logger.info("Create one new User!");

        var entity = new User();
        entity.setFullName(user.getFullname());
        entity.setUserName(user.getUsername());
        entity.setPassword(generateHashedPassword(user.getPassword()));
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
        entity.setEnabled(true);

        var dto = repository.save(entity);
        return new AccountCredentialsDTO(dto.getUsername(), dto.getPassword(), dto.getFullName());
    }

}
