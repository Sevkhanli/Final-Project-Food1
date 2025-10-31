package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.response.MenuItemResponseDTO;
import az.edu.itbrains.food.repositories.MenuItemRepository;
import az.edu.itbrains.food.services.IMenuItemService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements IMenuItemService {
    @Override
    public List<MenuItemResponseDTO> getFirstNMenuItems(int limit) {
        return menuItemRepository.findFirstN(limit)
                .stream()
                .map(item -> modelMapper.map(item, MenuItemResponseDTO.class))
                .toList();
    }


    private final MenuItemRepository menuItemRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<MenuItemResponseDTO> getAllMenuItem() {
        return menuItemRepository.findAll()
                .stream()
                .map(item -> modelMapper.map(item, MenuItemResponseDTO.class))
                .toList();
    }

    @Override
    public List<MenuItemResponseDTO> getMenuItemsByCategoryId(Long categoryId) {
        return menuItemRepository.findByCategory_Id(categoryId)
                .stream()
                .map(item -> modelMapper.map(item, MenuItemResponseDTO.class))
                .toList();

    }
    @Override
    public Optional<MenuItemResponseDTO> getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .map(item -> modelMapper.map(item, MenuItemResponseDTO.class));
    }

    @Override
    public long countActiveMenuItems() {
        return menuItemRepository.countByIsActiveTrue();
    }
}