package ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.*;

import java.util.List;

public interface LoadUserDataPort
{
    public List<FollowerPostView> getFollowingPosts(int userId);
    public UserPostWithLatestCommentsView getUserPostAndLatestComments(int postId);
    public List<PostCommentView> getAllPostComments(int postId);
    public List<PostCommentView> getLatestCommentsOnAllUserOrFollowingPosts(int userId);
    public List<FollowerView> getFollowerList(int userId);
    public List<FollowerView> getFollowingList(int userId);
    public List<UserView> getUsersByUsernameStr(String usernameStr);


}
