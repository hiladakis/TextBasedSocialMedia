package ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.UserPost;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.UserPostResponse;

public interface UserPostPort
{
    public boolean isPremiumUser(int user_id) throws Exception;
    public UserPostResponse storePost(UserPost userPost) throws Exception;
}
