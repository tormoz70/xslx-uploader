package ru.sbrf.services.uploader.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sbrf.services.uploader.model.SessDto;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbUploader {


    public void upload(Map.Entry<String, SessDto> entry) {
        try {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
