package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.DashboardDTO.MenuItemCreateDTO;
import az.edu.itbrains.food.DTOs.response.MenuItemResponseDTO;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface IMenuItemService {
    List<MenuItemResponseDTO> getAllMenuItem();
    List<MenuItemResponseDTO> getMenuItemsByCategoryId(Long categoryId);
    List<MenuItemResponseDTO> getFirstNMenuItems(int limit);
    Optional<MenuItemResponseDTO> getMenuItemById(Long id);

    long countActiveMenuItems();

    void createMenuItem(MenuItemCreateDTO menuItemCreateDTO);
}