package az.edu.itbrains.food.Controllers;

import az.edu.itbrains.food.DTOs.CartItemDTO;
import az.edu.itbrains.food.DTOs.response.MenuItemResponseDTO;
import az.edu.itbrains.food.services.IMenuItemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    // Menyu səhifəsindən məhsulu səbətə əlavə etmək üçün
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

    // ---

//    ## Səbətdən Məhsulun Silinməsi

//    Bu metod `cart.html` səhifəsindəki "Sil" düyməsinə basıldıqda işə düşəcək.
//    URL-dən məhsulun ID-sini qəbul edir və həmin məhsulu səbətdən silir.

    @GetMapping("/remove-from-cart")
    public String removeFromCart(@RequestParam("id") Long id, HttpSession session) {
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        if (cart != null) {
            // Dövr edib eyni id-li məhsulu tapıb silirik
            cart.removeIf(item -> item.getId().equals(id));
        }
        return "redirect:/cart"; // Səbət səhifəsinə qayıdırıq
    }


//            ## Səbətdə Miqdarın Dəyişdirilməsi
//
//    Bu metod `cart.html` səhifəsindəki "+" və "-" düymələrinə basıldıqda işə düşəcək.
//    URL-dən məhsulun ID-sini və yeni miqdarı (quantity) qəbul edir.

    @GetMapping("/update-quantity")
    public String updateQuantity(@RequestParam("id") Long id, @RequestParam("quantity") int quantity, HttpSession session) {
        // Əgər miqdar 1-dən azdırsa, məhsulu silməyə yönləndiririk
        if (quantity < 1) {
            return "redirect:/remove-from-cart?id=" + id;
        }

        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        if (cart != null) {
            for (CartItemDTO item : cart) {
                if (item.getId().equals(id)) {
                    // Məhsulu tapanda miqdarını yeniləyirik
                    item.setQuantity(quantity);
                    break;
                }
            }
        }
        return "redirect:/cart"; // Səbət səhifəsinə qayıdırıq
    }


//            ## Sifarişin Verilməsi
//
//    Bu metod `cart.html` səhifəsindəki "Ödəniş Et" düyməsinə basıldıqda işə düşəcək.
//    Formadan gələn sifarişçi məlumatlarını qəbul edir və sifarişi emal edir.

//    @PostMapping("/checkout")
//    public String checkout(@RequestParam("fullName") String fullName,
//                           @RequestParam("phoneNumber") String phoneNumber,
//                           @RequestParam("address") String address,
//                           HttpSession session) {
//        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
//
//        if (cart == null || cart.isEmpty()) {
//            return "redirect:/menu"; // Səbət boşdursa, menyuya qayıt
//        }
//
//        // Burda siz sifarişi databazaya yaza və ya başqa bir proses apara bilərsiniz
//        System.out.println("Yeni Sifariş qəbul edildi!");
//        System.out.println("Ad Soyad: " + fullName);
//        System.out.println("Telefon: " + phoneNumber);
//        System.out.println("Ünvan: " + address);
//        System.out.println("Sifarişin məhsulları:");
//        for (CartItemDTO item : cart) {
//            System.out.println("- " + item.getName() + " (" + item.getQuantity() + " ədəd)");
//        }
//
//        // Sifariş tamamlandıqdan sonra səbəti təmizləyirik
//        session.removeAttribute("cart");
//
//        // İstifadəçini təsdiq səhifəsinə yönləndiririk
//        return "redirect:/order-confirmation";
//    }
}