package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.DashboardDTO.UserDetailDTO;
import az.edu.itbrains.food.DTOs.DashboardDTO.UserListDTO;
import az.edu.itbrains.food.DTOs.request.UserDTO.RegisterDTO;
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
    // Status Entity-də olmadığı üçün bu metod implementasiya edilməyəcək, amma interfeysdə qalır.
    // void updateUserStatus(Long userId, String newStatus);

    Set<String> getAllRoleNames();
    Long countAllUsers();    //TODO Dashboard üçün Ümumi İstifadəçi Sayını almaq
    Long countActiveUsers();
    Long countBlockedUsers();
    Long countAdminUsers(); // Yeni
    void updateUserStatus(Long userId, String newStatus);

}