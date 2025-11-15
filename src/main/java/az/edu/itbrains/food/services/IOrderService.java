package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.DashboardDTO.OrderDetailDTO;
import az.edu.itbrains.food.DTOs.DashboardDTO.OrderListDTO;
import az.edu.itbrains.food.models.Order;
import java.util.List;

public interface IOrderService {
    Order saveOrder(Order order);
    Order getOrderById(Long orderId);

    OrderDetailDTO getOrderDetailsById(Long orderId);

    //TODO Dashboard üçün
    long countTodayOrders();
    // Dashboard: Bu gün gəlir
    double calculateTodayRevenue();

    // ⭐ XƏTANIN HƏLLİ: BU METODU MÜTLƏQ ƏLAVƏ EDİN
    double calculateTotalRevenue();

    List<Order> getRecentOrders(int limit);

    List<OrderListDTO> getAllOrdersForAdminList();
    long countTotalOrders();
    long countPendingOrders();
    long countDeliveredOrders();
    void updateOrderStatus(Long orderId, String newStatus);

}