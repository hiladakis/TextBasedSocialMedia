package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model;

import lombok.Value;

@Value
public class LoginUser
{
    private String username;
    private String password;
}
