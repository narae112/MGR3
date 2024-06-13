package com.MGR.oauth2;

import java.util.Map;

public class GoogleMemberInfo implements OAuth2MemberInfo{

    private Map<String, Object> attributes;

    public GoogleMemberInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getNickname() {
        return (String) attributes.get("given_name");
    }


    @Override
    public String getProfileImgUrl(){
        return (String) attributes.get("picture").toString();
    }
}
