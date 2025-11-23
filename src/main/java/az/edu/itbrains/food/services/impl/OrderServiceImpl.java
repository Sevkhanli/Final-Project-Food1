package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.DashboardDTO.OrderDetailDTO;
import az.edu.itbrains.food.DTOs.DashboardDTO.OrderItemDetailDTO;
import az.edu.itbrains.food.DTOs.DashboardDTO.OrderListDTO;
import az.edu.itbrains.food.models.Order;
import az.edu.itbrains.food.repositories.OrderRepository;
import az.edu.itbrains.food.services.IOrderService;
import az.edu.itbrains.food.services.EmailService; // 👈 YENİ İMPORT
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
    private final EmailService emailService; // 👈 ƏLAVƏ EDİLDİ

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

    /**
     * Sifarişin Detallarını (Order və OrderItems) DTO formatında gətirir.
     */
    @Override
    @Transactional(readOnly = true)
    public OrderDetailDTO getOrderDetailsById(Long orderId) {

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return null;
        }

        // OrderItems-in DTO-ya çevrilməsi
        List<OrderItemDetailDTO> itemDetails = order.getOrderItems().stream()
                .map(item -> new OrderItemDetailDTO(
                        // menuItem əlaqəsi vasitəsilə məhsulun adını çəkirik.
                        item.getMenuItem() != null ? item.getMenuItem().getName() : "Naməlum Məhsul",

                        item.getQuantity(),

                        // Price
                        BigDecimal.valueOf(item.getPrice())
                ))
                .collect(Collectors.toList());

        // Əsas OrderDetailDTO-nun doldurulması
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setFullName(order.getFullName());
        dto.setPhoneNumber(order.getPhoneNumber());
        dto.setAddress(order.getAddress());

        // Entity-də double olduğu üçün çevrilir
        dto.setTotalPrice(BigDecimal.valueOf(order.getTotalPrice()));
        dto.setOrderItems(itemDetails);

        return dto;
    }


    @Override
    @Transactional(readOnly = true)
    public long countTodayOrders() {
        LocalDateTime startOfToday = LocalDateTime.now().with(LocalTime.MIN);
        // Repository-də countOrdersSince metodu mövcuddur
        return orderRepository.countOrdersSince(startOfToday);
    }

    /**
     * Dashboard üçün: ANCAQ BUGÜN üçün gəliri hesablayır.
     */
    @Override
    @Transactional(readOnly = true)
    public double calculateTodayRevenue() {
        LocalDateTime startOfToday = LocalDateTime.now().with(LocalTime.MIN);
        // İndi Repository-dəki sumTotalPriceSince metodunu çağırır.
        Double totalRevenue = orderRepository.sumTotalPriceSince(startOfToday);
        return totalRevenue != null ? totalRevenue : 0.0;
    }

    /**
     * BÜTÜN DÖVRÜN ümumi gəlirini hesablayır.
     */
    @Override
    @Transactional(readOnly = true)
    public double calculateTotalRevenue() {
        // Repository-dəki sumTotalRevenue metodunu çağırır.
        Optional<Double> totalRevenue = orderRepository.sumTotalRevenue();
        return totalRevenue.orElse(0.0);
    }


    @Override
    public List<Order> getRecentOrders(int limit) {
        // Repository-də findTop5ByOrderByOrderDateDesc metodu mövcud olduğu fərz edilir
        return orderRepository.findTop5ByOrderByOrderDateDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderListDTO> getAllOrdersForAdminList() {
        // ... (Mövcud implementasiya dəyişdirilmədən qalır)
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

    /**
     * Toplam Sifariş sayğacının implementasiyası.
     */
    @Override
    @Transactional(readOnly = true)
    public long countTotalOrders() {
        return orderRepository.count(); // JpaRepository-nin yerli metodu
    }

    /**
     * Gözləmədə sayğacının implementasiyası.
     */
    @Override
    @Transactional(readOnly = true)
    public long countPendingOrders() {
        // "Gözləmədə" kartı üçün "YENİ" statuslu sifarişləri sayır
        return orderRepository.countByOrderStatus("YENİ");
    }

    /**
     * Çatdırılıb sayğacının implementasiyası.
     */
    @Override
    @Transactional(readOnly = true)
    public long countDeliveredOrders() {
        return orderRepository.countByOrderStatus("ÇATDIRILDI");
    }

    @Override
    @Transactional // Statusu yeniləyən metod
    public void updateOrderStatus(Long orderId, String newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Sifariş tapılmadı: ID=" + orderId));

        // 1. Statusu yenilə
        order.setOrderStatus(newStatus);
        orderRepository.save(order);

        // 2. ⭐ Status Yenilənməsi mailini göndər
        String customerEmail = order.getCustomerEmail();
        String fullName = order.getFullName();

        if (customerEmail != null && !customerEmail.isEmpty()) {
            emailService.sendOrderStatusUpdateEmail(customerEmail, orderId, newStatus, fullName);
        } else {
            System.err.println("Status yenilənməsi maili göndərilmədi. Müştəri email ünvanı tapılmadı.");
        }
    }
}