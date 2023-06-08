package ru.sbrf.services.uploader.converter.hanlers;

import org.apache.commons.lang3.StringUtils;
import ru.sbrf.services.uploader.converter.*;

import java.lang.reflect.Array;

public class StringHandler extends TypeHandlerBase implements TypeHandler<String> {

    @Override
    public <T> T write(String value, Class<T> targetType, String format) throws ConvertValueException {
        Object result = null;
        Class<?> targetTypeWrapped = Types.wrapPrimitiveType(targetType);
        if (Types.typeIsDate(targetTypeWrapped))
            result = Types.date2Date(Types.parseDate(value, format), targetTypeWrapped);
        else if (Types.typeIsLocalDate(targetTypeWrapped))
            result = Types.date2Date(Types.parseLocalDate(value, format), targetTypeWrapped);
        else if (Types.typeIsLocalDateTime(targetTypeWrapped))
            result = Types.date2Date(Types.parseLocalDateTime(value, format), targetTypeWrapped);
        else if (targetTypeWrapped == Boolean.class)
            result = Types.parseBoolean(value);
        else if (Types.typeIsNumber(targetTypeWrapped)) {
            if(StringUtils.isEmpty(value))
                result = null;
            else
                result = Types.string2Number(value, targetTypeWrapped);
        } else if (targetTypeWrapped == String.class)
            result = value;
        else if (targetTypeWrapped == Character.class)
            result = value;
        else if (targetTypeWrapped == byte[].class)
            result = value.getBytes();
        else if (targetTypeWrapped.isEnum())
            result = Types.parseEnum(value, targetTypeWrapped);
        else if (targetTypeWrapped.isArray() && Types.isPrimitiveOrWrapper(targetTypeWrapped.getComponentType())){
            String[] valStrs = StringUtils.split(value, ",");
            result = Array.newInstance(targetTypeWrapped.getComponentType(), valStrs.length);
            for (int i=0; i<valStrs.length; i++)
                Array.set(result, i, Converter.toType(valStrs[i], targetTypeWrapped.getComponentType()));
        }
        if (result != null)
            return (T) result;
        else {
            if (!targetType.isPrimitive())
                return (T) result;
        }
        throw new ConvertValueException(value, genericType, targetTypeWrapped);
    }

    public <T> T write(String value, Class<T> targetType) throws ConvertValueException {
        return write(value, targetType, null);
    }

}
