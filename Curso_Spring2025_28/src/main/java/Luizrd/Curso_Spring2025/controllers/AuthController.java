package Luizrd.Curso_Spring2025.controllers;

import Luizrd.Curso_Spring2025.controllers.docs.AuthControllerDocs;
import Luizrd.Curso_Spring2025.data.dto.PersonDTO;
import Luizrd.Curso_Spring2025.data.dto.security.AccountCredentialsDTO;
import Luizrd.Curso_Spring2025.exception.RequiredObjectIsNullException;
import Luizrd.Curso_Spring2025.model.Person;
import Luizrd.Curso_Spring2025.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static Luizrd.Curso_Spring2025.mapper.ObjectMapper.parseObject;

@Tag(name = "Authentication Endpoint!")
@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDocs {

    @Autowired
    AuthService service;

    @PostMapping("/signin")
    @Override
    public ResponseEntity<?> signin(@RequestBody AccountCredentialsDTO credentials) {

        if(credentialsInvalid(credentials)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }

        var token = service.signIn(credentials);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }

        return token;
    }

    @PutMapping("/refresh/{username}")
    @Override
    public ResponseEntity<?> refreshToken(@PathVariable("username") String username,
                                          @RequestHeader("Authorization") String refreshToken) {

        if(parametersAreInvalid(username, refreshToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }

        var token = service.refreshToken(username, refreshToken);

        if (token == null) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }

        return token;
    }



    //Muito cuidado com essa parte, não deve ser liberado na aplicação final, a não ser que tenha um controle de segurança bem feito.
    @PostMapping(value = "/createUser",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE}
    )
    @Override
    public AccountCredentialsDTO create(@RequestBody AccountCredentialsDTO credentials) {
        return service.create(credentials);
    }






    private boolean parametersAreInvalid(String username, String refreshTOken) {
        return StringUtils.isBlank(username) || StringUtils.isBlank(refreshTOken);
    }


    private static boolean credentialsInvalid(AccountCredentialsDTO credentials) {
        return credentials == null || StringUtils.isBlank(credentials.getPassword())
                || StringUtils.isBlank(credentials.getUsername());
    }
}
