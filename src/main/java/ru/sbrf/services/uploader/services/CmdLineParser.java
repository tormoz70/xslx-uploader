package ru.sbrf.services.uploader.services;

import org.springframework.stereotype.Component;
import ru.sbrf.services.uploader.model.CmdLineCommand;
import ru.sbrf.services.uploader.model.CmdLineParams;

@Component
public class CmdLineParser {

    private void throwError() {
        throw new IllegalArgumentException("Error in command line! Expected: -<commad>");
    }

    public CmdLineParams parse(String[] args) {
        var builder = CmdLineParams.builder();
        if(args.length != 1) {
            throwError();
        }
        builder.command(CmdLineCommand.parse(args[0]));
        return builder.build();
    }

}
