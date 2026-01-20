package Luizrd.Curso_Spring2025.controllers;

import Luizrd.Curso_Spring2025.controllers.docs.PersonControllerDocs;
import Luizrd.Curso_Spring2025.data.dto.PersonDTO;
import Luizrd.Curso_Spring2025.file.exporter.MediaTypes;
import Luizrd.Curso_Spring2025.services.PersonServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

//@CrossOrigin(origins = "http://localhost:8080") //Uma das formas de usar CORS
@RestController
@RequestMapping("/api/person/v1")
@Tag(name="People", description = "Endpoints for Managing People")
public class PersonController implements PersonControllerDocs {

    @Autowired
    private PersonServices service; //@Autowired evita o codigo abaixo
    //private PersonServices service = new PersonServices();


    //GET 1
    //@CrossOrigin(origins = "http://localhost:8080") //CORS
    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE}
    )
    @Override
    public PersonDTO findById(@PathVariable("id") Long id) {

        return service.findById(id);
    }


    // EXPORT PDF
    @GetMapping(value = "/export/{id}",
        produces = {
            MediaType.APPLICATION_PDF_VALUE
        }
    )
    @Override
    public ResponseEntity<Resource> export(Long id, HttpServletRequest request) {
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);

        Resource file = service.exportPerson(id, acceptHeader);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(acceptHeader))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=person.pdf")
                .body(file);
    }


    //GET ALL
    // @RequestMapping(   method = RequestMethod.GET, //Outro método
    @GetMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE}
    )
    @Override
    public ResponseEntity<PagedModel<EntityModel<PersonDTO>>>  findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC: Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        return ResponseEntity.ok(service.findAll(pageable));
    }


    //EXPORT PAGE
    @GetMapping( value = "/exportPage",
            produces = {MediaTypes.APPLICATION_XLSX_VALUE, MediaTypes.APPLICATION_CSV_VALUE,
            MediaTypes.APPLICATION_PDF_VALUE}
    )
    @Override
    public ResponseEntity<Resource>  exportPage(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC: Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));

        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);

        Resource file = service.exportPage(pageable, acceptHeader);

        Map<String, String> extensionMap = Map.of(
                MediaTypes.APPLICATION_XLSX_VALUE, ".xlsx",
                MediaTypes.APPLICATION_CSV_VALUE, ".csv",
                MediaTypes.APPLICATION_PDF_VALUE, ".pdf"
        );

        var fileExtension = extensionMap.getOrDefault(acceptHeader, "");

        var contentType = acceptHeader != null ? acceptHeader : "application/octet-stream";

        var fileName = "people_exported" + fileExtension;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(file);

    }




    //FIND BY FIRST NAME
    // @RequestMapping(   method = RequestMethod.GET, //Outro método
    @GetMapping(
            value = "/findPeopleByName/{firstName}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE}
    )
    @Override
    public ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findByName(
            @PathVariable("firstName") String firstName,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC: Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        return ResponseEntity.ok(service.findByName(firstName, pageable));
    }




    //POST - Create
    //@CrossOrigin(origins = {"http://localhost:8080", "www.exemplo.com.br"}) //CORS
    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE}
    )
    @Override
    public PersonDTO create(@RequestBody PersonDTO person) {
        return service.create(person);
    }




    //MASS CRIATION
    //@CrossOrigin(origins = {"http://localhost:8080", "www.exemplo.com.br"}) //CORS
    @PostMapping(value = "/massCreation",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE}
    )
    @Override
    public List<PersonDTO> massCreation(@RequestParam("file") MultipartFile file) {
        return service.massCreation(file);
    }



    //UPDATE / PUT
    @PutMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE}
    )
    @Override
    public PersonDTO update(@RequestBody PersonDTO person) {
        return service.update(person);
    }



    //Disable Person
    @PatchMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE}
    )
    @Override
    public PersonDTO disablePerson(@PathVariable Long id) {
        return service.diseblePerson(id);
    }



    //DELETE
    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


}
