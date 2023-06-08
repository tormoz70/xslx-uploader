package ru.sbrf.services.uploader.converter;


import org.apache.commons.lang3.StringUtils;
import ru.sbrf.services.uploader.converter.hanlers.DateTimePatterns;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;

public class Types {

    public static Object wrapPrimitive(Object value) {
        Class<?> inType = (value == null) ? null : value.getClass();
        if (inType == null || !inType.isPrimitive())
            return value;
        if (inType == boolean.class)
            return Boolean.valueOf((boolean) value);
        if (inType == byte.class)
            return Byte.valueOf((byte) value);
        if (inType == short.class)
            return Short.valueOf((short) value);
        if (inType == int.class)
            return Integer.valueOf((int) value);
        if (inType == long.class)
            return Long.valueOf((long) value);
        if (inType == char.class)
            return Character.valueOf((char) value);
        if (inType == float.class)
            return Float.valueOf((float) value);
        if (inType == double.class)
            return Double.valueOf((double) value);
        return value;
    }

    public static Class<?> wrapPrimitiveType(Class<?> type) {
        if (type != null && !type.isPrimitive())
            return type;
        if (type == boolean.class)
            return Boolean.class;
        if (type == byte.class)
            return Byte.class;
        if (type == short.class)
            return Short.class;
        if (type == int.class)
            return Integer.class;
        if (type == long.class)
            return Long.class;
        if (type == char.class)
            return Character.class;
        if (type == float.class)
            return Float.class;
        if (type == double.class)
            return Double.class;
        return type;
    }

    public static Double parsDouble(String inValue) {
        DecimalFormat fmt = new DecimalFormat("##############################.################");
        DecimalFormatSymbols dfs = fmt.getDecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        fmt.setDecimalFormatSymbols(dfs);
        try {
            Object vval = fmt.parse(inValue);
            if(vval.getClass() == Double.class)
                return (Double)vval;
            if(vval.getClass() == Long.class)
                return ((Long)vval).doubleValue();
            return null;
        } catch (ParseException e) {
            return null;
        }
    }

    public static Number parsNumber(String inValue) {
        DecimalFormat fmt = new DecimalFormat("##############################.################");
        DecimalFormatSymbols dfs = fmt.getDecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        fmt.setDecimalFormatSymbols(dfs);
        try {
            Number vval = fmt.parse(inValue);
            if(vval.getClass() == Double.class || vval.getClass() == Long.class)
                return vval;
            return null;
        } catch (ParseException e) {
            return null;
        }
    }

    public static <T> T string2Number(String inValue, Class<T> targetType) {
        inValue = StringUtils.isEmpty(inValue) ? "0" : inValue.trim().replace(" ", "").replace(',', '.');
        return number2Number(parsNumber(inValue), targetType);
    }

    public static <T> T char2Number(Character inValue, Class<T> targetType) {
        inValue = inValue == null ? '0' : inValue;
        return number2Number(parsNumber(""+inValue), targetType);
    }

    public static <T> T number2Number(Number inValue, Class<T> targetType) {
        if(inValue != null) {
            if (targetType == Byte.class)
                return (T) Byte.valueOf(inValue.byteValue());
            if (targetType == Short.class)
                return (T) Short.valueOf(inValue.shortValue());
            if (targetType == Integer.class)
                return (T) Integer.valueOf(inValue.intValue());
            if (targetType == Long.class)
                return (T) Long.valueOf(inValue.longValue());
            if (targetType == Float.class)
                return (T) Float.valueOf(inValue.floatValue());
            if (targetType == Double.class)
                return (T) Double.valueOf(inValue.doubleValue());
            if (targetType == BigDecimal.class)
                return (T) BigDecimal.valueOf(inValue.doubleValue());
            if (targetType == BigInteger.class)
                return (T) new BigInteger(inValue.toString());
        }
        return null;
    }

    public static boolean typeIsDate(Class<?> type) {
        return Date.class.isAssignableFrom(type);
    }
    public static boolean typeIsLocalDate(Class<?> type) {
        return LocalDate.class.isAssignableFrom(type);
    }
    public static boolean typeIsLocalDateTime(Class<?> type) {
        return LocalDateTime.class.isAssignableFrom(type);
    }

