package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.response.MenuItemResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IMenuItemService {
    List<MenuItemResponseDTO> getAllMenuItem();
    List<MenuItemResponseDTO> getMenuItemsByCategoryId(Long categoryId);
    List<MenuItemResponseDTO> getFirstNMenuItems(int limit);
    Optional<MenuItemResponseDTO> getMenuItemById(Long id);

    long countActiveMenuItems();

}