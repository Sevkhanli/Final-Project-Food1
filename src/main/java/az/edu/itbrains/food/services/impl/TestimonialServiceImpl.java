// src/main/java/az/edu/itbrains/food/services/impl/TestimonialService.java

package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.response.TestimonialResponseDTO;
import az.edu.itbrains.food.models.Testimonial;
import az.edu.itbrains.food.repositories.TestimonialRepository;
import az.edu.itbrains.food.services.ITestimonialService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestimonialServiceImpl implements ITestimonialService {

    private final TestimonialRepository testimonialRepository;
    private final ModelMapper modelMapper; // ModelMapper bean kimi qeyd edilibsə

    @Override
    public List<TestimonialResponseDTO> getAll() {
        return testimonialRepository.findAll()
                .stream()
                .map(testimonial -> modelMapper.map(testimonial, TestimonialResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void save(Testimonial testimonial) {
        testimonialRepository.save(testimonial);
    }

    @Override
    public void delete(Long id) {
        testimonialRepository.deleteById(id);
    }
}