    public static <T> T date2Date(Date inValue, Class<T> targetType) {
        if (targetType == Date.class)
            return (T)new Date(inValue.getTime());
        if (targetType == java.sql.Date.class)
            return (T)new java.sql.Date(inValue.getTime());
        if (targetType == java.sql.Timestamp.class)
            return (T)new java.sql.Timestamp(inValue.getTime());
        if (targetType == LocalDate.class)
            return (T) Instant.ofEpochMilli(inValue.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        if (targetType == LocalDateTime.class)
            return (T) Instant.ofEpochMilli(inValue.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return null;
    }

    public static <T> T date2Date(LocalDate inValue, Class<T> targetType) {
        if (targetType == Date.class)
            return (T) Date.from(inValue.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        if (targetType == java.sql.Date.class)
            return (T)java.sql.Date.valueOf(inValue);
        if (targetType == java.sql.Timestamp.class)
            return (T)java.sql.Timestamp.from(inValue.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        if (targetType == LocalDate.class)
            return (T) inValue;
        if (targetType == LocalDateTime.class)
            return (T) inValue.atStartOfDay();
        return null;
    }

    public static <T> T date2Date(LocalDateTime inValue, Class<T> targetType) {
        if (targetType == Date.class)
            return (T) Date.from(inValue.atZone(ZoneId.systemDefault()).toInstant());
        if (targetType == java.sql.Date.class)
            return (T)java.sql.Date.valueOf(inValue.toLocalDate());
        if (targetType == java.sql.Timestamp.class)
            return (T)java.sql.Timestamp.from(inValue.atZone(ZoneId.systemDefault()).toInstant());
        if (targetType == LocalDate.class)
            return (T) inValue;
        if (targetType == LocalDateTime.class)
            return (T) inValue;
        return null;
    }

    public static boolean typeIsInteger(Class<?> type) {
        if(Arrays.asList(int.class, Integer.class, byte.class, Byte.class,
                short.class, Short.class, long.class, Long.class).contains(type))
            return true;
        return false;
    }

    public static boolean typeIsNumber(Class<?> type) {
        if(Arrays.asList(int.class, byte.class, short.class, long.class, float.class, double.class).contains(type))
            return true;
        return Number.class.isAssignableFrom(type);
    }

    public static boolean typeIsReal(Class<?> type) {
        if(Arrays.asList(float.class, double.class).contains(type))
            return true;
        return Number.class.isAssignableFrom(type);
    }

    public static Date parseDate(String value) throws ConvertValueException {
        try {
            return DateTimeParser.getInstance().parse(value);
        } catch (DateParseException ex) {
            throw new ConvertValueException(value, String.class, Date.class);
        }
    }

    public static void nop(){
        return;
    }

    private static final String CLASS_NAME_PREFIX = "class ";
    private static final String INTERFACE_NAME_PREFIX = "interface ";
    public static String getClassName(Type type) {
        if (type==null) {
            return "";
        }
        String className = type.toString();
        if (className.startsWith(CLASS_NAME_PREFIX)) {
            className = className.substring(CLASS_NAME_PREFIX.length());
        } else if (className.startsWith(INTERFACE_NAME_PREFIX)) {
            className = className.substring(INTERFACE_NAME_PREFIX.length());
        }
        return className;
    }

    public static Class<?> getClass(Type type) {
        String className = getClassName(type);
        if (className.isEmpty()) {
            return null;
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

	public static Date parseDate(String str, String formatStr) throws ConvertValueException {
        if (StringUtils.isEmpty(str))
            return null;
        if (StringUtils.isEmpty(formatStr))
            formatStr = DateTimePatterns.detectFormat(str);
        if (formatStr != null) {
            SimpleDateFormat format = new SimpleDateFormat(formatStr);
            try {
                return format.parse(str);
            } catch (ParseException e) {
                throw new ConvertValueException(str, String.class, Date.class, e);
            }
        } else
            throw new ConvertValueException(str, String.class, Date.class);
	}

	public static Date minDate(){
		return new Date(Long.MIN_VALUE);
	}
	public static Date maxDate(){
		return new Date(Long.MAX_VALUE);
	}

    public static LocalDate parseLocalDate(String str, String formatStr) {
        if(StringUtils.isEmpty(str))
            return null;
        if(StringUtils.isEmpty(formatStr))
            formatStr = DateTimePatterns.detectFormat(str);
        DateTimeFormatter format = DateTimeFormatter.ofPattern(formatStr);
        try {
            return LocalDate.parse(str, format);
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalDate parseLocalDate(String str) {
        return parseLocalDate(str, null);
    }

    public static LocalDateTime parseLocalDateTime(String str, String formatStr) {
        if(StringUtils.isEmpty(str))
            return null;
        if(StringUtils.isEmpty(formatStr))
            formatStr = DateTimePatterns.detectFormat(str);
        DateTimeFormatter format = DateTimeFormatter.ofPattern(formatStr);
        try {
            return LocalDateTime.parse(str, format);
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalDateTime parseLocalDateTime(String str) {
        return parseLocalDateTime(str, null);
    }

    public static Boolean parseBoolean(String value) {
        if(StringUtils.isEmpty(value))
            return false;
        return  value.equalsIgnoreCase("true") ||
                value.equalsIgnoreCase("t") ||
                value.equalsIgnoreCase("1") ||
                value.equalsIgnoreCase("yes") ||
                value.equalsIgnoreCase("y");
    }

    public static Boolean parseBoolean(Character value) {
        if(value == null)
            return false;
        return  Character.toLowerCase(value) == 't' ||
                Character.toLowerCase(value) == '1' ||
                Character.toLowerCase(value) == 'y';
    }

    public static <T> T parseEnum(String value, Class<T> type) {
        if(!type.isEnum())
            throw new IllegalArgumentException("Parameter type mast be instance of Enum!");
        Field[] flds = type.getDeclaredFields();
        for(Field f : flds)
            if(StringUtils.compareIgnoreCase(f.getName(), value) == 0)
                try {
                    return (T)f.get(type);
                } catch (IllegalAccessException e) {
                    return null;
                }
        return null;
    }

    public static boolean isPrimitiveOrWrapper(Class<?> type) {
        return (type.isPrimitive() && type != void.class) ||
                type == Double.class || type == Float.class || type == Long.class ||
                type == Integer.class || type == Short.class || type == Character.class ||
                type == Byte.class || type == Boolean.class || type == String.class || type == Date.class;
    }
}
