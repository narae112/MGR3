package com.MGR.oauth2;

import java.util.Map;

public class GithubMemberInfo implements OAuth2MemberInfo{

    private Map<String, Object> attributes;

    public GithubMemberInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "github";
    }

    @Override
    public String getName() {
        return (String) attributes.get("login");
    }

    @Override
    public String getNickname() {
        return (String) attributes.get("login");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getProfileImgUrl(){
        return (String) attributes.get("avatar_url").toString();
    }

}
