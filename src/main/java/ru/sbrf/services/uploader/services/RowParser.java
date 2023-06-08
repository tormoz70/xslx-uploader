package ru.sbrf.services.uploader.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;
import ru.sbrf.services.uploader.model.FileDto;
import ru.sbrf.services.uploader.model.SessDto;
import ru.sbrf.services.uploader.model.TicketDto;

import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class RowParser {

    private final CellsParser cellsParser;
    private final SessAccessor sessAccessor;

    public void parse(Row row, FileDto fileDto) {
//        log.debug(" - - - - try parse row: {}", row);
        var orgIdAsString = cellsParser.getCellValueAsString(row, "org-id");
        if (Strings.isEmpty(orgIdAsString)) {
            return;
        }
        var sessDto = SessDto.builder()
                .orgId(cellsParser.parseOrgId(row))
                .sessDatetime(cellsParser.parseSessDatetime(row))
                .puNum(cellsParser.parsePuNum(row))
                .sessTitle(cellsParser.parseSessTitle(row))
                .showRoom(cellsParser.parseShowRoom(row))
                .showFormat(cellsParser.getCellValueAsString(row, "show-format"))
                .tickets(new ArrayList<>())
                .build();
        var sess = sessAccessor.getSess(fileDto, sessDto);
        sess.getTickets().add(TicketDto.builder()
                .row(cellsParser.getCellValueAsStringM(row, "section"))
                .row(cellsParser.getCellValueAsStringM(row, "row"))
                .place(cellsParser.getCellValueAsStringM(row, "place"))
                .price(cellsParser.getCellValueAsStringM(row, "price"))
                .discount(cellsParser.getCellValueAsStringM(row, "discount"))
                .saleDatetime(cellsParser.parseSaleDatetime(row))
                .excelRowNum(row.getRowNum())
                .build());
    }
}
