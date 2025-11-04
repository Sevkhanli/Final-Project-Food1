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

    @Override
    public MenuItemEditDTO getMenuItemForEdit(Long id) {
        // ID ilə məhsulu tapırıq
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Redaktə ediləcək məhsul tapılmadı: ID " + id));

        // Entity-ni Edit DTO-ya çeviririk
        MenuItemEditDTO dto = modelMapper.map(menuItem, MenuItemEditDTO.class);

        // ModelMapper Category-ni CategoryId-yə avtomatik map etməyə bilər,
        // ona görə bunu əl ilə edirik (əgər ModelMapper konfiqurasiyası yoxdursa)
        if (menuItem.getCategory() != null) {
            dto.setCategoryId(menuItem.getCategory().getId());
        }

        return dto;
    }
    @Override
    @Transactional // Birdən çox əməliyyat olmasa da, yeniləmə (UPDATE) üçün məqsədəuyğundur
    public void updateMenuItem(MenuItemEditDTO dto) {
        // 1. Məhsulun mövcud entity-sini tapırıq
        MenuItem existingItem = menuItemRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Yenilənmə üçün məhsul tapılmadı: ID " + dto.getId()));

        // 2. Yeni Kateqoriya obyektini tapırıq
        Category newCategory = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kateqoriya tapılmadı: ID " + dto.getCategoryId()));

        // 3. MÖVCUD OBYEKTİN LAZIMI SAHƏLƏRİNİ DTO-dan gələn dəyərlərlə əl ilə yeniləyirik:
        // Bu, həm Status (Aktiv/Passiv) dəyişikliyini, həm də digər məlumatları yadda saxlayır.

        existingItem.setName(dto.getName());
        existingItem.setPrice(dto.getPrice()); // ✅ Qiymət set olunur
        existingItem.setDescription(dto.getDescription());
        existingItem.setImageUrl(dto.getImageUrl());

        // 🌟 ƏSAS MƏQSƏD: Aktiv/Passiv statusunu yeniləyirik
        existingItem.setIsActive(dto.getIsActive());

        // 4. Kateqoriyanı set edirik (Xətanın qarşısını alır)
        existingItem.setCategory(newCategory);

        // 5. Database-i yeniləyirik
        menuItemRepository.save(existingItem);
    }

    @Override
    @Transactional // Bütün əməliyyat ya işləyir, ya da heç biri işləmir
    public void deleteMenuItem(Long id) {

        // 1. Mövcudluq yoxlaması
        if (!menuItemRepository.existsById(id)) {
            throw new RuntimeException("Silinəcək məhsul tapılmadı: ID " + id);
        }

        // 2. 🏆 ƏSAS ADDIM: Foreign Key xətasının qarşısını almaq üçün
        // Əvvəlcə bu məhsula bağlı olan bütün Sifariş Elementlərini silirik.
        orderItemRepository.deleteByMenuItemId(id);

        // 3. Əsas məhsulu (MenuItem) silirik
        menuItemRepository.deleteById(id);
    }
}