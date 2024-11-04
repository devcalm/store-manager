package org.devcalm.store.manager.domain.model;

import lombok.Data;

@Data
public class AddressInfo {
    private String country;
    private String province;
    private String city;
    private String address;
    private String postalCode;
    private double latitude;
    private double longitude;
}
