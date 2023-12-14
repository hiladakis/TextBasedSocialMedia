package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.UserPost;
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
    @Override
    public UserPostResponse makePost(UserPostCommand userPostCommand) throws Exception
    {
        return DbUtils.inTransaction(entityManager -> {
            UserPost userPost = userPostCommand.getUserPost();
            boolean isUserAllowedToPost = userPost.isUserAllowedToPost(userPostPort.isPremiumUser(userPostCommand.getUserId()),
                    userPostCommand.getText().length());
            if(isUserAllowedToPost)
            {
                return userPostPort.storePost(userPost);
            }
            else{
                return null;
            }
        });
    }

}
