package Luizrd.Curso_Spring2025.file.importer.contract;

import Luizrd.Curso_Spring2025.data.dto.PersonDTO;

import java.io.InputStream;
import java.util.List;

public interface FileImporter {

    List<PersonDTO> importFile(InputStream inputStream) throws Exception;
}
