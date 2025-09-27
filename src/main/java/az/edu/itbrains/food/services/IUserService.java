package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.request.UserDTO.UserLoginDTO;
import az.edu.itbrains.food.DTOs.request.UserDTO.UserRegisterDTO;
import az.edu.itbrains.food.models.User;

public interface IUserService {
    User registerUser(UserRegisterDTO registerDTO);

    User findByEmail(String email);

    boolean existsByEmail(String email);
}
