package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.PostCommentResponse;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class PostCommentView extends PostCommentResponse
{
    private final String commentUser;
    private final String comment;

    public PostCommentView(int postCommentId, Timestamp postCommentDate, String commentUser,
                           String comment)
    {
        super(postCommentId, postCommentDate);
        this.commentUser = commentUser;
        this.comment = comment;
    }
}
