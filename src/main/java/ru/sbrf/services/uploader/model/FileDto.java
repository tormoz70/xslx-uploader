package ru.sbrf.services.uploader.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class FileDto {
    private String fileName;
    private String fileMd5;
    private Map<String, SessDto> sesses;
}
