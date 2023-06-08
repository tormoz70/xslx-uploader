package ru.sbrf.services.uploader.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sbrf.services.uploader.model.FileDto;
import ru.sbrf.services.uploader.model.SessDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessAccessor {

    public String buildKey(SessDto sessDto) {
        return sessDto.toString();
    }

    public SessDto getSess(FileDto fileDto, SessDto sessDto) {
        var key = buildKey(sessDto);
        if(fileDto.getSesses().containsKey(key)) {
            return fileDto.getSesses().get(key);
        }
        fileDto.getSesses().put(key, sessDto);
        return sessDto;
    }

}
