package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.models.Order;
import az.edu.itbrains.food.repositories.OrderRepository;
import az.edu.itbrains.food.services.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        return order.orElse(null);
    }

    // YENİ METOD 1: Bugün verilən sifarişlərin sayını hesablayır
    @Override
    public long countTodayOrders() {
        // Bugünün başlanğıc vaxtını (00:00:00) hesablayırıq
        LocalDateTime startOfToday = LocalDateTime.now().with(LocalTime.MIN);
        return orderRepository.countOrdersSince(startOfToday);
    }

    // YENİ METOD 2: Bugün verilən sifarişlərin ümumi gəlirini (məbləğini) hesablayır
    @Override
    public double calculateTodayRevenue() {
        LocalDateTime startOfToday = LocalDateTime.now().with(LocalTime.MIN);

        // Repository-dən gələn dəyər null ola bilər (Əgər sifariş yoxdursa)
        Double totalRevenue = orderRepository.sumTotalPriceSince(startOfToday);

        // Əgər null gələrsə 0.0 qaytarırıq
        return totalRevenue != null ? totalRevenue : 0.0;
    }

    @Override
    public List<Order> getRecentOrders(int limit) {
        return orderRepository.findTop5ByOrderByOrderDateDesc();
    }
}