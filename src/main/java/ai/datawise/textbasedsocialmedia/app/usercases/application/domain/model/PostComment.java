package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model;

import lombok.Value;

@Value
public class PostComment
{
    private int postId;
    private int userId;
    private String comment;
}
