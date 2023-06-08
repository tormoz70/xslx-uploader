package ru.sbrf.services.uploader.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {
//    Секция
    private String section;
//    Ряд
    private String row;
//    Место
    private String place;
//    Цена
    private String price;
//    Скидка
    private String discount;
//    Дата/время продажи
    private LocalDateTime saleDatetime;

    private int excelRowNum;

}
