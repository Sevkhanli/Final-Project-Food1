package az.edu.itbrains.food.Controllers;

import az.edu.itbrains.food.DTOs.response.CategoryResponseDTO;
import az.edu.itbrains.food.DTOs.response.MenuItemResponseDTO;
import az.edu.itbrains.food.DTOs.response.RestaurantInfoResponseDTO;
import az.edu.itbrains.food.DTOs.response.TestimonialResponseDTO;
import az.edu.itbrains.food.models.SpecialOffers;
import az.edu.itbrains.food.models.Testimonial;
import az.edu.itbrains.food.repositories.SpecialOffersRepository;
import az.edu.itbrains.food.services.ICategoryService;
import az.edu.itbrains.food.services.IMenuItemService;
import az.edu.itbrains.food.services.IRestaurantInfoService;
import az.edu.itbrains.food.services.ITestimonialService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final ICategoryService iCategoryService;
    private final IMenuItemService iMenuItemService;
    private final SpecialOffersRepository specialOffersRepository;
    private final ITestimonialService testimonialService;
    private final ModelMapper modelMapper;
    private final IRestaurantInfoService restaurantInfoService;



    @GetMapping("/")
    public String home(@RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false, defaultValue = "false") boolean all,
                       Model model) {

        RestaurantInfoResponseDTO restaurantInfo = restaurantInfoService.getRestaurantInfo(1L);
        model.addAttribute("restaurantInfo", restaurantInfo);


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

        // Bu hissəni əlavə edin: Məlumat bazasından xüsusi təklifləri çəkirik
        List<SpecialOffers> offers = specialOffersRepository.findAll();


        model.addAttribute("activeCategoryId", categoryId);
        model.addAttribute("menuItems", menuItemResponseDTOList);
        model.addAttribute("categoryItems", categoryResponseDTOList);
        model.addAttribute("allView", all);
        model.addAttribute("offers", offers); // Xüsusi təklifləri modelə əlavə edirik






        List<TestimonialResponseDTO> testimonials = testimonialService.getAll();
        model.addAttribute("testimonials", testimonials);
        model.addAttribute("newTestimonial", new Testimonial());
        return "index";
    }





    @GetMapping("/about")
    public String about() {
        return "about";
    }
    @GetMapping("/dashboard/restaurant")
    public String adminRestaurant(Model model) {
        RestaurantInfoResponseDTO info = restaurantInfoService.getRestaurantInfo(1L);
        model.addAttribute("restaurantInfo", info);
        return "dashboard/restaurant";
    }

    @PostMapping("/dashboard/restaurant/update")
    public String updateRestaurant(RestaurantInfoResponseDTO dto) {
        restaurantInfoService.updateRestaurantInfo(1L, dto);
        return "redirect:/dashboard/restaurant";
    }

}