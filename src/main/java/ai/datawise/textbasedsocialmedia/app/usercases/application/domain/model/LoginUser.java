package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model;

import lombok.Value;

@Value
public class LoginUser
{
    private String username;
    private String password;

    private static final int activeUsersMaxNum = 500;
    public boolean isActiveUsersNumExceeded(long activeUsersNum)
    {
        if(activeUsersNum < activeUsersMaxNum)
        {
            return false;
        }
        return true;
    }
}
