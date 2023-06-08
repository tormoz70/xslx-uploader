package ru.sbrf.services.uploader.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;
import ru.sbrf.services.uploader.config.PosterConfiguration;
import ru.sbrf.services.uploader.services.FileParser;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@EnableConfigurationProperties
public class ServiceTest {

    @Autowired
    private FileParser fileParser;
    @Autowired
    private PosterConfiguration config;

    @Test
    public void doTestProcess() throws Exception {
        assertNotNull(config);
        File file = ResourceUtils.getFile("classpath:example/ekb_278_20230518_example.xlsx");
        var rslt = fileParser.parse(file);
        assertNotNull(rslt);
    }

}
