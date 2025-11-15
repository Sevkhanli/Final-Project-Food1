package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.DashboardDTO.OrderDetailDTO;
import az.edu.itbrains.food.DTOs.DashboardDTO.OrderListDTO;
import az.edu.itbrains.food.models.Order;

import java.util.List;

public interface IOrderService {
    Order saveOrder(Order order);
    Order getOrderById(Long orderId);

    // Detalları gətirən metod
    OrderDetailDTO getOrderDetailsById(Long orderId);

    //TODO Dashboard üçün
    long countTodayOrders();
    double calculateTodayRevenue();
    List<Order> getRecentOrders(int limit);
    //TODO Admin siyahısı üçün sifarişləri gətirən yeni metod
    List<OrderListDTO> getAllOrdersForAdminList();

    void updateOrderStatus(Long orderId, String newStatus);
}