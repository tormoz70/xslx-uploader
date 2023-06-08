package ru.sbrf.services.uploader.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessDto {

    //    ID кинот
    private long orgId;

    //    Название сеанса
    private String sessTitle;

    //    Кинозал
    private String showRoom;

    //    Дата/время сеанса
    private LocalDateTime sessDatetime;

    //    Номер ПУ
    private String puNum;

    //    Формат
    private String showFormat;

    private List<TicketDto> tickets;
}
