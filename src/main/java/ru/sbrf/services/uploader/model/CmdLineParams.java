package ru.sbrf.services.uploader.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CmdLineParams {
    private CmdLineCommand command;
}
