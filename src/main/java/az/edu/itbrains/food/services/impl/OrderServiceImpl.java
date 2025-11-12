package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.DashboardDTO.OrderListDTO;
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
import java.util.stream.Collectors; // Collectors importunu da əlavə edirik

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    // ModelMapper inject-i silinməlidir, çünki istifadə edilməyəcək.

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
        LocalDateTime startOfToday = LocalDateTime.now().with(LocalTime.MIN);
        return orderRepository.countOrdersSince(startOfToday);
    }

    // YENİ METOD 2: Bugün verilən sifarişlərin ümumi gəlirini (məbləğini) hesablayır
    @Override
    public double calculateTodayRevenue() {
        LocalDateTime startOfToday = LocalDateTime.now().with(LocalTime.MIN);
        Double totalRevenue = orderRepository.sumTotalPriceSince(startOfToday);
        return totalRevenue != null ? totalRevenue : 0.0;
    }

    @Override
    public List<Order> getRecentOrders(int limit) {
        return orderRepository.findTop5ByOrderByOrderDateDesc();
    }

    @Override
    @Transactional // Bu annotasiya saxlanılır
    public List<OrderListDTO> getAllOrdersForAdminList() {
        // Bütün sifarişləri (ən yenidən) gətiririk
        List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();

        // ⭐ TƏHLÜKƏSİZ ƏL İLƏ MAPİNQ (Lazy Loading-dən qaçırıq) ⭐
        return orders.stream()
                .map(order -> {
                    OrderListDTO dto = new OrderListDTO();

                    // Entity-dən DTO-ya olan bütün adi (Lazy olmayan) sahələri köçürürük.
                    dto.setId(order.getId());
                    dto.setOrderDate(order.getOrderDate());
                    dto.setOrderStatus(order.getOrderStatus());
                    dto.setFullName(order.getFullName());
                    dto.setPhoneNumber(order.getPhoneNumber());
                    dto.setAddress(order.getAddress());
                    dto.setTotalPrice(order.getTotalPrice());

                    // User və OrderItems (Lazy Loading) kimi əlaqəli obyektlərə toxunmuruq.

                    return dto;
                })
                .collect(Collectors.toList());
    }
}