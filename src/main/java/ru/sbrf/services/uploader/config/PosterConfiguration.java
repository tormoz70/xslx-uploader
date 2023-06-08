package ru.sbrf.services.uploader.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "poster")
@Data
public class PosterConfiguration {

    private String serviceId;
    private FileMapping fileMapping = new FileMapping();
    private Folders folders = new Folders();

    @Data
    public static class Folders {
        private String infiles = "infiles";
        private String uploaded = "uploaded";
    }

}
