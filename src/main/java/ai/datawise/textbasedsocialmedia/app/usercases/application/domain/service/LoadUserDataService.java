package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.*;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.LoadUserDataUseCase;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.GetInfoQuery;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.LoadUserDataPort;
import ai.datawise.textbasedsocialmedia.app.utils.DbUtils;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class LoadUserDataService implements LoadUserDataUseCase
{
    private final LoadUserDataPort loadUserDataPort;
    public List<FollowerPostView> loadFollowingPosts(GetInfoQuery query) throws Exception
    {
        return DbUtils.inTransaction(entityManager -> loadUserDataPort.getFollowingPosts(query.getId()));
    }
    public UserPostWithLatestCommentsView loadUserPostAndLatestComments(GetInfoQuery query) throws Exception
    {
        return DbUtils.inTransaction(entityManager -> loadUserDataPort.
                getUserPostAndLatestComments(query.getId()));
    }
    public List<PostCommentView> loadAllPostComments(GetInfoQuery query) throws Exception
    {
        return DbUtils.inTransaction(entityManager -> loadUserDataPort.getAllPostComments(query.getId()));
    }
    public List<PostCommentView> loadLatestCommentsOnAllUserOrFollowingPosts(GetInfoQuery query) throws Exception
    {
        return DbUtils.inTransaction(entityManager -> loadUserDataPort.
                getLatestCommentsOnAllUserOrFollowingPosts(query.getId()));
    }
    public List<FollowerView> loadFollowerList(GetInfoQuery query) throws Exception
    {
        return DbUtils.inTransaction(entityManager -> loadUserDataPort.getFollowerList(query.getId()));
    }
    public List<FollowerView> loadFollowingList(GetInfoQuery query) throws Exception
    {
        return DbUtils.inTransaction(entityManager -> loadUserDataPort.getFollowingList(query.getId()));
    }
    public List<UserView> loadUsersByUsernameStr(String usernameStr) throws Exception
    {
        return DbUtils.inTransaction(entityManager -> loadUserDataPort.getUsersByUsernameStr(usernameStr));
    }

}
