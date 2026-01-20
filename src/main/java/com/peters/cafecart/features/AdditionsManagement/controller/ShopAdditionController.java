package com.peters.cafecart.features.AdditionsManagement.controller;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.config.CustomUserPrincipal;
import com.peters.cafecart.features.AdditionsManagement.dto.ShopAdditionDto;
import com.peters.cafecart.features.AdditionsManagement.service.ShopAdditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.API_V1 + "/shop-additions")
@RequiredArgsConstructor
public class ShopAdditionController {

    private final ShopAdditionService shopAdditionService;

    @GetMapping("/shop")
    public ResponseEntity<List<ShopAdditionDto>> getShopAdditions(
            @AuthenticationPrincipal CustomUserPrincipal user) {
        return ResponseEntity.ok(shopAdditionService.getAdditionsByShop(user.getId()));
    }

    @PutMapping("/shop/{id}/availability")
    public ResponseEntity<Void> updateAvailability(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @PathVariable Long id,
            @RequestParam Boolean isAvailable) {
        shopAdditionService.updateAvailability(id, isAvailable, user.getId());
        return ResponseEntity.ok().build();
    }
}
