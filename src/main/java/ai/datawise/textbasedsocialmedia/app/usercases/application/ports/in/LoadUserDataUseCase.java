package ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.*;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.GetInfoQuery;

import java.util.List;

public interface LoadUserDataUseCase
{
    public List<FollowerPostView> loadFollowingPosts(GetInfoQuery query);
    public UserPostWithLatestCommentsView loadUserPostAndLatestComments(GetInfoQuery query);
    public List<PostCommentView> loadAllPostComments(GetInfoQuery query);
    public List<PostCommentView> loadLatestCommentsOnAllUserOrFollowingPosts(GetInfoQuery query);
    public List<FollowerView> loadFollowerList(GetInfoQuery query);
    public List<FollowerView> loadFollowingList(GetInfoQuery query);
    public List<UserView> loadUsersByUsernameStr(String usernameStr);

}
