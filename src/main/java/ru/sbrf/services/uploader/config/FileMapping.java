package ru.sbrf.services.uploader.config;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class FileMapping {
    private String sessDatetimeFormat;
    private String saleDatetimeFormat;
    private int firstRow;
    private int firstCell;
    private int cellsTotal;
    private Map<String, Integer> columnsMapping = new LinkedHashMap<>();
}
