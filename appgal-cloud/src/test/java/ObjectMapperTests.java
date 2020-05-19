import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.JavaVersion;
import io.appgal.cloud.model.Profile;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ObjectMapperTests {

    @Test
    public void test() throws Exception
    {
        String json = "{\"profile\":{\"id\":\"0cedc697-129c-42d9-a32f-5bbe60f27698\",\"email\":\"blah@blah.com\",\"mobile\":\"8675309\",\"photo\":\"photu\",\"password\":\"blahblah\"}}";

        ObjectMapper mapper = new ObjectMapper();

        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        Profile profile = mapper.readValue(jsonObject.get("profile").toString(), Profile.class);

        System.out.println(JavaVersion.getMajorJavaVersion());
        System.out.println(profile.toJson().toString());
    }
}
