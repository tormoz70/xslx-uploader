package ru.sbrf.services.uploader.converter;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

import static ru.sbrf.services.uploader.converter.hanlers.DateTimePatterns.detectFormat;

/**
 * @author ayrat
 * 
 * Класс для преобразований из строки в дату.
 * 
 */
public class LocalDateParser {

	/**
	 * Экземпляр класса.
	 */
	private static LocalDateParser instance;

	public static LocalDateParser getInstance() {
		if (instance == null)
			synchronized (LocalDateParser.class) {
				if (instance == null)
					createDateTimeParser();
			}
		return instance;
	}

	private static void createDateTimeParser() {
		instance = new LocalDateParser();
	}

	private LocalDateParser() {
	}

	public LocalDate parse(String value, String format) {
		if (StringUtils.isNotEmpty(value)) {
			if (value.equalsIgnoreCase("NOW"))
				return LocalDate.now();
			if (value.equalsIgnoreCase("MAX"))
				return LocalDate.MAX;
			if (value.equalsIgnoreCase("MIN"))
				return LocalDate.MIN;
			try {
				return Types.parseLocalDate(value, format);
			} catch (Exception ex) {
				throw new DateParseException("Ошибка разбора даты. Параметры: (" + value + ", " + format + "). Сообщение: " + ex.toString());
			}
		}
		return null;
	}

	public LocalDate parse(String value) {
		String datetimeFormat = detectFormat(value);
		if (StringUtils.isEmpty(datetimeFormat))
			throw new DateParseException("Не верная дата: [" + value + "]. Невозможно определить формат даты.");
		return parse(value, datetimeFormat);
	}

}
