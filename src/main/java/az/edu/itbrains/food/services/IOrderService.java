package az.edu.itbrains.food.services;

import az.edu.itbrains.food.models.Order;

public interface IOrderService {
    Order saveOrder(Order order);
    Order getOrderById(Long orderId);
}