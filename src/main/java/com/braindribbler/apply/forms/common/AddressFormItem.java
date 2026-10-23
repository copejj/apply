package com.braindribbler.apply.forms.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AddressFormItem {
    private Long addressId; // Hidden, no validation needed

    @NotBlank(message = "Address type is required")
    private String type;

    @NotBlank(message = "Street address is required")
    @Size(max = 255)
    private String street;

    private String streetExt; // Optional, no annotation

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 2, message = "Use 2-letter abbreviation")
    private String stateAbbr;

    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Invalid Zip Code")
    private String zip;

    // Standard Getters/Setters
    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetExt() {
        return streetExt;
    }

    public void setStreetExt(String streetExt) {
        this.streetExt = streetExt;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateAbbr() {
        return stateAbbr;
    }

    public void setStateAbbr(String stateAbbr) {
        this.stateAbbr = stateAbbr;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
