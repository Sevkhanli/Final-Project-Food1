package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.request.UserDTO.RegisterDTO;
import az.edu.itbrains.food.models.User;

public interface IUserService {
    User registerUser(RegisterDTO registerDTO);

    User findByEmail(String email);

    boolean existsByEmail(String email);
    User findUserByUsername(String username);


    Long countAllUsers();    //TODO Dashboard üçün Ümumi İstifadəçi Sayını almaq
}