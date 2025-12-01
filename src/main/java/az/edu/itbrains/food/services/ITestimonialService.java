package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.response.TestimonialResponseDTO;
import az.edu.itbrains.food.models.Testimonial;

import java.util.List;

public interface ITestimonialService {
    List<TestimonialResponseDTO> getAll();

    void save(Testimonial testimonial);

    void delete(Long id);
}
