package Luizrd.Curso_Spring2025.controllers;

import Luizrd.Curso_Spring2025.services.PersonServices;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;


@RestController
@RequestMapping("/api/test/v1")
public class TestLogController {

    private Logger logger= LoggerFactory.getLogger(TestLogController.class.getName());

    //http://localhost:8080/test
    @GetMapping("/test")
    public String testLog() {
        logger.debug("This is an DEBUG log.");
        logger.info("This is an INFO log.");
        logger.warn("This is an WARN log.");
        logger.error("This is an ERROR log.");


        return "Logs generated successfully!";
    }

}
