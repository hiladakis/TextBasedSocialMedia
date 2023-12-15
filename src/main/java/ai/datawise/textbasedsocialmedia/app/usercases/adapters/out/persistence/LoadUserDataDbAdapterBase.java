package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.FollowersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.PostCommentsEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.UserPostsEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.FollowerPostView;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.FollowerView;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.PostCommentView;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.UserPostWithLatestCommentsView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

class LoadUserDataDbAdapterBase
{
    protected void addFollowerPostsToPostViewList(List<FollowerPostView> followerPostViewList,
                                                List<Object[]> followerPosts)
    {
        Iterator<Object[]> userPostsIterator = followerPosts.iterator();
        while(userPostsIterator.hasNext())
        {
            Object[] userPost = userPostsIterator.next();
            int postId = (int)userPost[0];
            Timestamp postDate = (Timestamp)userPost[1];
            String text = (String)userPost[2];
            String followerName = (String)userPost[3];
            FollowerPostView followerPostView = new FollowerPostView(postId,postDate,
                    text,followerName);
            followerPostViewList.add(followerPostView);
        }
    }

    protected List<PostCommentView> getPostCommentViewListFromQuery( List<Object[]> postCommentList )
    {
        List<PostCommentView> postCommentViewList = new ArrayList<>();
        Iterator<Object[]> postCommentListIterator = postCommentList.iterator();
        while(postCommentListIterator.hasNext())
        {
            Object[] postComment = postCommentListIterator.next();
            int postCommentId = (int)postComment[0];
            Timestamp postCommentDate = (Timestamp) postComment[1];
            String postCommentUser = (String) postComment[2];
            String comment = (String) postComment[3];
            postCommentViewList.add(new PostCommentView(postCommentId,
                    postCommentDate,postCommentUser,comment));
        }
        return postCommentViewList;
    }

    protected UserPostWithLatestCommentsView
        getUserPostWithLatestCommentsViewFromQuery(List<Object[]> resultPost, List<PostCommentView> postCommentViewList)
    {
        Object[] userPost = resultPost.get(0);
        int postId = (int)userPost[0];
        Timestamp postDate = (Timestamp) userPost[1];
        String postUser = (String) userPost[2];
        String text = (String) userPost[3];
        UserPostWithLatestCommentsView userPostWithLatestCommentsView =
                new UserPostWithLatestCommentsView(postId,postDate,postUser,text);
        userPostWithLatestCommentsView.setLatestComments(postCommentViewList);
        return userPostWithLatestCommentsView;
    }

    protected List<FollowerView> getFollowerViewListFromFollowersEntities(List<FollowersEntity> followersEntityList)
    {
        Iterator<FollowersEntity> followersEntityIterator = followersEntityList.iterator();
        List<FollowerView> followerViewList = new ArrayList<>();
        while(followersEntityIterator.hasNext())
        {
            FollowersEntity followersEntity = followersEntityIterator.next();
            FollowerView followerView = new FollowerView(followersEntity.getFollowerUserId(),
                    followersEntity.getFollower().getUsername());
            followerViewList.add(followerView);
        }
        return followerViewList;
    }

    protected List<FollowerView> getFollowingViewListFromFollowersEntities(List<FollowersEntity> followingEntityList)
    {
        Iterator<FollowersEntity> followingEntityIterator = followingEntityList.iterator();
        List<FollowerView> followingViewList = new ArrayList<>();
        while(followingEntityIterator.hasNext())
        {
            FollowersEntity followersEntity = followingEntityIterator.next();
            FollowerView followerView = new FollowerView(followersEntity.getFollowingUserId(),
                    followersEntity.getFollowed().getUsername());
            followingViewList.add(followerView);
        }
        return followingViewList;
    }
}
