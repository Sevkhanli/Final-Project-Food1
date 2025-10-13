package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.response.RestaurantInfoResponseDTO;
import az.edu.itbrains.food.models.RestaurantInfo;
import az.edu.itbrains.food.repositories.RestaurantInfoRepository;
import az.edu.itbrains.food.services.IRestaurantInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantInfoServiceImpl implements IRestaurantInfoService {

    private final RestaurantInfoRepository restaurantInfoRepository;

    @Override
    public RestaurantInfoResponseDTO getRestaurantInfo(Long id) {
        RestaurantInfo info = restaurantInfoRepository.findById(id).orElse(null);
        if (info == null) {
            return null;
        }
        return new RestaurantInfoResponseDTO(info.getId(), info.getTitle(), info.getDescription());
    }

    @Override
    public List<RestaurantInfoResponseDTO> getAllRestaurantInfo() {
        List<RestaurantInfo> list = restaurantInfoRepository.findAll();
        return list.stream().map(info -> new RestaurantInfoResponseDTO(info.getId(), info.getTitle(), info.getDescription())).collect(Collectors.toList());
    }

    @Override
    public RestaurantInfoResponseDTO createRestaurantInfo(RestaurantInfoResponseDTO dto) {
        RestaurantInfo info = new RestaurantInfo();
        info.setTitle(dto.getTitle());
        info.setDescription(dto.getDescription());
        RestaurantInfo saved = restaurantInfoRepository.save(info);
        return new RestaurantInfoResponseDTO(saved.getId(), saved.getTitle(), saved.getDescription());
    }

    @Override
    public RestaurantInfoResponseDTO updateRestaurantInfo(Long id, RestaurantInfoResponseDTO dto) {
        RestaurantInfo info = restaurantInfoRepository.findById(id).orElse(null);
        if (info == null) {
            return null;
        }
        info.setTitle(dto.getTitle());
        info.setDescription(dto.getDescription());
        RestaurantInfo updated = restaurantInfoRepository.save(info);
        return new RestaurantInfoResponseDTO(updated.getId(), updated.getTitle(), updated.getDescription());
    }

    @Override
    public void deleteRestaurantInfo(Long id) {
        restaurantInfoRepository.deleteById(id);
    }
}