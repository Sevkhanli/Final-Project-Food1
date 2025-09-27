package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.request.CategoryRequestDTO;
import az.edu.itbrains.food.DTOs.response.CategoryResponseDTO;

import java.util.List;

public interface ICategoryService {
    CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO);
    CategoryResponseDTO updateCategory(Long id,CategoryRequestDTO categoryRequestDTO);
    CategoryResponseDTO getCategoryById(Long id);
    void deleteCategory(Long id);
    List<CategoryResponseDTO> getAllCategory();


}
