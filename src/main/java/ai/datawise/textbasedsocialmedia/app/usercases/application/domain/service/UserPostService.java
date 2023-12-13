package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.UserPostResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserPostCommand;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.UserPostUseCase;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.UserPostPort;
import ai.datawise.textbasedsocialmedia.app.utils.DbUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserPostService implements UserPostUseCase
{
    private final UserPostPort userPostPort;
    private static final int freeUsersTextLimit = 1000;
    private static final int premiumUsersTextLimit = 3000;
    @Override
    public UserPostResponse makePost(UserPostCommand userPostCommand) throws Exception
    {
        return DbUtils.inTransaction(entityManager -> {
            boolean isUserAllowedToPost = isUserAllowedToPost(userPostPort.isPremiumUser(userPostCommand.getUserId()),
                    userPostCommand.getText().length());
            if(isUserAllowedToPost)
            {
                return userPostPort.storePost(userPostCommand.getUserPost());
            }
            else{
                return null;
            }
        });
    }

    private boolean isUserAllowedToPost(boolean isPremiumUser, int textSize)
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
