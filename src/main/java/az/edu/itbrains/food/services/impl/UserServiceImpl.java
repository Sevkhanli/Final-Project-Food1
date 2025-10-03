package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.request.UserDTO.RegisterDTO;
import az.edu.itbrains.food.models.User;
import az.edu.itbrains.food.repositories.UserRepository;
import az.edu.itbrains.food.services.IUserService;

// Custom Exception class-ı yaradacağınızı fərz edirik
// import az.edu.itbrains.food.exceptions.UserAlreadyExistsException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    // ROL üçün müəyyən bir enum və ya constant olduğunu fərz edirik (Məsələn: Role.USER)
    // private final Role defaultRole;

    @Override
    public User registerUser(RegisterDTO registerDTO) {

        // 1. Email-in artıq istifadə olunub-olunmadığını yoxlamaq
        if (userRepository.findByEmail(registerDTO.getEmail()) != null) {

            // DİQQƏT: Bu, əvəzinə özünüzün yaratdığı bir `UserAlreadyExistsException` ola bilər.
            throw new RuntimeException("Bu email (" + registerDTO.getEmail() + ") artıq sistemdə mövcuddur.");
        }

        // 2. DTO-dan User obyektinə konvertasiya etmək
        User user = modelMapper.map(registerDTO, User.class);

        // 3. Şifrəni şifrələmək (Encode)
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        user.setPassword(encodedPassword);

        // 4. Default rolu təyin etmək (Əgər tətbiqinizdə rollar varsa)
        // user.setRole(defaultRole);

        // 5. İstifadəçini bazaya saxlamaq və yaradılmış obyekti qaytarmaq
        return userRepository.save(user);
    }

    // Əlavə olaraq digər metodları da IUserService interfeysinə uyğun tamamlayaq:

    @Override
    public User findByEmail(String email) {
        // Repository-dən istifadə edərək email-ə görə istifadəçini tapın.
        return userRepository.findByEmail(email);
        // findByEmail metodu UserRepository interfeysində olmalıdır
    }

    @Override
    public boolean existsByEmail(String email) {
        // Əgər repository-də existsByEmail metodu varsa, ondan istifadə edin:
        // return userRepository.existsByEmail(email);

        // Yoxdursa, findByEmail ilə yoxlamaq da olar:
        return userRepository.findByEmail(email) != null;
    }
}