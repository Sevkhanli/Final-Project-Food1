package az.edu.itbrains.food.Controllers;

import az.edu.itbrains.food.DTOs.CartItemDTO;
import az.edu.itbrains.food.DTOs.request.CheckoutRequestDTO;
import az.edu.itbrains.food.models.Order;
import az.edu.itbrains.food.models.OrderItem;
import az.edu.itbrains.food.models.User;
import az.edu.itbrains.food.services.IOrderService;
import az.edu.itbrains.food.services.ICartService;
import az.edu.itbrains.food.services.IUserService;
import az.edu.itbrains.food.services.EmailService; // 👈 YENİ İMPORT
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;
    private final ICartService cartService;
    private final IUserService userService;
    private final EmailService emailService; // 👈 ƏLAVƏ EDİLDİ

    // 1. Sifarişin Verilməsi (POST /checkout)
    @PostMapping("/checkout")
    public String placeOrder(
            @Valid @ModelAttribute("checkoutRequest") CheckoutRequestDTO checkoutRequest,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        // ADIM 1 & 2: Validasiya və Səbət Kontrolu
        if (bindingResult.hasErrors()) {
            List<CartItemDTO> cart = cartService.getCartItems(session);
            model.addAttribute("cartItems", cart);
            model.addAttribute("totalPrice", cartService.calculateTotalPrice(cart));
            model.addAttribute("cartSize", cartService.calculateCartSize(cart));
            model.addAttribute("checkoutRequest", checkoutRequest);
            return "cart";
        }

        List<CartItemDTO> cart = cartService.getCartItems(session);
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart?error=empty";
        }

        // ADIM 3: Order yaradılır
        Order order = new Order();
        order.setFullName(checkoutRequest.getFullName());
        order.setPhoneNumber(checkoutRequest.getPhoneNumber());
        order.setAddress(checkoutRequest.getAddress());
        order.setCustomerEmail(checkoutRequest.getEmail()); // 👈 DTO-dan gələn email yazılır
        order.setTotalPrice(cartService.calculateTotalPrice(cart));
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus("YENİ");

        // ADIM 4: Cari istifadəçi kontrol edilir (Login olunmuşsa)
        String recipientEmail = checkoutRequest.getEmail(); // Əsasən bu emailə gedəcək

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {

            String username = authentication.getName();
            User currentUser = userService.findUserByUsername(username);
            if (currentUser != null) {
                order.setUsers(currentUser);
                // Əgər login olubsa, User-in emailini əsas götürürük
                recipientEmail = currentUser.getEmail();
                order.setCustomerEmail(currentUser.getEmail()); // Bazaya User-in emailini yazırıq
            }
        }

        // ADIM 5: OrderItem-lər yaradılır
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemDTO cartItem : cart) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItemId(cartItem.getId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        // ADIM 6: Order-i sifariş etmək
        try {
            Order savedOrder = orderService.saveOrder(order);

            // ⭐ ADIM 6.5: Sifariş təsdiqlənməsi E-poçtunu göndər
            if (recipientEmail != null && !recipientEmail.isEmpty()) {
                emailService.sendOrderConfirmationEmail(recipientEmail, savedOrder);
            }

            // ADIM 7: Session-dan səbəti sil
            session.removeAttribute("cart");

            // ADIM 8: Success səhifəsinə yönləndir
            return "redirect:/order-success?orderId=" + savedOrder.getId();

        } catch (Exception e) {
            System.err.println("Sifariş xətası: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/cart?error=sifaris_verile_bilmedi";
        }
    }

    // 2. Uğurlu Sifariş Səhifəsi (GET /order-success) - Dəyişiklik yoxdur
    @GetMapping("/order-success")
    public String orderSuccess(
            @RequestParam(value = "orderId", required = false) Long orderId,
            Model model) {

        if (orderId != null) {
            try {
                Order order = orderService.getOrderById(orderId);
                if (order != null) {
                    model.addAttribute("orderId", order.getId());
                    model.addAttribute("address", order.getAddress());
                    model.addAttribute("phoneNumber", order.getPhoneNumber());
                    model.addAttribute("totalPrice", order.getTotalPrice());
                    model.addAttribute("fullName", order.getFullName());
                    model.addAttribute("now", order.getOrderDate());

                }
            } catch (Exception e) {
                System.err.println("Order tapmaq xətası: " + e.getMessage());
            }
        }

        return "orderSuccess";
    }
}