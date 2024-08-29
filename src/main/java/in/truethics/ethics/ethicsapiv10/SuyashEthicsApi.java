package in.truethics.ethics.ethicsapiv10;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SuyashEthicsApi {

    public static void main(String[] args) {
        SpringApplication.run(SuyashEthicsApi.class, args);
        System.out.println("Successfully Executed.....");
    }
}
