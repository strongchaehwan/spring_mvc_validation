package hello.itemservice.domain.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private Long id;

    private String itemName;

    private Integer price;

    private Integer quantity;


}
