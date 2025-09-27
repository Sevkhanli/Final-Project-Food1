//package az.edu.itbrains.food.services.impl;
//
//import az.edu.itbrains.food.DTOs.request.UserDTO.UserLoginDTO;
//import az.edu.itbrains.food.DTOs.request.UserDTO.UserRegisterDTO;
//import az.edu.itbrains.food.models.User;
//import az.edu.itbrains.food.repositories.UserRepository;
//import az.edu.itbrains.food.services.IUserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class UserServiceImpl implements IUserService {
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder; // Dependency Injection ilə alınır
//
//    @Override
//    public User registerUser(UserRegisterDTO registerDTO) {
//        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
//            throw new RuntimeException("Bu e-poçt ünvanı artıq qeydiyyatdan keçib!");
//        }
//        User user = new User();
//        user.setName(registerDTO.getName());
//        user.setSurname(registerDTO.getSurname());
//        user.setEmail(registerDTO.getEmail());
//
//        // Şifrənin HASH edilməsi - Təhlükəsizlik üçün ən vacib addımdır!
//        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
//
//        return userRepository.save(user);
//    }
//
//    @Override
//    public User findByEmail(String email) {
//        return userRepository.findByEmail(email).orElse(null);
//    }
//
//    @Override
//    public boolean existsByEmail(String email) {
//        return userRepository.findByEmail(email).isPresent();
//    }
//
//
//
//}
