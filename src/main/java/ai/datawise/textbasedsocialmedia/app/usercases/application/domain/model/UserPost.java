package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model;

import lombok.Value;

@Value
public class UserPost
{
    private int userId;
    private String text;

    private static final int freeUsersTextLimit = 1000;
    private static final int premiumUsersTextLimit = 3000;
    public boolean isUserAllowedToPost(boolean isPremiumUser, int textSize)
    {
        if(isPremiumUser)
        {
            if(textSize <= premiumUsersTextLimit)
            {
                return true;
            }
        }
        else{
            if(textSize <= freeUsersTextLimit)
            {
                return true;
            }
        }

        return false;
    }

}
