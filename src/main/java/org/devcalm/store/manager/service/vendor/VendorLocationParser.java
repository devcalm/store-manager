package org.devcalm.store.manager.service.vendor;

import org.devcalm.store.manager.domain.model.AddressInfo;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class VendorLocationParser {
    /** Canada, 9249 120 St, Delta, BC V4C 6R8 */
    private final Pattern pattern = Pattern.compile("^(.*?),\\s+(.*?),\\s+(.*?),\\s+(\\w{2})\\s+(\\w{3}\\s\\w{3})$");

    public AddressInfo parse(String location) {
        var matcher = pattern.matcher(location);
        if (matcher.matches()) {
            var addressInfo = new AddressInfo();
            addressInfo.setCountry(matcher.group(1));
            addressInfo.setAddress(matcher.group(2));
            addressInfo.setCity(matcher.group(3));
            addressInfo.setProvince(matcher.group(4));
            addressInfo.setPostalCode(matcher.group(5));
            return addressInfo;
        } else {
            throw new IllegalArgumentException("Invalid address format: " + location);
        }
    }
}
