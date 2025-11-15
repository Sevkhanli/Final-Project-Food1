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

    // ⭐ Yeni Bean: AuthenticationFailureHandler - Bloklanmanı ayırmaq üçün
    @Bean
    public AuthenticationFailureHandler customFailureHandler() {
        return (request, response, exception) -> {
            String redirectUrl = "/login?error"; // Default xəta: yanlış email/şifrə

            // Əgər istisna DisabledException-dırsa, xüsusi URL-ə yönləndir
            if (exception instanceof DisabledException) {
                redirectUrl = "/login?blocked"; // Bloklanma halı üçün
            }

            response.sendRedirect(request.getContextPath() + redirectUrl);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // ADMIN-lər üçün dashboard və admin panel icazəsi
                        .requestMatchers("/admin/**", "/dashboard/**").hasRole("ADMIN")

                        // Qeydiyyat və login açıq olsun
                        .requestMatchers("/", "/register", "/login", "/front/**", "/menu", "/about",
                                "/css/**", "/js/**", "/order-success"
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
                        // ⭐ DÜZƏLİŞ: Custom Failure Handler-i qoşuruq
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