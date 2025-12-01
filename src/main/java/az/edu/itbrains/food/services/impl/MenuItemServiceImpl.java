package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.DashboardDTO.MenuItemCreateDTO;
import az.edu.itbrains.food.DTOs.DashboardDTO.MenuItemEditDTO;
import az.edu.itbrains.food.DTOs.response.MenuItemResponseDTO;
import az.edu.itbrains.food.models.Category;
import az.edu.itbrains.food.models.MenuItem;
import az.edu.itbrains.food.repositories.CategoryRepository;
import az.edu.itbrains.food.repositories.MenuItemRepository;
import az.edu.itbrains.food.repositories.OrderItemRepository;
import az.edu.itbrains.food.services.IMenuItemService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements IMenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public List<MenuItemResponseDTO> getFirstNMenuItems(int limit) {
        return menuItemRepository.findFirstNActive(limit)
                .stream()
                .map(item -> modelMapper.map(item, MenuItemResponseDTO.class))
                .toList();
    }

    @Override
    public List<MenuItemResponseDTO> getAllMenuItem() {
        return menuItemRepository.findAll()
                .stream()
                .map(item -> {
                    MenuItemResponseDTO dto = modelMapper.map(item, MenuItemResponseDTO.class);

                    if (item.getCategory() != null) {
                        dto.setCategory(item.getCategory().getName());
                    } else {
                        dto.setCategory("Kateqoriyasız");
                    }

                    if (dto.getIsActive() == null) {
                        dto.setIsActive(false);
                    }

                    if (dto.getDescription() == null) {
                        dto.setDescription("");
                    }

                    return dto;
                })
                .toList();
    }

    @Override
    public List<MenuItemResponseDTO> getMenuItemsByCategoryId(Long categoryId) {
        return menuItemRepository.findByCategory_IdAndIsActiveTrue(categoryId)
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

    // ⭐ DÜZƏLDİLMİŞ METOD ⭐
    @Override
    public void createMenuItem(MenuItemCreateDTO menuItemCreateDTO) {

        Category category = categoryRepository.findById(menuItemCreateDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kateqoriya tapılmadı: ID " + menuItemCreateDTO.getCategoryId()));

        MenuItem menuItem = modelMapper.map(menuItemCreateDTO, MenuItem.class);

        menuItem.setId(null);

        menuItem.setCategory(category);

        menuItemRepository.save(menuItem);
    }

    @Override
    public MenuItemEditDTO getMenuItemForEdit(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Redaktə ediləcək məhsul tapılmadı: ID " + id));

        MenuItemEditDTO dto = modelMapper.map(menuItem, MenuItemEditDTO.class);


        if (menuItem.getCategory() != null) {
            dto.setCategoryId(menuItem.getCategory().getId());
        }

        return dto;
    }
    @Override
    @Transactional
    public void updateMenuItem(MenuItemEditDTO dto) {
        MenuItem existingItem = menuItemRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Yenilənmə üçün məhsul tapılmadı: ID " + dto.getId()));

        Category newCategory = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kateqoriya tapılmadı: ID " + dto.getCategoryId()));


        existingItem.setName(dto.getName());
        existingItem.setPrice(dto.getPrice());
        existingItem.setDescription(dto.getDescription());
        existingItem.setImageUrl(dto.getImageUrl());

        existingItem.setIsActive(dto.getIsActive());

        existingItem.setCategory(newCategory);

        menuItemRepository.save(existingItem);
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long id) {

        if (!menuItemRepository.existsById(id)) {
            throw new RuntimeException("Silinəcək məhsul tapılmadı: ID " + id);
        }


        orderItemRepository.deleteByMenuItemId(id);

        // 3. Əsas məhsulu (MenuItem) silirik
        menuItemRepository.deleteById(id);
    }

    @Override
    public List<MenuItemResponseDTO> getAllActiveMenuItemsForClient() {
        return menuItemRepository.findByIsActiveTrue()
                .stream()
                .map(item -> {
                    MenuItemResponseDTO dto = modelMapper.map(item, MenuItemResponseDTO.class);

                    // Kateqoriya adını və Null yoxlamalarını əlavə edirik (sizdəki kimi)
                    if (item.getCategory() != null) {
                        dto.setCategory(item.getCategory().getName());
                    } else {
                        dto.setCategory("Kateqoriyasız");
                    }

                    // isActive dəyəri zatən true olacaq, amma ehtiyat üçün saxlayaq
                    if (dto.getIsActive() == null) {
                        dto.setIsActive(false);
                    }

                    if (dto.getDescription() == null) {
                        dto.setDescription("");
                    }

                    return dto;
                })
                .toList();
    }
    }
