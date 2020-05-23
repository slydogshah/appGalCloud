package prototype;

import io.appgal.cloud.model.Location;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.model.ProfileType;
import io.appgal.cloud.model.SourceOrg;
import io.appgal.cloud.services.CustomerService;
import io.appgal.cloud.services.ProfileRegistrationService;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;

@QuarkusTest
public class SeedData {
    private static Logger logger = LoggerFactory.getLogger(SeedData.class);

    @Inject
    private CustomerService customerService;

    @Inject
    private ProfileRegistrationService profileRegistrationService;

    @Test
    public void testSeedData() throws Exception
    {
        SourceOrg pickUp1 = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
        pickUp1.setLocation(new Location(30.25860595703125d, -97.74873352050781d));
        SourceOrg pickUp2 = new SourceOrg("apple", "Apple", "tim_cook@apple.com");

        SourceOrg dropOff1 = new SourceOrg("church1", "DOWNTOWN_CHURCH", "downtown.church@gmail.com");
        SourceOrg dropOff2 = new SourceOrg("church2", "SUBURB_CHURCH", "suburb.church@gmail.com");

        customerService.storeSourceOrg(pickUp1);
        customerService.storeSourceOrg(pickUp2);
        customerService.storeSourceOrg(dropOff1);
        customerService.storeSourceOrg(dropOff2);

        Profile profile = new Profile(UUID.randomUUID().toString(), "c@s.com", "8675309", "", "c",
                ProfileType.FOOD_RUNNER);
        this.profileRegistrationService.register(profile);
    }
}
