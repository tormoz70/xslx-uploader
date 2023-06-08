package ru.sbrf.services.uploader.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import ru.sbrf.services.uploader.config.PosterConfiguration;
import ru.sbrf.services.uploader.model.CmdLineCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class RootService {

    private final CmdLineParser cmdLineParser;
    private final InputProcessor inputProcessor;
//    private final PreparedProcessor preparedProcessor;
    private final PosterConfiguration config;


    private String normalizePath(String path) {
        return Paths.get(path).normalize().toString();
    }


    private void preparePaths() {
        try {
            config.getFolders().setInfiles(normalizePath(config.getFolders().getInfiles()) + File.separator);
            FileUtils.forceMkdir(new File(config.getFolders().getInfiles()));
            config.getFolders().setUploaded(normalizePath(config.getFolders().getUploaded()) + File.separator);
            FileUtils.forceMkdir(new File(config.getFolders().getUploaded()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(String[] args) {
        try {
            log.info("Root service starting ...");
            preparePaths();
            var cmdLineParams = cmdLineParser.parse(args);
            log.info("Parameters: {}", cmdLineParams);
            if(cmdLineParams.getCommand() == CmdLineCommand.UPLOAD) {
                inputProcessor.process();
            } else if(cmdLineParams.getCommand() == CmdLineCommand.REPORT) {
                //preparedProcessor.process();
            }
            log.info("Root service finished!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
