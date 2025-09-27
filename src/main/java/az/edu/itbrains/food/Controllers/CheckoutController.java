package az.edu.itbrains.food.Controllers;

import az.edu.itbrains.food.models.MenuItem;
import az.edu.itbrains.food.models.Order;
import az.edu.itbrains.food.models.OrderItem;
import az.edu.itbrains.food.repositories.OrderRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CheckoutController {
    private final OrderRepository orderRepository;

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam("fullName") String fullName,
                             @RequestParam("phoneNumber") String phoneNumber,
                             @RequestParam("address") String address,
                             HttpSession session) {

        // 1. Səbət məlumatlarını sessiyadan götür
        List<MenuItem> cartItems = (List<MenuItem>) session.getAttribute("cartItems");
        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/cart"; // Səbət boşdursa geri qaytar
        }

        // 2. Yeni bir Sifariş (Order) obyekti yarat
        Order newOrder = new Order();
        newOrder.setFullName(fullName);
        newOrder.setPhoneNumber(phoneNumber);
        newOrder.setAddress(address);
        newOrder.setOrderDate(LocalDateTime.now());

        double total = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (MenuItem item : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(item);
            orderItem.setQuantity(1);
            orderItem.setPrice(item.getPrice());
            orderItem.setOrder(newOrder);

            total += item.getPrice();
            orderItems.add(orderItem);
        }

        newOrder.setTotalPrice(total);
        newOrder.setOrderItems(orderItems);

        // 4. Sifarişi və onunla əlaqəli məhsulları databazaya yaz
        orderRepository.save(newOrder);

        // 5. Səbəti təmizlə
        session.removeAttribute("cartItems");

        // 6. Uğurlu sifariş səhifəsinə yönləndir
        return "redirect:/order-success";
    }}
