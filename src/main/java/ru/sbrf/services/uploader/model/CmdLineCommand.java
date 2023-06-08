package ru.sbrf.services.uploader.model;

import java.util.Arrays;

public enum CmdLineCommand {
    UPLOAD, REPORT;

    public static CmdLineCommand parse(String cmd) {
        return Arrays.stream(CmdLineCommand.values()).filter(c->("-" + c.name()).equalsIgnoreCase(cmd))
                .findFirst().orElseThrow(()->new IllegalArgumentException(String.format("Unknown command \"%s\"!", cmd)));
    }
}
