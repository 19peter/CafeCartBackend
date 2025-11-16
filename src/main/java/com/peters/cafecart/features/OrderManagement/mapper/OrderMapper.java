package com.peters.cafecart.features.OrderManagement.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.peters.cafecart.features.OrderManagement.dto.OrderDto;
import com.peters.cafecart.features.OrderManagement.projections.OrderDetailShop;
import com.peters.cafecart.features.OrderManagement.dto.OrderItemDto;
import com.peters.cafecart.features.OrderManagement.projections.ItemDetail;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    List<OrderDto> toDtoList(List<OrderDetailShop> orders);

    List<OrderItemDto> toOrderItemDtoList(List<ItemDetail> orderItems);
}
