package az.edu.itbrains.food.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationFailureHandler customFailureHandler() {
        return (request, response, exception) -> {
            String redirectUrl = "/login?error";

            if (exception instanceof DisabledException) {
                // DisabledException həm BLOKLANIB, həm də GÖZLƏMƏDƏ statusları üçün atılır.
                // Qeyd: Bu handler yalnız "/login" POST sorğusu uğursuz olduqda işləyir.
                redirectUrl = "/login?blocked";
            }

            response.sendRedirect(request.getContextPath() + redirectUrl);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF-i /verify-otp POST-u üçün deaktiv etmirik, lakin əmin oluruq ki, Thymeleaf-də token var.
                .authorizeHttpRequests(auth -> auth
                        // ADMIN-lər üçün dashboard və admin panel icazəsi
                        .requestMatchers("/admin/**", "/dashboard/**").hasRole("ADMIN")

                        // 🏆 ƏSAS DÜZƏLİŞ: Qeydiyyat, login VƏ OTP TƏSDİQLƏNMƏSİ açıq olsun.
                        // Bura həm GET, həm də POST /verify-otp daxildir.
                        .requestMatchers("/", "/register", "/login", "/front/**", "/menu", "/about",
                                "/css/**", "/js/**", "/order-success",
                                "/verify-otp"
                        ).permitAll()

                        // İstifadəçi üçün qorunan endpoint-lər
                        .requestMatchers("/add-testimonial", "/api/testimonials", "/checkout").authenticated()

                        // Qalan hər şey giriş tələb edir
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/")
                        .failureHandler(customFailureHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }
}