package az.edu.itbrains.food.security;

import az.edu.itbrains.food.enums.Status;
import az.edu.itbrains.food.models.User;
import az.edu.itbrains.food.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.DisabledException;

@Configuration
public class CustomUserDetailService  implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        if (user != null){

            // ⭐ ƏSAS YOXLAMA: Bloklanma halında DisabledException atılır
            if (user.getStatus() == Status.BLOKLANIB) {
                // Xüsusi xəta handler-də fərqləndirilməsi üçün DisabledException atılır
                throw new DisabledException("Hesabınız bloklanmışdır!");
            }

            // Bloklanmayıbsa, aktiv olaraq girişə icazə veririk
            boolean isEnabled = true;

            org.springframework.security.core.userdetails.User loggedUser = new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    isEnabled,
                    true,
                    true,
                    true,
                    user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList()
            );

            return loggedUser;
        }

        throw new UsernameNotFoundException("İstifadəçi tapılmadı.");
    }
}