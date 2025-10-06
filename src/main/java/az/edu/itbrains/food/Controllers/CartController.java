package az.edu.itbrains.food.Controllers;

import az.edu.itbrains.food.DTOs.CartItemDTO;
import az.edu.itbrains.food.DTOs.response.MenuItemResponseDTO;
import az.edu.itbrains.food.services.IMenuItemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@SessionAttributes("cart")
public class CartController {

    private final IMenuItemService menuItemService;

    // Menyu səhifəsindən məhsulu səbətə əlavə etmək üçün (Adi məhsullar)
    @GetMapping("/add-to-cart")
    public String addToCart(@RequestParam("id") Long id, HttpSession session) {
        Optional<MenuItemResponseDTO> itemOptional = menuItemService.getMenuItemById(id);
        if (itemOptional.isPresent()) {
            MenuItemResponseDTO menuItem = itemOptional.get();

            List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
                session.setAttribute("cart", cart);
            }

            boolean found = false;
            for (CartItemDTO cartItem : cart) {
                // Adi məhsullar üçün ID məhsulun öz ID-si olaraq qalır
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
        }
        return "redirect:/menu";
    }

    // *** COMBO/TƏKLİF ƏLAVƏ ETMƏ METODU (Combo silinmə məntiqi üçün yeniləndi) ***
    @GetMapping("/add-offer-to-cart")
    public String addOfferToCart(@RequestParam("offerId") Long offerId, // YENİ: Təklifin öz ID-si
                                 @RequestParam("id1") Long id1,
                                 @RequestParam("id2") Long id2,
                                 @RequestParam("discount") double discountPercentage,
                                 HttpSession session) {

        if (offerId == null || id1 == null || id2 == null || id1 <= 0 || id2 <= 0) {
            System.err.println("XƏTA: Combo məhsul ID-lərinin hər ikisi və ya Təklif ID-si tapılmadı.");
            return "redirect:/";
        }

        // Məhsul 1 və Məhsul 2-ni offerId ilə səbətə əlavə edirik
        processComboItem(offerId, id1, discountPercentage, session);
        processComboItem(offerId, id2, discountPercentage, session);

        return "redirect:/";
    }

    // Köməkçi Metod: Combo tərkibini səbətə əlavə etmək
    private void processComboItem(Long offerId, Long menuItemId, double discountPercentage, HttpSession session) {
        Optional<MenuItemResponseDTO> itemOptional = menuItemService.getMenuItemById(menuItemId);

        if (itemOptional.isPresent()) {
            MenuItemResponseDTO menuItem = itemOptional.get();
            double originalPrice = menuItem.getPrice();
            // Hər iki Combo məhsuluna endirimi tətbiq edirik
            double discountedPrice = originalPrice * (1 - (discountPercentage / 100.0));

            List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
                session.setAttribute("cart", cart);
            }

            // Yeni CartItem yaradırıq.
            // ID olaraq MenuItem ID-si deyil, Offer ID-si istifadə olunur.
            CartItemDTO newCartItem = new CartItemDTO(
                    offerId, // *** ƏSAS DƏYİŞİKLİK: Combo silinməsi üçün Offer ID-ni saxlayır ***
                    menuItem.getName() + " (COMBO - " + discountPercentage + "% ENDİRİM)",
                    discountedPrice,
                    menuItem.getImageUrl(),
                    1
            );
            cart.add(newCartItem);
        }
    }


    // Səbət səhifəsini göstərmək üçün
    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        model.addAttribute("cartItems", cart);

        // Səbətin ümumi qiymətini hesablamaq
        double totalPrice = 0.0;
        if (cart != null) {
            for (CartItemDTO item : cart) {
                totalPrice += item.getPrice() * item.getQuantity();
            }
        }
        model.addAttribute("totalPrice", totalPrice);

        // Səbətdəki məhsulların ümumi sayını hesablamaq
        int cartSize = (cart != null) ? cart.stream().mapToInt(CartItemDTO::getQuantity).sum() : 0;
        model.addAttribute("cartSize", cartSize);

        return "cart";
    }

    // *** SƏBƏTDƏN SİLMƏ METODU (Combo-nu tamamilə silmək üçün yeniləndi) ***
    @GetMapping("/remove-from-cart")
    public String removeFromCart(@RequestParam("id") Long id, HttpSession session) {
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        if (cart != null) {
            // İndi silinən 'id' (məsələn, 1) Combo Təklifinin ID-sidir.
            // Həmin ID-yə sahib olan BÜTÜN məhsulları səbətdən silirik.
            cart.removeIf(item -> item.getId().equals(id));
        }
        return "redirect:/cart";
    }

    // Səbətdə Miqdarın Dəyişdirilməsi
    @GetMapping("/update-quantity")
    public String updateQuantity(@RequestParam("id") Long id, @RequestParam("quantity") int quantity, HttpSession session) {
        // QEYD: Combo məhsulların miqdarını ayrıca dəyişdirmək mürəkkəbdir,
        // çünki ID Combo ID-sidir. Lakin sadəlik üçün bu məntiqi saxlayırıq.

        // Əgər miqdar 1-dən azdırsa, məhsulu silməyə yönləndiririk (Bu Combo-nu tamamilə siləcək)
        if (quantity < 1) {
            return "redirect:/remove-from-cart?id=" + id;
        }

        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        if (cart != null) {
            for (CartItemDTO item : cart) {
                // Həm adi məhsullar (öz ID-ləri), həm də Combo məhsullar (Offer ID-ləri) üçün işləyir
                if (item.getId().equals(id)) {
                    // Məhsulu tapanda miqdarını yeniləyirik
                    item.setQuantity(quantity);
                    break;
                }
            }
        }
        return "redirect:/cart";
    }


//            ## Sifarişin Verilməsi
//            (Köhnə kodu olduğu kimi saxlayıram)
//    @PostMapping("/checkout")
//    // ...
//    }
}