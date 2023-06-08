package ru.sbrf.services.uploader.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sbrf.services.uploader.config.PosterConfiguration;
import ru.sbrf.services.uploader.model.FileDto;

import java.io.File;

@Slf4j
@Component
@RequiredArgsConstructor
public class InputProcessor {

    private final DirectoryScanner directoryScanner;
    private final FileParser fileParser;
    private final DbUploader dbUploader;
    private final PosterConfiguration config;

    public void process() {
        log.info("Start {}.process ...", this.getClass().getName());
        var files = directoryScanner.scan(new File(config.getFolders().getInfiles()));
        log.info(" - found {} files", files.length);

        for (var file : files) {
            log.info(" - - start processing file \"{}\" ...", file);
            var fileDto = fileParser.parse(file);
            processFile(fileDto);
            log.info(" - - file \"{}\" - parsed, contains sess: {}!", file.getName(), fileDto.getSesses().size());
        }
    }

    private void processFile(FileDto fileDto) {
        try{
            fileDto.getSesses().entrySet().forEach(e -> dbUploader.upload(e));
            FileMover.move(config.getFolders().getInfiles(), config.getFolders().getUploaded(), fileDto.getFileName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
