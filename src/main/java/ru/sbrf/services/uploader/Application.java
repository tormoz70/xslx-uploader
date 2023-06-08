package ru.sbrf.services.uploader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.sbrf.services.uploader.services.RootService;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
//@EnableConfigurationProperties(PosterConfiguration.class)
public class Application implements CommandLineRunner {

    private final RootService rootService;

    public static void main(String[] args) {
        log.info("STARTING THE APPLICATION...");
        SpringApplication.run(Application.class, args);
        log.info("APPLICATION FINISHED!");
    }

    @Override
    public void run(String... args) {
        rootService.run(args);
    }
}
