package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.repositories.OrderItemRepository;
import az.edu.itbrains.food.services.IOrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements IOrderItemService {
    private final OrderItemRepository orderItemRepository;
    @Override
    public Long getTopSellingMenuItemId() {
        return orderItemRepository.findTopSellingMenuItemId();
    }
}