package com.peters.cafecart.features.DeliveryManagment.entity;

import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "delivery_areas")
public class DeliveryAreas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "governorate")
    String governorate;

    @Column(name = "city")
    String city;

    @Column(name = "area")
    String area;

    @Column(name = "price")
    BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_shop_id", nullable = false)
    private VendorShop vendorShop;
}
