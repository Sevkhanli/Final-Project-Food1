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

    // 1. PasswordEncoder təyin etmək
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Təhlükəsizlik Qaydaları
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Hansı URL-lərin icazəli olduğunu müəyyən edir
                .authorizeHttpRequests((requests) -> requests

                        // !!! YENİ QAYDA: DASHBOARD QORUMASI. Yalnız 'ROLE_ADMIN' girişi !!!
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Hər kəs üçün açıq (Statik fayllar, Əsas Səhifələr və Uğur Səhifəsi)
                        .requestMatchers("/", "/register", "/login", "/front/**", "/menu", "/about",
                                "/css/**", "/js/**", "/order-success"
                        ).permitAll()

                        // Sifariş endpoint-ləri daxil olmaqla, qeydiyyatdan keçmiş istifadəçilər üçündür
                        .requestMatchers("/add-testimonial", "/api/testimonials", "/checkout").authenticated()

                        // Qalan bütün sorğular üçün də giriş tələb olunur.
                        .anyRequest().authenticated()
                )
                // Login konfiqurasiyası
                .formLogin((form) -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/login?error")
                        .permitAll()
                )
                // Logout konfiqurasiyası
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }
}
