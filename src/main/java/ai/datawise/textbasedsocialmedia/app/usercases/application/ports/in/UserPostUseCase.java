package ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.UserPostResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserPostCommand;

public interface UserPostUseCase
{
    public UserPostResponse makePost(UserPostCommand userPostCommand) throws Exception;
}
