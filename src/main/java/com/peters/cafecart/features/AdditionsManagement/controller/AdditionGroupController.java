package com.peters.cafecart.features.AdditionsManagement.controller;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.config.CustomUserPrincipal;
import com.peters.cafecart.features.AdditionsManagement.dto.AdditionDto;
import com.peters.cafecart.features.AdditionsManagement.dto.AdditionGroupDto;
import com.peters.cafecart.features.AdditionsManagement.service.AdditionGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.API_V1 + "/addition-groups")
@RequiredArgsConstructor
public class AdditionGroupController {

    private final AdditionGroupService groupService;
    private final com.peters.cafecart.workflows.CreateAdditionUseCase createAdditionUseCase;
    private final com.peters.cafecart.workflows.DeleteAdditionUseCase deleteAdditionUseCase;

    @PostMapping("/vendor")
    public ResponseEntity<AdditionGroupDto> createGroup(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @Valid @RequestBody AdditionGroupDto groupDto) {
        return ResponseEntity.ok(groupService.createGroup(groupDto, user.getId()));
    }

    @GetMapping("/vendor")
    public ResponseEntity<List<AdditionGroupDto>> getGroups(
            @AuthenticationPrincipal CustomUserPrincipal user) {
        return ResponseEntity.ok(groupService.getGroupsByVendor(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdditionGroupDto> getGroup(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @PathVariable Long id) {
        return ResponseEntity.ok(groupService.getGroupById(id, user.getId()));
    }

    @PutMapping("/vendor/{id}")
    public ResponseEntity<AdditionGroupDto> updateGroup(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @PathVariable Long id,
            @Valid @RequestBody AdditionGroupDto groupDto) {
        return ResponseEntity.ok(groupService.updateGroup(id, groupDto, user.getId()));
    }

    @DeleteMapping("/vendor/{id}")
    public ResponseEntity<Void> deleteGroup(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @PathVariable Long id) {
        groupService.deleteGroup(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/vendor/{groupId}/additions")
    public ResponseEntity<AdditionDto> addAddition(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @PathVariable Long groupId,
            @Valid @RequestBody AdditionDto additionDto) {
        return ResponseEntity.ok(createAdditionUseCase.execute(groupId, additionDto, user.getId()));
    }

    @PutMapping("/vendor/additions/{id}")
    public ResponseEntity<AdditionDto> updateAddition(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @PathVariable Long id,
            @Valid @RequestBody AdditionDto additionDto) {
        return ResponseEntity.ok(groupService.updateAddition(id, additionDto, user.getId()));
    }

    @GetMapping("/vendor/{groupId}/additions")
    public ResponseEntity<List<AdditionDto>> getAdditions(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroupById(groupId, user.getId()).getAdditions());
    }

    @DeleteMapping("/vendor/additions/{id}")
    public ResponseEntity<Void> deleteAddition(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @PathVariable Long id) {
        deleteAdditionUseCase.execute(id, user.getId());
        return ResponseEntity.noContent().build();
    }

}
