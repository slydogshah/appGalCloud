package io.appgal.cloud.util;

import io.appgal.cloud.model.Address;
import io.appgal.cloud.model.Location;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class MapUtilsTests {

    @Inject
    private MapUtils mapUtils;

    @Test
    public void calculateCoordinates() throws Exception
    {
        Address address = new Address();
        address.setStreet("801 West Fifth Street");
        address.setZip("78703");
        Location location = this.mapUtils.calculateCoordinates(address);
        JsonUtil.print(this.getClass(),location.toJson());
    }
}
