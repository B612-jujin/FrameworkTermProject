package kr.ac.kopo.cjj.myapp.config;

import kr.ac.kopo.cjj.myapp.service.UserAccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountService userAccountService) {
        return userAccountService;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserAccountService userAccountService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userAccountService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                        .disable())
                .authorizeHttpRequests(auth -> auth
                        // 공개 영역
                        .requestMatchers("/", "/home", "/login", "/register", "/css/**", "/js/**", "/images/**", "/h2-console/**").permitAll()
                        // 관리자 전용: 생성/수정/삭제/가시성 토글
                        .requestMatchers(
                                HttpMethod.POST,
                                "/portfolios/new",
                                "/portfolios/*/edit",
                                "/portfolios/*/delete",
                                "/portfolios/*/toggle-visibility"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/portfolios/new",
                                "/portfolios/*/edit",
                                "/portfolios/*/delete"
                        ).hasRole("ADMIN")
                        .requestMatchers("/admin/tech/**").hasRole("ADMIN")
                        // 공개: 목록/상세
                        .requestMatchers(HttpMethod.GET, "/portfolios").permitAll()
                        .requestMatchers(HttpMethod.GET, "/portfolios/**").permitAll()

                        // 사용자/관리자: 피드백 작성
                        .requestMatchers(HttpMethod.POST, "/portfolios/*/feedback").authenticated()
                        .requestMatchers("/settings/**").authenticated()

                        .requestMatchers("/chat", "/ws/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home", true)
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .failureHandler(new LoggingAuthenticationFailureHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .authenticationProvider(authenticationProvider)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
