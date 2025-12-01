package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.DashboardDTO.UserDetailDTO;
import az.edu.itbrains.food.DTOs.DashboardDTO.UserListDTO;
import az.edu.itbrains.food.DTOs.request.UserDTO.RegisterDTO;
import az.edu.itbrains.food.enums.Status; // Status enum-unu import edin
import az.edu.itbrains.food.models.User;

import java.util.List;
import java.util.Set;

public interface IUserService {
    User registerUser(RegisterDTO registerDTO);

    User findByEmail(String email);

    boolean existsByEmail(String email);
    User findUserByUsername(String username);


    List<UserListDTO> getAllUsersForAdminList();
    List<UserListDTO> searchUsers(String query);

    UserDetailDTO getUserDetailsById(Long id);

    void updateUserRole(Long userId, String newRoleName);

    // Admin paneldən ID ilə status yenilənməsi
    void updateUserStatus(Long userId, String newStatus);

    void updateUserStatusByEmail(String email, Status newStatus);

    Set<String> getAllRoleNames();
    Long countAllUsers();
    Long countActiveUsers();
    Long countBlockedUsers();
    Long countAdminUsers();

    double getUserCashbackBalance(String email);
    void updateCashbackBalance(String email, double newBalance);
}