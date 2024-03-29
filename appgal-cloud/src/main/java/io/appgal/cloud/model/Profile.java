package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.model.validators.ValidProfileSourceOrg;
import io.appgal.cloud.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Objects;

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

    @NotNull
    private ProfileType profileType;

    private Location location;

    private String chainId;

    private String resetCode;

    private boolean offlineCommunitySupport = false;

    private boolean resetPasswordActive=false;

    private String bearerToken;

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

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public boolean isOfflineCommunitySupport() {
        return offlineCommunitySupport;
    }

    public void setOfflineCommunitySupport(boolean offlineCommunitySupport) {
        logger.info("*********SET*****************");


        this.offlineCommunitySupport = offlineCommunitySupport;

        JsonUtil.print(this.getClass(),this.toJson());
    }

    public boolean isResetPasswordActive() {
        return resetPasswordActive;
    }

    public void setResetPasswordActive(boolean resetPasswordActive) {
        this.resetPasswordActive = resetPasswordActive;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
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

        if(this.chainId != null) {
            jsonObject.addProperty("chainId", this.chainId);
        }
        jsonObject.addProperty("offlineCommunitySupport",this.offlineCommunitySupport);

        jsonObject.addProperty("resetPasswordActive",this.resetPasswordActive);

        if(this.bearerToken != null){
            jsonObject.addProperty("bearerToken",this.bearerToken);
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
        if(jsonObject.has("photo") && !jsonObject.get("photo").isJsonNull()) {
            profile.photo = jsonObject.get("photo").getAsString();
        }
        if(jsonObject.has("password")) {
            profile.password = jsonObject.get("password").getAsString();
        }
        if(jsonObject.has("sourceOrgId")) {
            profile.sourceOrgId = jsonObject.get("sourceOrgId").getAsString();
        }
        String profileTypeName = jsonObject.get("profileType").getAsString();
        profile.profileType = ProfileType.valueOf(profileTypeName);

        if(jsonObject.has("chainId")) {
            profile.chainId = jsonObject.get("chainId").getAsString();
        }

        if(jsonObject.has("resetCode"))
        {
            profile.resetCode = jsonObject.get("resetCode").getAsString();
        }

        if(jsonObject.has("offlineCommunitySupport"))
        {
            profile.offlineCommunitySupport = jsonObject.get("offlineCommunitySupport").getAsBoolean();
        }

        if(jsonObject.has("resetPasswordActive"))
        {
            profile.resetPasswordActive = jsonObject.get("resetPasswordActive").getAsBoolean();
        }

        if(jsonObject.has("bearerToken")){
            profile.bearerToken = jsonObject.get("bearerToken").getAsString();
        }

        return profile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return Objects.equals(email, profile.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
