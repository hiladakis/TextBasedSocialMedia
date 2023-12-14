package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.PostComment;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.PostCommentResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.PostCommentCommand;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.PostCommentUseCase;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.PostCommentPort;
import ai.datawise.textbasedsocialmedia.app.utils.DbUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostCommentService implements PostCommentUseCase
{
    private final PostCommentPort postCommentPort;

    @Override
    public PostCommentResponse postComment(PostCommentCommand postCommentCommand) throws Exception
    {
        PostCommentResponse postCommentResponse = DbUtils.inTransaction(entityManager ->
        {
            PostComment postComment = postCommentCommand.getPostComment();
            if(postComment.isUserAllowedToPostComment(postCommentPort.isPremiumUser(postCommentCommand.getUserId()),
                    postCommentPort.getPostCommentsNumber(postCommentCommand.getPostId(), postCommentCommand.getUserId()),
                    postCommentCommand.getComment().length()))
            {
                return postCommentPort.storePostComment(postComment);
            }
            else{
                return null;
            }
        });
        return postCommentResponse;
    }

}
