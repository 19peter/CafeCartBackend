package com.peters.cafecart.features.AdditionsManagement.service;

import com.peters.cafecart.features.AdditionsManagement.dto.AdditionDto;
import com.peters.cafecart.features.AdditionsManagement.dto.AdditionGroupDto;
import com.peters.cafecart.features.AdditionsManagement.entity.AdditionGroup;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import java.util.List;

public interface AdditionGroupService {
    AdditionGroupDto createGroup(AdditionGroupDto groupDto, Long vendorId);
    AdditionGroupDto updateGroup(Long id, AdditionGroupDto groupDto, Long vendorId);
    void deleteGroup(Long id, Long vendorId);
    List<AdditionGroupDto> getGroupsByVendor(Long vendorId);
    AdditionGroupDto getGroupById(Long id, Long vendorId);

    AdditionDto addAddition(Long groupId, AdditionDto additionDto, Long vendorId);
    AdditionDto updateAddition(Long id, AdditionDto additionDto, Long vendorId);
    void deleteAddition(Long id, Long vendorId);

    List<AdditionGroup> getAdditionGroupsByIds(Long vendorId, List<Long> ids);
    List<AdditionGroupDto>getAdditionGroupsDtoList(List<AdditionGroup> additionGroups);
    void validateAdditions(Product product, List<Long> additionIds);
}
