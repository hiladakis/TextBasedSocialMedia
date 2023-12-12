package ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.*;

import java.util.List;

public interface LoadUserDataPort
{
    public List<FollowerPostView> getFollowingPosts(int userId) throws Exception;
    public UserPostWithLatestCommentsView getUserPostAndLatestComments(int postId) throws Exception;
    public List<PostCommentView> getAllPostComments(int postId) throws Exception;
    public List<PostCommentView> getLatestCommentsOnAllUserOrFollowingPosts(int userId) throws Exception;
    public List<FollowerView> getFollowerList(int userId) throws Exception;
    public List<FollowerView> getFollowingList(int userId) throws Exception;
    public List<UserView> getUsersByUsernameStr(String usernameStr) throws Exception;


}
