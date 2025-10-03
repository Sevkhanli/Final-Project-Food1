package az.edu.itbrains.food.config;

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
                        .requestMatchers("/", "/register", "/login", "/front/**", "/css/**", "/js/**").permitAll() // Hər kəs üçün açıq
                        .anyRequest().authenticated() // Qalan hər şeyi yalnız daxil olan istifadəçilər üçün bağlayır
                )
                // Login konfiqurasiyası
                .formLogin((form) -> form
                        .loginPage("/login") // Custom Login səhifəmizin yolu
                        .loginProcessingUrl("/login") // Formanın POST sorğusu göndərdiyi yer
                        .defaultSuccessUrl("/") // Uğurlu girişdən sonra ana səhifəyə yönləndir
                        .failureUrl("/login?error") // Uğursuz olduqda xəta mesajı ilə login-ə geri dön
                        .permitAll()
                )
                // Logout konfiqurasiyası
                .logout((logout) -> logout
                        .logoutUrl("/logout") // Çıxış URL-i (Layout-da var idi)
                        .logoutSuccessUrl("/") // Çıxışdan sonra ana səhifəyə yönləndir
                        .permitAll());

        return http.build();
    }
}