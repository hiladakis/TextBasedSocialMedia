package ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.*;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.GetInfoQuery;

import java.util.List;

public interface LoadUserDataUseCase
{
    public List<FollowerPostView> loadFollowingPosts(GetInfoQuery query) throws Exception;
    public UserPostWithLatestCommentsView loadUserPostAndLatestComments(GetInfoQuery query) throws Exception;
    public List<PostCommentView> loadAllPostComments(GetInfoQuery query) throws Exception;
    public List<PostCommentView> loadLatestCommentsOnAllUserOrFollowingPosts(GetInfoQuery query) throws Exception;
    public List<FollowerView> loadFollowerList(GetInfoQuery query) throws Exception;
    public List<FollowerView> loadFollowingList(GetInfoQuery query) throws Exception;
    public List<UserView> loadUsersByUsernameStr(String usernameStr) throws Exception;

}
