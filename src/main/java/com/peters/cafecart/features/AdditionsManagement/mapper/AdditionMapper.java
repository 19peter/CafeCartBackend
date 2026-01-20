package com.peters.cafecart.features.AdditionsManagement.mapper;

import com.peters.cafecart.features.AdditionsManagement.dto.AdditionDto;
import com.peters.cafecart.features.AdditionsManagement.dto.AdditionGroupDto;
import com.peters.cafecart.features.AdditionsManagement.dto.ShopAdditionDto;
import com.peters.cafecart.features.AdditionsManagement.entity.Addition;
import com.peters.cafecart.features.AdditionsManagement.entity.AdditionGroup;
import com.peters.cafecart.features.AdditionsManagement.entity.ShopAddition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdditionMapper {
    AdditionGroupDto toDto(AdditionGroup additionGroup);
    AdditionDto toDto(Addition addition);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vendor", ignore = true)
    @Mapping(target = "additions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AdditionGroup toEntity(AdditionGroupDto additionGroupDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "additionGroup", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Addition toEntity(AdditionDto additionDto);

    @Mapping(source = "shop.id", target = "shopId")
    ShopAdditionDto toDto(ShopAddition shopAddition);

    List<AdditionGroupDto> toGroupDtoList(List<AdditionGroup> additionGroups);
    List<ShopAdditionDto> toShopAdditionDtoList(List<ShopAddition> shopAdditions);
}
