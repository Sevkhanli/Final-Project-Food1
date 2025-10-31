// src/main/java/az/edu/itbrains/food/Controllers/Dashboard/DashboardController.java (SADƏCƏ DİZAYN ÜÇÜN)

package az.edu.itbrains.food.Controllers.Dashboard;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList; // Boş list yaratmaq üçün

@Controller
@RequiredArgsConstructor
public class DashboardController {

    // FUNKSIONALLIQ YOXDUR: Service-ləri hələlik silin və ya istifadə etməyin.
    // private final OrderService orderService;
    // private final ProductService productService;

    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public String index(Model model, HttpServletRequest request){

        // 1. STATİSTİK DUMMY DATALAR
        model.addAttribute("todayRevenue", new BigDecimal("1250.75")); // Nümunə Gəlir
        model.addAttribute("todayOrdersCount", 45L);                     // Nümunə Sifariş Sayı
        model.addAttribute("activeProductCount", 120L);                 // Nümunə Məhsul Sayı
        model.addAttribute("topSellingProduct", "Cheeseburger Menu");   // Nümunə Ən Çox Satılan

        // 2. CƏDVƏL ÜÇÜN BOŞ VƏ YA NÜMUNƏ SİYAHISI
        // Index.html faylının 'recentOrders' listi gözlədiyi üçün boş list göndəririk.
        List<?> recentOrders = new ArrayList<>();

        model.addAttribute("recentOrders", recentOrders);

        // Layoutda aktiv menunu seçmək üçün
        model.addAttribute("currentUri", request.getRequestURI());

        // View yolu: templates/dashboard/index.html
        return "dashboard/index";
    }
}