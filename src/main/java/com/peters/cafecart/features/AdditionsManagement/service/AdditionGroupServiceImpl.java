package com.peters.cafecart.features.AdditionsManagement.service;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.UnauthorizedAccessException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.AdditionsManagement.dto.AdditionDto;
import com.peters.cafecart.features.AdditionsManagement.dto.AdditionGroupDto;
import com.peters.cafecart.features.AdditionsManagement.entity.Addition;
import com.peters.cafecart.features.AdditionsManagement.entity.AdditionGroup;
import com.peters.cafecart.features.AdditionsManagement.mapper.AdditionMapper;
import com.peters.cafecart.features.AdditionsManagement.repository.AdditionGroupRepository;
import com.peters.cafecart.features.AdditionsManagement.repository.AdditionRepository;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.repository.ProductRepository;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdditionGroupServiceImpl implements AdditionGroupService {

    private final AdditionGroupRepository groupRepository;
    private final AdditionRepository additionRepository;
    private final AdditionMapper mapper;
    private final EntityManager entityManager;

    @Override
    public AdditionGroupDto createGroup(AdditionGroupDto groupDto, Long vendorId) {
        if (groupRepository.existsByNameAndVendor_Id(groupDto.getName(), vendorId)) {
            throw new ValidationException("Addition group with this name already exists");
        }
        AdditionGroup group = mapper.toEntity(groupDto);
        group.setVendor(entityManager.getReference(Vendor.class, vendorId));
        return mapper.toDto(groupRepository.save(group));
    }

    @Override
    public AdditionGroupDto updateGroup(Long id, AdditionGroupDto groupDto, Long vendorId) {
        AdditionGroup group = groupRepository.findByIdAndVendor_Id(id, vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Addition group not found"));
        
        if (!group.getName().equals(groupDto.getName()) && 
            groupRepository.existsByNameAndVendor_Id(groupDto.getName(), vendorId)) {
            throw new ValidationException("Addition group with this name already exists");
        }

        group.setName(groupDto.getName());
        group.setMaxSelectable(groupDto.getMaxSelectable());
        return mapper.toDto(groupRepository.save(group));
    }

    @Override
    public void deleteGroup(Long id, Long vendorId) {
        AdditionGroup group = groupRepository.findByIdAndVendor_Id(id, vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Addition group not found"));
        groupRepository.delete(group);
    }

    @Override
    public List<AdditionGroupDto> getGroupsByVendor(Long vendorId) {
        return mapper.toGroupDtoList(groupRepository.findByVendor_Id(vendorId));
    }

    @Override
    public AdditionGroupDto getGroupById(Long id, Long vendorId) {
        return groupRepository.findByIdAndVendor_Id(id, vendorId)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Addition group not found"));
    }

    @Override
    public AdditionDto addAddition(Long groupId, AdditionDto additionDto, Long vendorId) {
        AdditionGroup group = groupRepository.findByIdAndVendor_Id(groupId, vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Addition group not found"));
        
        Addition addition = mapper.toEntity(additionDto);
        addition.setAdditionGroup(group);
        return mapper.toDto(additionRepository.save(addition));
    }

    @Override
    public AdditionDto updateAddition(Long id, AdditionDto additionDto, Long vendorId) {
        Addition addition = additionRepository.findByIdAndAdditionGroup_Vendor_Id(id, vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Addition not found"));
        
        addition.setName(additionDto.getName());
        addition.setPrice(additionDto.getPrice());
        return mapper.toDto(additionRepository.save(addition));
    }

    @Override
    public void deleteAddition(Long id, Long vendorId) {
        Addition addition = additionRepository.findByIdAndAdditionGroup_Vendor_Id(id, vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Addition not found"));
        additionRepository.delete(addition);
    }

    @Override
    public List<AdditionGroup> getAdditionGroupsByIds(Long vendorId, List<Long> ids) {
        return groupRepository.findAllByVendor_IdAndIdIn(vendorId, ids);
    }

    @Override
    public List<AdditionGroupDto> getAdditionGroupsDtoList(List<AdditionGroup> additionGroups) {
        List<AdditionGroupDto> additionGroupDtoList = new ArrayList<>();
        additionGroups.forEach(group -> {
            additionGroupDtoList.add(mapAdditionGroupToDto(group));
        });
        return additionGroupDtoList;
    }

    private AdditionGroupDto mapAdditionGroupToDto(AdditionGroup additionGroup) {
        AdditionGroupDto dto = new AdditionGroupDto();
        dto.setId(additionGroup.getId());
        dto.setName(additionGroup.getName());
        dto.setMaxSelectable(additionGroup.getMaxSelectable());
        additionGroup.getAdditions().forEach(addition -> {
            dto.getAdditions().add(mapAdditionsToDto(addition));
        });
        return dto;
    }

    private AdditionDto mapAdditionsToDto(Addition addition) {
        AdditionDto dto = new AdditionDto();
        dto.setId(addition.getId());
        dto.setName(addition.getName());
        dto.setPrice(addition.getPrice());
        return dto;
    }
}
