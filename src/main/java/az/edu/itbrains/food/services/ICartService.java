package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.CartItemDTO;
import jakarta.servlet.http.HttpSession;
import java.util.List;

public interface ICartService {

    List<CartItemDTO> getCartItems(HttpSession session);

    double calculateTotalPrice(List<CartItemDTO> items);

    void clearCart(HttpSession session);

    int calculateCartSize(List<CartItemDTO> items);

}