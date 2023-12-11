package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.UserPostResponse;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
public class UserPostWithLatestCommentsView extends UserPostResponse
{
    private final String postUser;
    private final String text;

    @Setter
    private List<PostCommentView> latestComments;
    public UserPostWithLatestCommentsView(int postId, Timestamp postDate, String postUser,
                                          String text)
    {
        super(postId, postDate);
        this.postUser = postUser;
        this.text = text;
    }
}
