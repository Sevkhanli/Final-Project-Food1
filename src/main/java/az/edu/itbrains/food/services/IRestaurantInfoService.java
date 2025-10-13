package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.response.RestaurantInfoResponseDTO;

import java.util.List;

public interface IRestaurantInfoService {
    RestaurantInfoResponseDTO getRestaurantInfo(Long id);
    List<RestaurantInfoResponseDTO> getAllRestaurantInfo();
    RestaurantInfoResponseDTO createRestaurantInfo(RestaurantInfoResponseDTO dto);
    RestaurantInfoResponseDTO updateRestaurantInfo(Long id, RestaurantInfoResponseDTO dto);
    void deleteRestaurantInfo(Long id);
}
