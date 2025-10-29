package az.edu.itbrains.food.Controllers.Dashboard;

import jakarta.servlet.http.HttpServletRequest; // ƏLAVƏ OLUNMALI
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // ƏLAVƏ OLUNMALI
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    // DÜZƏLİŞ: HttpServletRequest daxil edilir və Modelə ötürülür.
    public String index(Model model, HttpServletRequest request){

        // Bu, problem yaradan #httpServletRequest-i əvəz edir.
        model.addAttribute("currentUri", request.getRequestURI());

        return "dashboard/index";
    }
}