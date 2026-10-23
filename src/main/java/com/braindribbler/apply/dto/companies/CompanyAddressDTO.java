package com.braindribbler.apply.dto.companies;

import com.braindribbler.apply.models.companies.CompanyAddress;

public record CompanyAddressDTO(
        String type,
        String street,
        String streetExt,
        String city,
        String stateAbbr,
        String stateFull,
        String zip) {
    public static CompanyAddressDTO fromEntity(CompanyAddress ca) {
        var addr = ca.getAddress();
        var state = addr.getState(); // This might be null due to your LEFT JOIN

        return new CompanyAddressDTO(
                ca.getAddressType() != null ? ca.getAddressType().getName() : "N/A",
                addr.getStreet(),
                addr.getStreetExt(),
                addr.getCity(),
                state != null ? state.getAbbr() : "",
                state != null ? state.getName() : "",
                addr.getZip());
    }
}
