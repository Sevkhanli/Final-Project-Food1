package az.edu.itbrains.food.Controllers;

import az.edu.itbrains.food.models.Order;
import az.edu.itbrains.food.models.User;
import az.edu.itbrains.food.services.IOrderService;
import az.edu.itbrains.food.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MyOrdersController {

    private final IOrderService orderService;
    private final IUserService userService;

    /**
     * İstifadəçinin bütün sifarişlərini göstərir
     */
    @GetMapping("/my-orders")
    public String showMyOrders(Model model) {
        // Cari istifadəçini tap
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User currentUser = userService.findUserByUsername(username);

        if (currentUser == null) {
            return "redirect:/login";
        }

        // İstifadəçinin sifarişlərini tap
        List<Order> userOrders = orderService.getOrdersByUser(currentUser);

        // Model-ə əlavə et
        model.addAttribute("orders", userOrders);
        model.addAttribute("user", currentUser);

        return "my-orders"; // my-orders.html
    }

    /**
     * Konkret sifarişin detallarını göstərir
     */
    @GetMapping("/my-orders/{orderId}")
    public String showOrderDetail(@PathVariable Long orderId, Model model) {
        // Cari istifadəçini tap
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User currentUser = userService.findUserByUsername(username);

        if (currentUser == null) {
            return "redirect:/login";
        }

        // Sifarişi tap
        Order order = orderService.getOrderById(orderId);

        if (order == null) {
            return "redirect:/my-orders?error=notfound";
        }

        // Təhlükəsizlik: Yalnız öz sifarişini görsün
        if (order.getUsers() != null && !order.getUsers().getId().equals(currentUser.getId())) {
            return "redirect:/my-orders?error=unauthorized";
        }

        model.addAttribute("order", order);
        model.addAttribute("user", currentUser);

        return "order-detail"; // order-detail.html
    }
}