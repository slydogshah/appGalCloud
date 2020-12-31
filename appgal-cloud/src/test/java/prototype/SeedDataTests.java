package prototype;

import com.google.gson.*;

import io.appgal.cloud.model.Location;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.model.ProfileType;
import io.appgal.cloud.model.SourceOrg;
import io.appgal.cloud.app.services.CustomerService;
import io.appgal.cloud.app.services.ProfileRegistrationService;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;

@QuarkusTest
public class SeedDataTests {
    private static Logger logger = LoggerFactory.getLogger(SeedDataTests.class);

    @Inject
    private CustomerService customerService;

    @Inject
    private ProfileRegistrationService profileRegistrationService;

    @Test
    public void testSeedData() throws Exception
    {
        SourceOrg pickUp1 = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",true);
        pickUp1.setProducer(true);
        pickUp1.setLocation(new Location(30.25860595703125d, -97.74873352050781d));
        SourceOrg pickUp2 = new SourceOrg("apple", "Apple", "tim_cook@apple.com",true);
        pickUp2.setProducer(true);

        SourceOrg dropOff1 = new SourceOrg("church1", "DOWNTOWN_CHURCH", "downtown.church@gmail.com",false);
        SourceOrg dropOff2 = new SourceOrg("church2", "SUBURB_CHURCH", "suburb.church@gmail.com",false);

        customerService.storeSourceOrg(pickUp1);
        customerService.storeSourceOrg(pickUp2);
        customerService.storeSourceOrg(dropOff1);
        customerService.storeSourceOrg(dropOff2);

        String uuid = UUID.randomUUID().toString();
        Profile profile = new Profile(UUID.randomUUID().toString(), "c."+uuid+"@s.com", 8675309l, "", "c",
                ProfileType.FOOD_RUNNER);
        this.profileRegistrationService.register(profile);

        Profile profile2 = new Profile(UUID.randomUUID().toString(), "m."+uuid+"@s.com", 7675309l, "", "s",
                ProfileType.ORG, "microsoft");
        this.profileRegistrationService.register(profile2);

        JsonObject auth = this.profileRegistrationService.login("c."+uuid+"@s.com","c");
        logger.info("****************");
        logger.info(auth.toString());
        logger.info("****************");
    }
}
