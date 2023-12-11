package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model;

import lombok.Value;

@Value
public class UserPost
{
    private int userId;
    private String text;
}
