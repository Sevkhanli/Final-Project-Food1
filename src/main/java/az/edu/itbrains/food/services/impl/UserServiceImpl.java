package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.DashboardDTO.UserDetailDTO;
import az.edu.itbrains.food.DTOs.DashboardDTO.UserListDTO;
import az.edu.itbrains.food.DTOs.request.UserDTO.RegisterDTO;
import az.edu.itbrains.food.enums.Status;
import az.edu.itbrains.food.models.Role;
import az.edu.itbrains.food.models.User;
import az.edu.itbrains.food.repositories.RoleRepository;
import az.edu.itbrains.food.repositories.UserRepository;
import az.edu.itbrains.food.services.IUserService;
import az.edu.itbrains.food.services.OtpService; // 👈 Yeni Import
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService; // 👈 OtpService inject edildi

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

        // 4. Default rol əlavə etmək
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = roleRepository.findByName("MÜŞTƏRİ");
        }

        if (userRole == null) {
            throw new RuntimeException("Default rol (MÜŞTƏRİ/ROLE_USER) tapılmadı.");
        }
        user.getRoles().add(userRole);

        // 5. Bazaya yadda saxla (Status default olaraq GÖZLƏMƏDƏ olacaq)
        User savedUser = userRepository.save(user);

        // 6. OTP yaradın və göndərin
        otpService.generateAndSendOtp(savedUser.getEmail());

        return savedUser;
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

    // ==========================================================
    // OTP TƏSDİQLƏNMƏSİ ÜÇÜN METOD
    // ==========================================================

    /**
     * İstifadəçinin statusunu email adresinə görə yeniləyir (OTP təsdiqlənməsi üçün).
     */
    @Override
    @Transactional
    public void updateUserStatusByEmail(String email, Status newStatus) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("İstifadəçi tapılmadı. Email: " + email);
        }
        user.setStatus(newStatus);
        userRepository.save(user); // Statusu AKTİV edir
    }

    // ==========================================================
    // ADMIN PANEL METODLARI
    // ==========================================================

    @Override
    @Transactional
    public List<UserListDTO> getAllUsersForAdminList() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> {
                    UserListDTO dto = modelMapper.map(user, UserListDTO.class);

                    String roles = user.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.joining(", "));

                    dto.setRoleNames(roles);
                    dto.setStatus(user.getStatus().name());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<UserListDTO> searchUsers(String query) {
        List<User> users = userRepository.findBySearchQuery(query);

        return users.stream()
                .map(user -> {
                    UserListDTO dto = modelMapper.map(user, UserListDTO.class);
                    String roles = user.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.joining(", "));
                    dto.setRoleNames(roles);
                    dto.setStatus(user.getStatus().name());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDetailDTO getUserDetailsById(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        UserDetailDTO dto = modelMapper.map(user, UserDetailDTO.class);
        dto.setRoleName(user.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("MÜŞTƏRİ"));
        dto.setStatus(user.getStatus().name());
        return dto;
    }

    @Override
    @Transactional
    public void updateUserRole(Long userId, String newRoleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı: ID=" + userId));

        Role newRole = roleRepository.findByName(newRoleName);
        if (newRole == null) {
            throw new RuntimeException("Rol tapılmadı: Ad=" + newRoleName);
        }

        user.getRoles().clear();
        user.getRoles().add(newRole);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public Set<String> getAllRoleNames() {
        return roleRepository.findAll().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public Long countActiveUsers() {
        return userRepository.countByStatus(Status.AKTİV);
    }

    @Override
    public Long countBlockedUsers() {
        return userRepository.countByStatus(Status.BLOKLANIB);
    }

    @Override
    public Long countAdminUsers() {
        return userRepository.countByRoleName("ROLE_ADMIN");
    }

    @Override
    public double getUserCashbackBalance(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("İstifadəçi tapılmadı: " + email);
        }
        return user.getCashbackBalance();
    }
    @Override
    public void updateCashbackBalance(String email, double newBalance) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("İstifadəçi tapılmadı: " + email);
        }
        user.setCashbackBalance(newBalance);
        userRepository.save(user);
    }

    @Override
    public void updateUserStatus(Long userId, String newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı. ID: " + userId));

        Status status = Status.valueOf(newStatus.toUpperCase());

        user.setStatus(status);

        userRepository.save(user);


    }
}