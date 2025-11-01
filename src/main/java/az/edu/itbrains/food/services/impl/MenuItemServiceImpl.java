package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.DashboardDTO.MenuItemCreateDTO;
import az.edu.itbrains.food.DTOs.response.MenuItemResponseDTO;
import az.edu.itbrains.food.models.Category;
import az.edu.itbrains.food.models.MenuItem;
import az.edu.itbrains.food.repositories.CategoryRepository;
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

    private final MenuItemRepository menuItemRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public List<MenuItemResponseDTO> getFirstNMenuItems(int limit) {
        return menuItemRepository.findFirstN(limit)
                .stream()
                .map(item -> modelMapper.map(item, MenuItemResponseDTO.class))
                .toList();
    }

    // 🛑 Düzəliş edilən metod
    @Override
    public List<MenuItemResponseDTO> getAllMenuItem() {
        return menuItemRepository.findAll()
                .stream()
                .map(item -> {
                    // ModelMapper ilə ilkin mapinq aparılır
                    MenuItemResponseDTO dto = modelMapper.map(item, MenuItemResponseDTO.class);

                    // 1. Category adını əl ilə yoxlayıb mapinq edirik (NullPointerException qarşısını alır)
                    if (item.getCategory() != null) {
                        dto.setCategory(item.getCategory().getName());
                    } else {
                        dto.setCategory("Kateqoriyasız");
                    }

                    // 2. isActive fieldinin Null olub-olmaması yoxlanılır (500 xətasının qarşısını alır)
                    // DTO-da 'Boolean isActive' fieldinin normal getter və setter metodlarının (is/get/set)
                    // mövcudluğunu fərz edirik.
                    if (dto.getIsActive() == null) {
                        dto.setIsActive(false); // Default olaraq passiv təyin edirik
                    }

                    // 3. Description üçün Null yoxlaması edirik (Thymeleaf xətasının qarşısını alır)
                    if (dto.getDescription() == null) {
                        dto.setDescription("");
                    }

                    return dto;
                })
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

    @Override
    public void createMenuItem(MenuItemCreateDTO menuItemCreateDTO) {

        // 1. Kateqoriyanı ID vasitəsilə database-dən tapırıq
        Category category = categoryRepository.findById(menuItemCreateDTO.getCategoryId()) // ✅ DÜZƏLİŞ
                .orElseThrow(() -> new RuntimeException("Kateqoriya tapılmadı: ID " + menuItemCreateDTO.getCategoryId())); // ✅ DÜZƏLİŞ

        // 2. DTO-nu Entity-yə çeviririk
        MenuItem menuItem = modelMapper.map(menuItemCreateDTO, MenuItem.class); // ✅ DÜZƏLİŞ

        // 3. Kateqoriya obyektini MenuItem entity-sinə set edirik
        menuItem.setCategory(category);

        // 4. Database-ə yazırıq
        menuItemRepository.save(menuItem);
    }
}