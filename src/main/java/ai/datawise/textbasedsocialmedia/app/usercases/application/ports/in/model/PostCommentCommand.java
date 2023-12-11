package ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.PostComment;
import ai.datawise.textbasedsocialmedia.app.utils.Validator;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PostCommentCommand implements Serializable
{
    @Positive(message = "postId must be a positive integer")
    int postId;

    @Positive(message = "userId must be a positive integer")
    int userId;

    @Size(min = 1, max = 3000, message = "text cannot exceed 3000 characters")
    String comment;

    public PostCommentCommand(int postId, int userId, String comment)
    {
        this.postId = postId;
        this.userId = userId;
        this.comment = comment;

        Validator.validate(this);
    }
    public PostComment getPostComment()
    {
        return new PostComment(this.getPostId(), this.getUserId(), this.getComment());
    }

    @Override
    public String toString()
    {
        return "{'postId':"+ postId +",'userId':"+ userId +",'comment':'"+comment+"'}";
    }

}
