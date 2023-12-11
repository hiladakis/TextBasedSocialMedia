package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.PostCommentResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.PostCommentCommand;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.PostCommentUseCase;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.PostCommentPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostCommentService implements PostCommentUseCase
{
    private final PostCommentPort postCommentPort;
    private static final int freeUsersTextLimit = 1000;
    private static final int premiumUsersTextLimit = 3000;
    private static final int freeUsersAllowedCommentsPerPost = 5;

    @Override
    public PostCommentResponse postComment(PostCommentCommand postCommentCommand)
    {
        if(isUserAllowedToPostComment(postCommentPort.isPremiumUser(postCommentCommand.getUserId()),
                postCommentPort.getPostCommentsNumber(postCommentCommand.getPostId(), postCommentCommand.getUserId()),
                postCommentCommand.getComment().length()))
        {
            return postCommentPort.storePostComment(postCommentCommand.getPostComment());
        }
        else{
            return null;
        }
    }

    private boolean isUserAllowedToPostComment(boolean isPremiumUser, int postCommentsNumber, int commentSize)
    {
        if(isPremiumUser)
        {
            if(commentSize < premiumUsersTextLimit)
            {
                return true;
            }
        }
        else{
            if(commentSize < freeUsersTextLimit && postCommentsNumber < freeUsersAllowedCommentsPerPost)
            {
                return true;
            }
        }
        return false;
    }

}
