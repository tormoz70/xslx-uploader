package ru.sbrf.services.uploader.converter.hanlers;


import ru.sbrf.services.uploader.converter.ConvertValueException;
import ru.sbrf.services.uploader.converter.TypeHandler;
import ru.sbrf.services.uploader.converter.TypeHandlerBase;
import ru.sbrf.services.uploader.converter.Types;

public class CharacterHandler extends TypeHandlerBase implements TypeHandler<Character> {

    @Override
    public Character read(Object value, Class<?> targetType) throws ConvertValueException {
        if (value == null)
            return null;
        value = Types.wrapPrimitive(value);
        Class<?> valType = (value == null) ? null : value.getClass();

        if (Types.typeIsDate(valType))
            return null;
        else if (Types.typeIsNumber(valType)) {
            Integer intVal = Types.number2Number((Number)value, Integer.class);
            return (intVal >= 0 && intVal < 10) ? intVal.toString().charAt(0) : null;
        } else if (valType == String.class)
            return ((String) value).charAt(0);
        else if (valType == Character.class)
            return (Character) value;
        throw new ConvertValueException(value, valType, genericType);
    }

    @Override
    public <T> T write(Character value, Class<T> targetType) throws ConvertValueException {
        Class<?> targetTypeWrapped = Types.wrapPrimitiveType(targetType);
        if (Types.typeIsDate(targetTypeWrapped))
            return null;
        else if (targetTypeWrapped == Boolean.class)
            return (T) Types.parseBoolean(value);
        else if (Types.typeIsNumber(targetTypeWrapped)) {
            if(value == null)
                return null;
            return (T) Types.char2Number(value, targetTypeWrapped);
        } else if (targetTypeWrapped == String.class)
            return (T) value;
        else if (targetTypeWrapped == Character.class)
            return (T) value;
        else if (targetTypeWrapped == byte[].class)
            return (T) value;
        else if (targetTypeWrapped.isEnum())
            return (T) Types.parseEnum(""+value, targetTypeWrapped);
        throw new ConvertValueException(value, genericType, targetTypeWrapped);
    }

    public <T> T write(Character value, Class<T> targetType, String format) throws ConvertValueException {
        return write(value, targetType);
    }

}
