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

            if (user.getStatus() != Status.AKTİV) {

                String errorMessage;

                if (user.getStatus() == Status.GÖZLƏMƏDƏ) {
                    errorMessage = "Hesabınız təsdiqlənməyib. Lütfən e-poçtunuza göndərilən kodu daxil edin.";
                } else if (user.getStatus() == Status.BLOKLANIB) {
                    errorMessage = "Hesabınız bloklanmışdır!";
                } else {
                    errorMessage = "Hesabınız aktiv deyil.";
                }

                throw new DisabledException(errorMessage);
            }

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