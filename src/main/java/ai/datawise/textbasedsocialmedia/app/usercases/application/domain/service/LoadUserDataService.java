package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.*;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.LoadUserDataUseCase;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.GetInfoQuery;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.LoadUserDataPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class LoadUserDataService implements LoadUserDataUseCase
{
    private final LoadUserDataPort loadUserDataPort;
    public List<FollowerPostView> loadFollowingPosts(GetInfoQuery query)
    {
        return loadUserDataPort.getFollowingPosts(query.getId());
    }
    public UserPostWithLatestCommentsView loadUserPostAndLatestComments(GetInfoQuery query)
    {
        return loadUserDataPort.getUserPostAndLatestComments(query.getId());
    }
    public List<PostCommentView> loadAllPostComments(GetInfoQuery query)
    {
        return loadUserDataPort.getAllPostComments(query.getId());
    }
    public List<PostCommentView> loadLatestCommentsOnAllUserOrFollowingPosts(GetInfoQuery query)
    {
        return loadUserDataPort.getLatestCommentsOnAllUserOrFollowingPosts(query.getId());
    }
    public List<FollowerView> loadFollowerList(GetInfoQuery query)
    {
        return loadUserDataPort.getFollowerList(query.getId());
    }
    public List<FollowerView> loadFollowingList(GetInfoQuery query)
    {
        return loadUserDataPort.getFollowingList(query.getId());
    }
    public List<UserView> loadUsersByUsernameStr(String usernameStr)
    {
        return loadUserDataPort.getUsersByUsernameStr(usernameStr);
    }

}
