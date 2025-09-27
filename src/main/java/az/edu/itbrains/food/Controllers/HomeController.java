package az.edu.itbrains.food.Controllers;

import az.edu.itbrains.food.DTOs.response.CategoryResponseDTO;
import az.edu.itbrains.food.DTOs.response.MenuItemResponseDTO;
import az.edu.itbrains.food.models.SpecialOffers;
import az.edu.itbrains.food.repositories.SpecialOffersRepository;
import az.edu.itbrains.food.services.ICategoryService;
import az.edu.itbrains.food.services.IMenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final ICategoryService iCategoryService;
    private final IMenuItemService iMenuItemService;
    private final SpecialOffersRepository specialOffersRepository;


    @GetMapping("/")
    public String home(@RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false, defaultValue = "false") boolean all,
                       Model model) {

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

        return "index";
    }






    @GetMapping("/about")
    public String about() {
        return "about";
    }


}

