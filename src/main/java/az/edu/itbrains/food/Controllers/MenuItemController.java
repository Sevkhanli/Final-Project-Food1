package az.edu.itbrains.food.Controllers;

import az.edu.itbrains.food.DTOs.CartItemDTO;
import az.edu.itbrains.food.DTOs.response.CategoryResponseDTO;
import az.edu.itbrains.food.DTOs.response.MenuItemResponseDTO;
import az.edu.itbrains.food.services.ICategoryService;
import az.edu.itbrains.food.services.IMenuItemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MenuItemController {
    private final ICategoryService iCategoryService;
    private final IMenuItemService iMenuItemService;


@GetMapping("/menu")
public String menu(@RequestParam(required = false) Long categoryId,
                   @RequestParam(required = false, defaultValue = "false") boolean all,
                   Model model, HttpSession session) {

    List<MenuItemResponseDTO> menuItemResponseDTOList;

    if (categoryId != null) {
        menuItemResponseDTOList = iMenuItemService.getMenuItemsByCategoryId(categoryId);
    } else {
        if (all) {
            menuItemResponseDTOList = iMenuItemService.getAllMenuItem();
        } else {
            menuItemResponseDTOList = iMenuItemService.getFirstNMenuItems(6);
        }
    }

    List<CategoryResponseDTO> categoryResponseDTOList = iCategoryService.getAllCategory();

    // Səbət məntiqi buraya əlavə edildi
    List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
    if (cart == null) {
        cart = new ArrayList<>();
        session.setAttribute("cart", cart);
    }
    int cartSize = cart.stream().mapToInt(CartItemDTO::getQuantity).sum();
    model.addAttribute("cartSize", cartSize);


    model.addAttribute("activeCategoryId", categoryId);
    model.addAttribute("menuItems", menuItemResponseDTOList);
    model.addAttribute("categoryItems", categoryResponseDTOList);
    model.addAttribute("allView", all);
    return "menu";
}}
