package io.appgal.cloud.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.model.validators.ValidProfileSourceOrg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;

@ValidProfileSourceOrg
public class Profile implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(Profile.class);

    private String id;

    @Email(regexp = ".+@.+\\..+",message="email_invalid")
    @NotBlank(message="email_required")
    private String email;

    @NotBlank(message="password_required")
    private String password;

    @Digits(integer = 10, fraction = 0, message = "phone_invalid")
    private long mobile;

    private String photo;

    //NotBlank if An 'ORG' Profile
    private String sourceOrgId;

    private ProfileType profileType;
    private Location location;

    private String chainId;

    public Profile()
    {

    }

    public Profile(String id, String email, long mobile, String photo, String password, ProfileType profileType)
    {
        this.id = id;
        this.email = email;
        this.mobile = mobile;
        this.photo = photo;
        this.password = password;
        this.profileType = profileType;
    }

    public Profile(String id, String email, long mobile, String photo, String password, ProfileType profileType, String sourceOrgId)
    {
        this(id,email,mobile,photo,password,profileType);
        this.sourceOrgId = sourceOrgId;
    }

    public Profile(String id, String email, long mobile, String photo, String password, ProfileType profileType, String sourceOrgId,
                   Location location)
    {
        this(id,email,mobile,photo,password,profileType, sourceOrgId);
        this.location = location;
    }

    public Profile(String id, String email, long mobile, String photo, String password, ProfileType profileType,
                   Location location)
    {
        this(id,email,mobile,photo,password,profileType);
        this.location = location;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public long getMobile()
    {
        return mobile;
    }

    public void setMobile(long mobile)
    {
        this.mobile = mobile;
    }

    public String getPhoto()
    {
        return photo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoto(String photo)
    {
        this.photo = photo;
    }

    public String getSourceOrgId() {
        return sourceOrgId;
    }

    public void setSourceOrgId(String sourceOrgId) {
        this.sourceOrgId = sourceOrgId;
    }

    public ProfileType getProfileType() {
        return profileType;
    }

    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    @Override
    public String toString()
    {
        return this.toJson().toString();
    }

    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();

        if(this.id != null) {
            jsonObject.addProperty("id", this.id);
        }
        if(this.email != null) {
            jsonObject.addProperty("email", this.email);
        }

        jsonObject.addProperty("mobile", this.mobile);

        if(this.photo != null) {
            jsonObject.addProperty("photo", this.photo);
        }
        if(this.password != null)
        {
            jsonObject.addProperty("password", this.password);
        }
        if(this.sourceOrgId != null)
        {
            jsonObject.addProperty("sourceOrgId", this.sourceOrgId);
        }
        jsonObject.addProperty("profileType", this.profileType.name());

        if(this.location != null) {
            jsonObject.add("location", this.location.toJson());
        }

        if(this.chainId != null) {
            jsonObject.addProperty("chainId", this.chainId);
        }

        return jsonObject;
    }

    public static Profile parse(String json)
    {
        Profile profile = new Profile();

        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        if(jsonObject.has("id")) {
            profile.id = jsonObject.get("id").getAsString();
        }
        if(jsonObject.has("email")) {
            profile.email = jsonObject.get("email").getAsString();
        }
        if(jsonObject.has("mobile")) {
            String input = jsonObject.get("mobile").getAsString();
            try {
                NumberFormat.getInstance().parse(input);
                profile.mobile = jsonObject.get("mobile").getAsLong();
            } catch (ParseException e) {
                profile.mobile = 11111111111l;
            }
        }
        if(jsonObject.has("photo")) {
            profile.photo = jsonObject.get("photo").getAsString();
        }
        if(jsonObject.has("password")) {
            profile.password = jsonObject.get("password").getAsString();
        }
        if(jsonObject.has("sourceOrgId")) {
            profile.sourceOrgId = jsonObject.get("sourceOrgId").getAsString();
        }
        if(jsonObject.has("location")) {
            profile.location = Location.parse(jsonObject.get("location").getAsJsonObject().toString());
        }
        String profileTypeName = jsonObject.get("profileType").getAsString();
        profile.profileType = ProfileType.valueOf(profileTypeName);

        if(jsonObject.has("chainId")) {
            profile.chainId = jsonObject.get("chainId").getAsString();
        }

        return profile;
    }
}
