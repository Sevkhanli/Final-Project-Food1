package az.edu.itbrains.food.Controllers;

import az.edu.itbrains.food.DTOs.CartItemDTO;
import az.edu.itbrains.food.DTOs.request.OrderRequestDTO; // <<< Yeni DTO
import az.edu.itbrains.food.DTOs.response.MenuItemResponseDTO;
import az.edu.itbrains.food.services.ICartService;
import az.edu.itbrains.food.services.IMenuItemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@SessionAttributes("cart")
public class CartController {

    private final IMenuItemService menuItemService;
    private final ICartService cartService;
    // Qeyd: IOrderService və IUserService bu Controller-dən silindi.

    // Adi məhsulu səbətə əlavə etmək (Mövcud məntiq qaldı)
    @GetMapping("/add-to-cart")
    public String addToCart(@RequestParam("id") Long id, HttpSession session) {
        Optional<MenuItemResponseDTO> itemOptional = menuItemService.getMenuItemById(id);

        if (itemOptional.isPresent()) {
            MenuItemResponseDTO menuItem = itemOptional.get();

            List<CartItemDTO> cart = cartService.getCartItems(session);

            boolean found = false;
            for (CartItemDTO cartItem : cart) {
                if (cartItem.getId().equals(menuItem.getId())) {
                    cartItem.setQuantity(cartItem.getQuantity() + 1);
                    found = true;
                    break;
                }
            }

            if (!found) {
                CartItemDTO newCartItem = new CartItemDTO(
                        menuItem.getId(),
                        menuItem.getName(),
                        menuItem.getPrice(),
                        menuItem.getImageUrl(),
                        1
                );
                cart.add(newCartItem);
            }
            session.setAttribute("cart", cart);
        }
        return "redirect:/menu";
    }

    // Təklifi (Combo) səbətə əlavə etmək (Mövcud məntiq qaldı)
    @GetMapping("/add-offer-to-cart")
    public String addOfferToCart(@RequestParam("offerId") Long offerId,
                                 @RequestParam("id1") Long id1,
                                 @RequestParam("id2") Long id2,
                                 @RequestParam("discount") double discountPercentage,
                                 HttpSession session) {

        if (offerId == null || id1 == null || id2 == null || id1 <= 0 || id2 <= 0) {
            System.err.println("XƏTA: Combo məhsul ID-lərinin hər ikisi və ya Təklif ID-si tapılmadı.");
            return "redirect:/";
        }

        processComboItem(offerId, id1, discountPercentage, session);
        processComboItem(offerId, id2, discountPercentage, session);

        return "redirect:/";
    }

    // Köməkçi Metod: Combo tərkibini səbətə əlavə etmək (Mövcud məntiq qaldı)
    private void processComboItem(Long offerId, Long menuItemId, double discountPercentage, HttpSession session) {
        Optional<MenuItemResponseDTO> itemOptional = menuItemService.getMenuItemById(menuItemId);
        if (itemOptional.isPresent()) {
            MenuItemResponseDTO menuItem = itemOptional.get();
            double originalPrice = menuItem.getPrice();
            double discountedPrice = originalPrice * (1 - (discountPercentage / 100.0));

            List<CartItemDTO> cart = cartService.getCartItems(session);

            CartItemDTO newCartItem = new CartItemDTO(
                    offerId,
                    menuItem.getName() + " (COMBO - " + discountPercentage + "% ENDİRİM)",
                    discountedPrice,
                    menuItem.getImageUrl(),
                    1
            );
            cart.add(newCartItem);
            session.setAttribute("cart", cart);
        }
    }

    // Səbət səhifəsini göstərmək (Yalnız DTO adını düzəltdik)
    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        List<CartItemDTO> cart = cartService.getCartItems(session);

        model.addAttribute("cartItems", cart);

        double totalPrice = cartService.calculateTotalPrice(cart);
        model.addAttribute("totalPrice", totalPrice);

        int cartSize = cartService.calculateCartSize(cart);
        model.addAttribute("cartSize", cartSize);

        // **Düzəliş:** Form üçün OrderRequestDTO-nu ötürürük
        model.addAttribute("checkoutRequest", new OrderRequestDTO());

        return "cart";
    }

    // Miqdarı yeniləmək (Mövcud məntiq qaldı)
    @GetMapping("/update-quantity")
    public String updateQuantity(@RequestParam("id") Long id, @RequestParam("quantity") int quantity, HttpSession session) {

        if (quantity < 1) {
            return "redirect:/remove-from-cart?id=" + id;
        }

        List<CartItemDTO> cart = cartService.getCartItems(session);
        if (!cart.isEmpty()) {
            for (CartItemDTO item : cart) {
                if (item.getId().equals(id)) {
                    item.setQuantity(quantity);
                }
            }
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    // Səbətdən silmək (Mövcud məntiq qaldı)
    @GetMapping("/remove-from-cart")
    public String removeFromCart(@RequestParam("id") Long id, HttpSession session) {
        List<CartItemDTO> cart = cartService.getCartItems(session);
        if (!cart.isEmpty()) {
            cart.removeIf(item -> item.getId().equals(id));
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }
}