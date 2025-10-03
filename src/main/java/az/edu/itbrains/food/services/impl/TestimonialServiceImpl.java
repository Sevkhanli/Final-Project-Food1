package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.response.TestimonialResponseDTO;
import az.edu.itbrains.food.models.Testimonial;
import az.edu.itbrains.food.repositories.TestimonialRepository;
import az.edu.itbrains.food.services.ITestimonialService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestimonialServiceImpl implements ITestimonialService {
    private final TestimonialRepository testimonialRepository;
    private final ModelMapper modelMapper;


    @Override
    public Testimonial save(Testimonial testimonial) {
        return testimonialRepository.save(testimonial);

    }
    @Override
    public List<TestimonialResponseDTO> findAll() {
        return testimonialRepository.findAll()
                .stream()
                .map(t -> modelMapper.map(t, TestimonialResponseDTO.class))
                .toList();
    }
}
