package ru.sbrf.services.uploader.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import ru.sbrf.services.uploader.config.PosterConfiguration;
import ru.sbrf.services.uploader.model.FileDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.stream.StreamSupport;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileParser {

    private final PosterConfiguration config;
    private final RowParser rowParser;

    public FileDto parse(File file) {
        log.debug(" - - - try pars file: {}", file.getName());
        String md5;
        try(var ins = new FileInputStream(file)) {
            md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(ins);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try(var ins = new FileInputStream(file)) {
            var fileDto = FileDto.builder()
                    .fileName(file.getName())
                    .fileMd5(md5)
                    .sesses(new LinkedHashMap<>())
                    .build();


            Workbook workbook = WorkbookFactory.create(ins);
            Sheet sheet = workbook.getSheetAt(0);

            StreamSupport.stream(sheet.spliterator(), false)
                    .filter(row -> row.getRowNum() >= (config.getFileMapping().getFirstRow()-1) &&
                            row.getCell(1) != null)
                    .forEach(r -> rowParser.parse(r, fileDto));

            return fileDto;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
