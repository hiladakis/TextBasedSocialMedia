package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model;

import lombok.Value;

@Value
public class PostComment
{
    private int postId;
    private int userId;
    private String comment;

    private static final int freeUsersTextLimit = 1000;
    private static final int premiumUsersTextLimit = 3000;
    private static final int freeUsersAllowedCommentsPerPost = 5;

    public boolean isUserAllowedToPostComment(boolean isPremiumUser, int postCommentsNumber, int commentSize)
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
