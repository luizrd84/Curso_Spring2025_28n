package Luizrd.Curso_Spring2025.file.importer.factory;

import Luizrd.Curso_Spring2025.exception.BadRequestException;
import Luizrd.Curso_Spring2025.file.importer.contract.FileImporter;
import Luizrd.Curso_Spring2025.file.importer.impl.CsvImporter;
import Luizrd.Curso_Spring2025.file.importer.impl.XlsxImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class FileImporterFactory {

    private Logger logger = LoggerFactory.getLogger(FileImporterFactory.class);

    @Autowired
    private ApplicationContext context;

    public FileImporter getImporter(String fileName) throws Exception {
        if (fileName.endsWith(".xlsx")) {
            return context.getBean(XlsxImporter.class);
            //return new XlsxImporter(); - o código acima faz a mesma coisa. Evita o new

        } else if (fileName.endsWith(".csv")) {
            return context.getBean(CsvImporter.class);
            //return new CsvImporter();

        } else {
            throw new BadRequestException("Invalid File Format!");
        }

    }

}
