package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model;

import lombok.Value;

@Value
public class User
{
    private String username;
    private String password;
    private String role;
}
