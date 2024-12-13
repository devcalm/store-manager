package org.devcalm.store.manager.service.vendor;

import org.devcalm.store.manager.domain.exception.StoreException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VendorLocationParserTest {

    private final VendorLocationParser vendorLocationParser = new VendorLocationParser();

    @Test
    void shouldParseLocation() {
        var addressInfo = vendorLocationParser.parse("Canada, 9249 120 St, Delta, BC V4C 6R8");

        assertThat(addressInfo).isNotNull();
        assertThat(addressInfo.getCountry()).isEqualTo("Canada");
        assertThat(addressInfo.getAddress()).isEqualTo("9249 120 St");
        assertThat(addressInfo.getCity()).isEqualTo("Delta");
        assertThat(addressInfo.getProvince()).isEqualTo("BC");
        assertThat(addressInfo.getPostalCode()).isEqualTo("V4C 6R8");
    }

    @Test
    void shouldThrowExceptionWhenInvalidAddress() {
        var location = "Canada, 9249 120 St";

        assertThatThrownBy(() -> vendorLocationParser.parse(location))
                .isInstanceOf(StoreException.class)
                .hasMessageContaining("Invalid address format: " + location);
    }
}