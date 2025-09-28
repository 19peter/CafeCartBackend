package com.peters.cafecart.features.CustomerManagement.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.peters.cafecart.features.CartManagement.dto.AddToCartDto;
import com.peters.cafecart.features.CartManagement.dto.RemoveFromCart;
import com.peters.cafecart.features.CartManagement.entity.Cart;
import com.peters.cafecart.features.CartManagement.entity.CartItem;
import com.peters.cafecart.features.CartManagement.repository.CartItemRepository;
import com.peters.cafecart.features.CartManagement.repository.CartRepository;
import com.peters.cafecart.features.CustomerManagement.repository.CustomerRepository;
import com.peters.cafecart.features.InventoryManagement.projections.ShopProductSummary;
import com.peters.cafecart.features.InventoryManagement.repository.InventoryRepository;
import com.peters.cafecart.features.VendorManagement.Repository.VendorShopsRepository;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.ProductsManagement.repository.ProductRepository;
import com.peters.cafecart.features.ProductsManagement.entity.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

///Cart may have a shopId or null
///If shopId is null, it means the cart is empty
///If shopId is not null, it means the cart has items from that shop
///If cart becomes empty after removing an item, shopId should be set to null
/// Validations:
/// 1- Ensure that the product exists for the Inventory of shopId sent from frontend
/// 2- Ensure that the shopId of the product added to cart is equal to shopId of the cart
/// 3- Ensure that the shopId of the product added to cart is equal to shopId of the other products

@Service
public class CustomerServiceImpl implements CustomerService {

   
}
