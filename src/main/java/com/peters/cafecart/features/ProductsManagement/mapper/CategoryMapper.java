package com.peters.cafecart.features.ProductsManagement.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.peters.cafecart.features.ProductsManagement.dto.response.CategoryDto;
import com.peters.cafecart.features.ProductsManagement.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);
    List<CategoryDto> toDtoList(List<Category> categories);
}
