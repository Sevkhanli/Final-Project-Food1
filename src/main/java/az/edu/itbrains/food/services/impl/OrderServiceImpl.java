package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.DashboardDTO.OrderDetailDTO;
import az.edu.itbrains.food.DTOs.DashboardDTO.OrderItemDetailDTO;
import az.edu.itbrains.food.DTOs.DashboardDTO.OrderListDTO;
import az.edu.itbrains.food.models.Order;
import az.edu.itbrains.food.repositories.OrderRepository;
import az.edu.itbrains.food.services.IOrderService;
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

        // Sifariş tapılmazsa null qaytarılır
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return null;
        }

        // OrderItems-in DTO-ya çevrilməsi
        List<OrderItemDetailDTO> itemDetails = order.getOrderItems().stream()
                .map(item -> new OrderItemDetailDTO(
                        // ⭐ DÜZƏLİŞ BURADA: menuItem əlaqəsi vasitəsilə məhsulun adını çəkirik.
                        // item.getMenuItem().getName() null olmasın deyə yoxlama əlavə edilir.
                        item.getMenuItem() != null ? item.getMenuItem().getName() : "Naməlum Məhsul",

                        // Quantity
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


    // Yuxarıdakı metodlar eyni qalır...

    @Override
    public long countTodayOrders() {
        LocalDateTime startOfToday = LocalDateTime.now().with(LocalTime.MIN);
        return orderRepository.countOrdersSince(startOfToday);
    }

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
    @Transactional(readOnly = true) // Sadəcə oxuma əməliyyatıdır
    public List<OrderListDTO> getAllOrdersForAdminList() {
        // Bütün sifarişləri (ən yenidən) gətiririk
        List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();

        // Əgər bazada sifariş yoxdursa, boş siyahı qaytar
        if (orders == null || orders.isEmpty()) {
            return List.of(); // Java 9+ üçün, əks halda Collections.emptyList();
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

                    // ⭐ ƏSAS DÜZƏLİŞ: double primitiv tipi olsa da,
                    // əgər hansısa səbəbdən bazadan null gələrsə, xəta verməməsi üçün yoxlama
                    double totalPrice = order.getTotalPrice();
                    dto.setTotalPrice(totalPrice > 0.0 ? totalPrice : 0.0);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional // Verilənlər bazasında dəyişiklik etdiyimiz üçün lazımdır
    public void updateOrderStatus(Long orderId, String newStatus) {

        // 1. Sifarişi ID ilə tapırıq. Tapılmazsa istisna (Exception) atırıq.
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Sifariş tapılmadı: ID=" + orderId));

        // 2. Statusu yeniləyirik
        order.setOrderStatus(newStatus);

        // 3. Dəyişiklikləri bazaya yadda saxlayırıq
        orderRepository.save(order);
    }
}