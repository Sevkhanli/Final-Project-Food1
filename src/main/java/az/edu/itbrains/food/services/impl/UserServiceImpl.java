package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.request.UserDTO.RegisterDTO;
import az.edu.itbrains.food.models.Role;
import az.edu.itbrains.food.models.User;
import az.edu.itbrains.food.repositories.RoleRepository;
import az.edu.itbrains.food.repositories.UserRepository;
import az.edu.itbrains.food.services.IUserService;
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
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(RegisterDTO registerDTO) {
        // 1. Email artıq mövcuddursa, istisna at
        if (userRepository.findByEmail(registerDTO.getEmail()) != null) {
            throw new RuntimeException("Bu email (" + registerDTO.getEmail() + ") artıq sistemdə mövcuddur.");
        }

        // 2. DTO → Entity çevrilməsi
        User user = modelMapper.map(registerDTO, User.class);

        // 3. Şifrəni şifrələmək
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        user.setPassword(encodedPassword);

        // 4. Default rol əlavə etmək (ROLE_USER)
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            throw new RuntimeException("Default rol (ROLE_USER) tapılmadı. Zəhmət olmasa 'roles' cədvəlini yoxla.");
        }
        user.getRoles().add(userRole);

        // 5. Bazaya yadda saxla
        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByEmail(username);
    }

    @Override
    public Long countAllUsers() {
        return userRepository.count();
    }
}
