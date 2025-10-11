package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.CartItemDTO;
import az.edu.itbrains.food.services.ICartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements ICartService {

    @Override
    public List<CartItemDTO> getCartItems(HttpSession session) {
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        return (cart != null) ? cart : new ArrayList<>();
    }

    @Override
    public double calculateTotalPrice(List<CartItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    @Override
    public void clearCart(HttpSession session) {
        session.removeAttribute("cart");
    }

    @Override
    public int calculateCartSize(List<CartItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return 0;
        }
        return items.stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();
    }
}