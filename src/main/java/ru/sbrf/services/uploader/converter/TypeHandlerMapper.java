package ru.sbrf.services.uploader.converter;


import ru.sbrf.services.uploader.converter.hanlers.*;

public class TypeHandlerMapper {
    private static final TypeHandler[] handlerMap = {
            new ArrayHandler(),
            new StringArrayHandler(),
            new ByteArrayHandler(),
            new BytePArrayHandler(),
            new DateHandler(),
            new LocalDateHandler(),
            new LocalDateTimeHandler(),
            new StringHandler(),
            new CharacterHandler(),
            new NumberHandler(),
            new BooleanHandler()
        };

    public static TypeHandler getHandler(Class<?> type) {
        for(TypeHandler h : handlerMap) {
            if(h.isHandler(type))
                return h;
        }
        return null;
    }
}
