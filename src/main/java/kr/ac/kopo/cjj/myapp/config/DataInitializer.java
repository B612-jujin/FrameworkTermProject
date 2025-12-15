package kr.ac.kopo.cjj.myapp.config;

import kr.ac.kopo.cjj.myapp.domain.Portfolio;
import kr.ac.kopo.cjj.myapp.domain.UserAccount;
import kr.ac.kopo.cjj.myapp.service.PortfolioService;
import kr.ac.kopo.cjj.myapp.service.UserAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedData(UserAccountService userAccountService,
                                      PortfolioService portfolioService) {
        return args -> {
            UserAccount admin = userAccountService.findByUsername("admin")
                    .orElseGet(() -> userAccountService.register("admin", "admin123", "/images/admin.png", "ROLE_ADMIN"));
            UserAccount user1 = userAccountService.findByUsername("user1")
                    .orElseGet(() -> userAccountService.register("user1", "pass123", "/images/user.png"));

            if (portfolioService.getAllPortfolios().isEmpty()) {
                portfolioService.createPortfolio(
                        Portfolio.builder()
                                .title("Landing Page 프로젝트")
                                .description("Thymeleaf와 Spring Boot로 만든 개인 포트폴리오 랜딩 페이지.")
                                .projectLink("https://github.com/example/landing-page")
                                .extraUrl("https://my-landing-page.example.com")
                                .techStack("Spring Boot, Thymeleaf, CSS Grid")
                                .imageUrl("/images/user.png")
                                .build(),
                        admin
                );

                portfolioService.createPortfolio(
                        Portfolio.builder()
                                .title("AI 챗봇 PoC")
                                .description("WebSocket을 이용한 실시간 채팅 챗봇 PoC.")
                                .projectLink("https://github.com/example/chatbot")
                                .extraUrl("https://demo-chatbot.example.com")
                                .techStack("WebSocket, Java, HTML")
                                .randomColor("#4b7bec")
                                .build(),
                        user1
                );
            }
        };
    }
}
