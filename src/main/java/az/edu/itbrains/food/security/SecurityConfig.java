package az.edu.itbrains.food.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }
}
