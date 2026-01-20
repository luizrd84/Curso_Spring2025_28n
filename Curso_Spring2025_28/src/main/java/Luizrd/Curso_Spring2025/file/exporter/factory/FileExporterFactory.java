package Luizrd.Curso_Spring2025.file.exporter.factory;

import Luizrd.Curso_Spring2025.exception.BadRequestException;
import Luizrd.Curso_Spring2025.file.exporter.MediaTypes;
import Luizrd.Curso_Spring2025.file.exporter.contract.PersonExporter;
import Luizrd.Curso_Spring2025.file.exporter.impl.CsvExporter;
import Luizrd.Curso_Spring2025.file.exporter.impl.PdfExporter;
import Luizrd.Curso_Spring2025.file.exporter.impl.XlsxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class FileExporterFactory {

    private Logger logger = LoggerFactory.getLogger(FileExporterFactory.class);

    @Autowired
    private ApplicationContext context;

    public PersonExporter getExporter(String acceptHeader) throws Exception {

        if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_XLSX_VALUE)) {
            return context.getBean(XlsxExporter.class);
            //return new XlsxImporter(); - o código acima faz a mesma coisa. Evita o new

        } else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_CSV_VALUE)) {
            return context.getBean(CsvExporter.class);
            //return new CsvImporter();

        } else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_PDF_VALUE)) {
            return context.getBean(PdfExporter.class);

        } else {
            throw new BadRequestException("Invalid File Format!");
        }

    }

}
