package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.DashboardDTO.OrderDetailDTO;
import az.edu.itbrains.food.DTOs.DashboardDTO.OrderItemDetailDTO;
import az.edu.itbrains.food.DTOs.DashboardDTO.OrderListDTO;
import az.edu.itbrains.food.models.Order;
import az.edu.itbrains.food.models.User;
import az.edu.itbrains.food.repositories.OrderRepository;
import az.edu.itbrains.food.repositories.UserRepository;
import az.edu.itbrains.food.services.IOrderService;
import az.edu.itbrains.food.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private static final double CASHBACK_PERCENTAGE = 0.05;

    @Override
    @Transactional
    public Order saveOrder(Order order) {
        Order savedOrder = orderRepository.save(order);

        if (savedOrder.getUsers() != null) {
            User user = savedOrder.getUsers();

            double cashbackAmount = savedOrder.getTotalPrice() * CASHBACK_PERCENTAGE;

            user.setCashbackBalance(user.getCashbackBalance() + cashbackAmount);

            userRepository.save(user);

            System.out.println("✅ Cashback əlavə edildi: " + cashbackAmount + " AZN - User: " + user.getEmail());
        }

        return savedOrder;
    }

    @Override
    public Order getOrderById(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        return order.orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailDTO getOrderDetailsById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return null;
        }

        List<OrderItemDetailDTO> itemDetails = order.getOrderItems().stream()
                .map(item -> new OrderItemDetailDTO(
                        item.getMenuItem() != null ? item.getMenuItem().getName() : "Naməlum Məhsul",
                        item.getQuantity(),
                        BigDecimal.valueOf(item.getPrice())
                ))
                .collect(Collectors.toList());

        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setFullName(order.getFullName());
        dto.setPhoneNumber(order.getPhoneNumber());
        dto.setAddress(order.getAddress());
        dto.setTotalPrice(BigDecimal.valueOf(order.getTotalPrice()));
        dto.setOrderItems(itemDetails);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public long countTodayOrders() {
        LocalDateTime startOfToday = LocalDateTime.now().with(LocalTime.MIN);
        return orderRepository.countOrdersSince(startOfToday);
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateTodayRevenue() {
        LocalDateTime startOfToday = LocalDateTime.now().with(LocalTime.MIN);
        Double totalRevenue = orderRepository.sumTotalPriceSince(startOfToday);
        return totalRevenue != null ? totalRevenue : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateTotalRevenue() {
        Optional<Double> totalRevenue = orderRepository.sumTotalRevenue();
        return totalRevenue.orElse(0.0);
    }

    @Override
    public List<Order> getRecentOrders(int limit) {
        return orderRepository.findTop5ByOrderByOrderDateDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderListDTO> getAllOrdersForAdminList() {
        List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();

        if (orders == null || orders.isEmpty()) {
            return List.of();
        }

        return orders.stream()
                .map(order -> {
                    OrderListDTO dto = new OrderListDTO();
                    dto.setId(order.getId());
                    dto.setOrderDate(order.getOrderDate());
                    dto.setOrderStatus(order.getOrderStatus());
                    dto.setFullName(order.getFullName());
                    dto.setPhoneNumber(order.getPhoneNumber());
                    dto.setAddress(order.getAddress());

                    double totalPrice = order.getTotalPrice();
                    dto.setTotalPrice(totalPrice > 0.0 ? totalPrice : 0.0);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalOrders() {
        return orderRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countPendingOrders() {
        return orderRepository.countByOrderStatus("YENİ");
    }

    @Override
    @Transactional(readOnly = true)
    public long countDeliveredOrders() {
        return orderRepository.countByOrderStatus("ÇATDIRILDI");
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Sifariş tapılmadı: ID=" + orderId));

        order.setOrderStatus(newStatus);
        orderRepository.save(order);

        String customerEmail = order.getCustomerEmail();
        String fullName = order.getFullName();

        if (customerEmail != null && !customerEmail.isEmpty()) {
            emailService.sendOrderStatusUpdateEmail(customerEmail, orderId, newStatus, fullName);
        } else {
            System.err.println("Status yenilənməsi maili göndərilmədi. Müştəri email ünvanı tapılmadı.");
        }
    }
    @Override
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUsersOrderByOrderDateDesc(user);
    }
}