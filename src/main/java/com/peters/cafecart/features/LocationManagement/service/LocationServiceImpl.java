package com.peters.cafecart.features.LocationManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.DeliveryManagment.dto.CustomerLocationRequestDto;
import com.peters.cafecart.features.LocationManagement.dto.GoogleDistanceResponseDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopLocationDto;
import com.peters.cafecart.features.VendorManagement.service.VendorShops.VendorShopsServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class LocationServiceImpl implements LocationService {
    @Value("${google.api.key}")
    String apiKey;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    VendorShopsServiceImpl vendorShopsService;

    @Override
    public GoogleDistanceResponseDto getDrivingDistance(CustomerLocationRequestDto customerLocationRequestDto) {
        VendorShopLocationDto vendorShopLocationDto = vendorShopsService
                .getVendorShopLocation(customerLocationRequestDto.getShopId());

        if (!isWithinCity(customerLocationRequestDto, vendorShopLocationDto)) throw new ValidationException("Service is not available in your city");

        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="
                + customerLocationRequestDto.getLatitude() + "," + customerLocationRequestDto.getLongitude()
                + "&destinations=" + vendorShopLocationDto.getLatitude() + "," + vendorShopLocationDto.getLongitude()
                + "&key=" + apiKey;

        return restTemplate.getForObject(url, GoogleDistanceResponseDto.class);
    }

    @Override
    public boolean isWithinCity(CustomerLocationRequestDto customerLocationRequestDto, VendorShopLocationDto vendorShopLocationDto) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
                + customerLocationRequestDto.getLatitude() + "," + customerLocationRequestDto.getLongitude()
                + "&location_type=ROOFTOP&result_type=street_address&key=" + apiKey;

        JsonNode root = restTemplate.getForObject(url, JsonNode.class);
        if (root == null || !"OK".equals(root.path("status").asText())) {
            return false;
        }

        JsonNode results = root.path("results");
        if (!results.isArray() || results.size() == 0) {
            return false;
        }

        String[] names = pickLocalityOrAdminLevel2(results.get(0));
        if (names == null) {
            return false;
        }

        String longName = names[0];
        String shortName = names[1];
        String shopCity = vendorShopLocationDto.getCity();
        if (shopCity == null || shopCity.isBlank()) {
            return false;
        }

        String normShop = normalizeCity(shopCity);
        String normLong = normalizeCity(longName);
        String normShort = normalizeCity(shortName);
        return normShop.equals(normLong) || normShop.equals(normShort);
    }

    private String[] pickLocalityOrAdminLevel2(JsonNode resultNode) {
        JsonNode components = resultNode.path("address_components");
        if (!components.isArray()) return null;

        String[] locality = findByType(components, "locality");
        if (locality != null) return locality;

        return findByType(components, "administrative_area_level_2");
    }

    private String[] findByType(JsonNode components, String type) {
        for (JsonNode comp : components) {
            JsonNode types = comp.path("types");
            if (types.isArray()) {
                for (JsonNode t : types) {
                    if (type.equals(t.asText())) {
                        String longName = comp.path("long_name").asText(null);
                        String shortName = comp.path("short_name").asText(null);
                        return new String[] { longName, shortName };
                    }
                }
            }
        }
        return null;
    }

    private String normalizeCity(String s) {
        if (s == null) return null;
        String r = s.trim().toLowerCase();
        // Drop trailing numeric designators like " 1", "-2", "_3"
        r = r.replaceAll("\\s*[-_]*\\d+$", "");
        // Collapse multiple spaces
        r = r.replaceAll("\\s+", " ");
        return r;
    }
}
