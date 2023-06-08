package ru.sbrf.services.uploader.converter;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

import static ru.sbrf.services.uploader.converter.hanlers.DateTimePatterns.detectFormat;

/**
 * @author ayrat
 * 
 * Класс для преобразований из строки в дату.
 * 
 */
public class LocalDateTimeParser {

	/**
	 * Экземпляр класса.
	 */
	private static LocalDateTimeParser instance;

	public static LocalDateTimeParser getInstance() {
		if (instance == null)
			synchronized (LocalDateTimeParser.class) {
				if (instance == null)
					createDateTimeParser();
			}
		return instance;
	}

	private static void createDateTimeParser() {
		instance = new LocalDateTimeParser();
	}

	private LocalDateTimeParser() {
	}


	public LocalDateTime parse(String value, String format) {
		if (StringUtils.isNotEmpty(value)) {
			if (value.equalsIgnoreCase("NOW"))
				return LocalDateTime.now();
			if (value.equalsIgnoreCase("MAX"))
				return LocalDateTime.MAX;
			if (value.equalsIgnoreCase("MIN"))
				return LocalDateTime.MIN;
			try {
				return Types.parseLocalDateTime(value, format);
			} catch (Exception ex) {
				throw new DateParseException("Ошибка разбора даты. Параметры: (" + value + ", " + format + "). Сообщение: " + ex.toString());
			}
		}
		return null;
	}

	public LocalDateTime parse(String value) {
		String datetimeFormat = detectFormat(value);
		if (StringUtils.isEmpty(datetimeFormat))
			throw new DateParseException("Не верная дата: [" + value + "]. Невозможно определить формат даты.");
		return parse(value, datetimeFormat);
	}

}
