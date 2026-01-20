package Luizrd.Curso_Spring2025.file.exporter.contract;

import Luizrd.Curso_Spring2025.data.dto.PersonDTO;
import org.springframework.core.io.Resource;

import java.util.List;

public interface PersonExporter {

    Resource exportPeople(List<PersonDTO> people) throws Exception;

    Resource exportPerson(PersonDTO person) throws Exception;

}
