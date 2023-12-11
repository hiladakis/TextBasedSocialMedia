package ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.PostCommentResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.PostCommentCommand;

public interface PostCommentUseCase
{
    public PostCommentResponse postComment(PostCommentCommand postCommentCommand);
}
