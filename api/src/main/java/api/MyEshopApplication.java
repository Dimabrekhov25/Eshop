package api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"api", "application", "infrastructure"})
public class MyEshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyEshopApplication.class, args);
    }

}
