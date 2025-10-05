package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.response.TestimonialResponseDTO;
import az.edu.itbrains.food.models.Testimonial;

import java.util.List;

public interface ITestimonialService {
    List<TestimonialResponseDTO> getAll();

    // Yeni rəyi saxlamaq üçün
    void save(Testimonial testimonial);

}
