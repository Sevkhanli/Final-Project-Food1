package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.CartItemDTO;
import jakarta.servlet.http.HttpSession;
import java.util.List;

public interface ICartService {

    // Session-dan səbət elementlərini almaq
    List<CartItemDTO> getCartItems(HttpSession session);

    // Səbətin ümumi qiymətini hesablamaq
    double calculateTotalPrice(List<CartItemDTO> items);

    // Səbəti təmizləmək (Sifariş bitəndə istifadə olunur)
    void clearCart(HttpSession session);

    // Səbət elementlərinin ümumi miqdarını hesablamaq (Navbar üçün)
    int calculateCartSize(List<CartItemDTO> items);

}