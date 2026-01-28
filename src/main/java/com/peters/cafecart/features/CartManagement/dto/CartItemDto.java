package com.peters.cafecart.features.CartManagement.dto;

import com.peters.cafecart.features.AdditionsManagement.dto.AdditionDto;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CartItemDto {
    private Long id;
    private Long cartId;
    private Long cartItemId;
    private Long productId;
    private Long productOptionId;
    private String productName;
    private String productImage;
    private int quantity;
    private double unitPrice;
    private boolean isStockTracked;
    private List<Long> additionsIds;
    private List<AdditionDto> additions;
}
