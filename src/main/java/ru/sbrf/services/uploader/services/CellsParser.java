package ru.sbrf.services.uploader.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;
import ru.sbrf.services.uploader.config.PosterConfiguration;
import ru.sbrf.services.uploader.converter.Converter;
import ru.sbrf.services.uploader.converter.LocalDateTimeParser;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CellsParser {
    private final PosterConfiguration config;
    private final DataFormatter dataFormatter = new DataFormatter();

    private Cell getCellByName(Row row, String colName) {
        if(!config.getFileMapping().getColumnsMapping().containsKey(colName)) {
            throw new IllegalArgumentException(String.format("Column %s not defined in application config!", colName));
        }
        int colIndex = config.getFileMapping().getColumnsMapping().get(colName);
        return row.getCell(colIndex);
    }

    public String getCellValueAsString(Row row, String colName) {
        var cell = getCellByName(row, colName);
        try {
            return cell != null ? dataFormatter.formatCellValue(cell) : null;
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Value \"%s\" cannot be extracted in row \"%d\" as STRING!", colName, row.getRowNum()));
        }
    }
    public Long getCellValueAsLong(Row row, String colName) {
        var cell = getCellByName(row, colName);
        try {
            return cell != null ? Converter.toType(dataFormatter.formatCellValue(cell), Long.class) : null;
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Value \"%s\" cannot be extracted in row \"%d\" as NUMERIC!", colName, row.getRowNum()));
        }
    }
    public String getCellValueAsStringM(Row row, String colName) {
        var result = getCellValueAsString(row, colName);
        if(result == null) {
            throw new IllegalArgumentException(String.format("Value \"%s\" cannot be null in row \"%d\"!", colName, row.getRowNum()));
        }
        return result;
    }

    public Long parseOrgId(Row row) {
        var result = getCellValueAsLong(row, "org-id");
        if(result == null) {
            throw new IllegalArgumentException(String.format("Value \"org-id\" cannot be null in row \"%d\"!", row.getRowNum()));
        }
        return result;
    }
    public String parsePuNum(Row row) {
        return getCellValueAsStringM(row, "pu-num");
    }
    public String parseSessTitle(Row row) {
        return getCellValueAsStringM(row, "sess-title");
    }
    public String parseShowRoom(Row row) {
        return getCellValueAsStringM(row, "show-room");
    }

    private LocalDateTime parseDatetime(Row row, String colName, String format) {
        var cell = getCellByName(row, colName);
        if(cell == null) {
            throw new IllegalArgumentException(String.format("Value \"%s\" not found in row \"%d\"!", colName, row.getRowNum()));
        }
        var resultStr = dataFormatter.formatCellValue(cell);
        var result = LocalDateTimeParser.getInstance().parse(resultStr, format);
        if(result == null) {
            throw new IllegalArgumentException(String.format("Error on parsing col \"%s\" in row \"%d\"! Value \"%s\" cannot been parsed with \"%s\" format!",
                    colName, row.getRowNum(), resultStr, format));
        }
        return result;
    }

    public LocalDateTime parseSessDatetime(Row row) {
        return parseDatetime(row, "sess-datetime", config.getFileMapping().getSessDatetimeFormat());
    }
    public LocalDateTime parseSaleDatetime(Row row) {
        return parseDatetime(row, "sale-datetime", config.getFileMapping().getSaleDatetimeFormat());
    }

}
