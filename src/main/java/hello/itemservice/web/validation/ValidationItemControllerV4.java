package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.web.validation.dto.ItemSaveDto;
import hello.itemservice.web.validation.dto.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v4/items")
@RequiredArgsConstructor
public class ValidationItemControllerV4 {

    private final ItemRepository itemRepository;


    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v4/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item()); // 검증에 실패 했을떄도 그대로 데이터가 남아 있게 하려고
        return "validation/v4/addForm";
    }


    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute("item") ItemSaveDto itemSaveDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        log.info("컨트롤러 호출");
        // 글로벌 오류
        if (itemSaveDto.getPrice() != null && itemSaveDto.getQuantity() != null) {
            int resultPrice = itemSaveDto.getPrice() * itemSaveDto.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        /**
         * bindingResult는 자동으로 뷰에 넘어감
         */
        if (bindingResult.hasErrors()) {
            log.error("errors={}", bindingResult);
            return "validation/v4/addForm";
        }

        log.info("성공 로직 실행");

        Item item = Item.builder().itemName(itemSaveDto.getItemName())
                .price(itemSaveDto.getPrice())
                .quantity(itemSaveDto.getQuantity()).build();


        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v4/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute("item") ItemUpdateDto updateDto, BindingResult bindingResult) {


        // 글로벌 오류
        if (updateDto.getPrice() != null && updateDto.getQuantity() != null) {
            int resultPrice = updateDto.getPrice() * updateDto.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        /**
         * bindingResult는 자동으로 뷰에 넘어감
         */
        if (bindingResult.hasErrors()) {
            log.error("errors={}", bindingResult);
            return "validation/v4/editForm";
        }


        Item item = Item.builder().id(updateDto.getId())
                .itemName(updateDto.getItemName())
                .price(updateDto.getPrice())
                .quantity(updateDto.getQuantity()).build();


        itemRepository.update(itemId, item);
        return "redirect:/validation/v4/items/{itemId}";
    }

}

