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

        // 4. Default rol əlavə etmək (Fərz edirik ki, default rolunuzun adı "MÜŞTƏRİ" və ya "ROLE_USER"dir)
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            // Əgər ROLE_USER tapılmasa, "MÜŞTƏRİ" ilə də yoxlaya bilərsiniz.
            userRole = roleRepository.findByName("MÜŞTƏRİ");
        }

        if (userRole == null) {
            throw new RuntimeException("Default rol (MÜŞTƏRİ/ROLE_USER) tapılmadı. Zəhmət olmasa 'roles' cədvəlini yoxla.");
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

    // ==========================================================
    // ⭐ ADMIN PANEL METODLARI
    // ==========================================================

    /**
     * Bütün istifadəçiləri DTO siyahısı formatında gətirir.
     */
    @Override
    @Transactional
    public List<UserListDTO> getAllUsersForAdminList() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> {
                    UserListDTO dto = modelMapper.map(user, UserListDTO.class);

                    // Rol adlarını birləşdiririk
                    String roles = user.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.joining(", "));

                    dto.setRoleNames(roles);
                    // ⭐ DÜZƏLİŞ: Statusu sabit ("AKTİV") yox, Entity-dən çəkirik
                    dto.setStatus(user.getStatus().name());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Axtarış query-sinə əsasən istifadəçiləri DTO siyahısı formatında gətirir.
     */
    @Override
    @Transactional
    public List<UserListDTO> searchUsers(String query) {
        // UserRepository-də findBySearchQuery() metodunun olduğunu fərz edir.
        List<User> users = userRepository.findBySearchQuery(query);

        return users.stream()
                .map(user -> {
                    UserListDTO dto = modelMapper.map(user, UserListDTO.class);
                    String roles = user.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.joining(", "));
                    dto.setRoleNames(roles);
                    // ⭐ DÜZƏLİŞ: Statusu sabit yox, Entity-dən çəkirik
                    dto.setStatus(user.getStatus().name());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * İstifadəçinin detallarını DTO formatında gətirir.
     */
    @Override
    @Transactional
    public UserDetailDTO getUserDetailsById(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        UserDetailDTO dto = modelMapper.map(user, UserDetailDTO.class);
        // Tək rol adını almaq (rol dəyişmə üçün)
        dto.setRoleName(user.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("MÜŞTƏRİ"));
        // ⭐ DÜZƏLİŞ: Statusu sabit yox, Entity-dən çəkirik
        dto.setStatus(user.getStatus().name());
        return dto;
    }

    /**
     * İstifadəçinin bütün rollarını silib yeni rolu əlavə edir (Rol Yenilənməsi).
     */
    @Override
    @Transactional
    public void updateUserRole(Long userId, String newRoleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı: ID=" + userId));

        // RoleRepository-də findByName(String name) metodu olmalıdır!
        Role newRole = roleRepository.findByName(newRoleName);
        if (newRole == null) {
            throw new RuntimeException("Rol tapılmadı: Ad=" + newRoleName);
        }

        // Bütün köhnə rolları sil
        user.getRoles().clear();

        // Yeni rolu əlavə et
        user.getRoles().add(newRole);

        userRepository.save(user);
    }

    /**
     * Bütün mövcud rolların adlarını gətirir (Dropdown üçün).
     */
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
    public void updateUserStatus(Long userId, String newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı. ID: " + userId));

        // Status stringini ENUM-a çevirmək
        Status status = Status.valueOf(newStatus.toUpperCase());

        // Entity-də statusu yeniləmək
        user.setStatus(status);

        // Save metodu @Transactional sayəsində verilənlər bazasına dəyişikliyi yadda saxlayır
        userRepository.save(user);
    }
}