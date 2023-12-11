package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.UserPostResponse;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class FollowerPostView extends UserPostResponse
{
    private final String followerName;
    private final String text;
    public FollowerPostView(int postId, Timestamp postDate, String text, String followerName)
    {
        super(postId, postDate);
        this.text = text;
        this.followerName = followerName;
    }
}
