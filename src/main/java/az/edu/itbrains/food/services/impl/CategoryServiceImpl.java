package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.request.CategoryRequestDTO;
import az.edu.itbrains.food.DTOs.response.CategoryResponseDTO;
import az.edu.itbrains.food.DTOs.response.MenuItemResponseDTO;
import az.edu.itbrains.food.repositories.CategoryRepository;
import az.edu.itbrains.food.services.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO) {
        return null;
    }

    @Override
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO categoryRequestDTO) {
        return null;
    }

    @Override
    public CategoryResponseDTO getCategoryById(Long id) {
        return null;
    }

    @Override
    public void deleteCategory(Long id) {

    }

    @Override
    public List<CategoryResponseDTO> getAllCategory() {

            return categoryRepository.findAll()
                    .stream()
                    .map(item -> modelMapper.map(item, CategoryResponseDTO.class))
                    .toList();

        }

    }

