package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.FollowersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.PostCommentsEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.UserPostsEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.FollowerPostView;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.FollowerView;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.PostCommentView;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.UserPostWithLatestCommentsView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

class LoadUserDataDbAdapterBase
{
    protected void addFollowerPostsToPostViewList(List<FollowerPostView> followerPostViewList,
                                                List<UserPostsEntity> followerPosts)
    {
        Iterator<UserPostsEntity> userPostsIterator = followerPosts.iterator();
        while(userPostsIterator.hasNext())
        {
            FollowerPostView followerPostView = getFollowerPostViewFromUserPostsEntity(userPostsIterator.next());
            followerPostViewList.add(followerPostView);
        }
    }

    protected List<PostCommentView> addFollowingPostsToPostCommentViewList(List<PostCommentView> followingPostCommentViewList,
                                                                         List<UserPostsEntity> followingPosts)
    {
        List<PostCommentView> resultList = followingPostCommentViewList;
        Iterator<UserPostsEntity> followingPostsIterator = followingPosts.iterator();
        while(followingPostsIterator.hasNext())
        {
            List<PostCommentView> postCommentViewList = getPostCommentViewListFromUserPostsEntity(followingPostsIterator
                    .next());
            resultList = Stream.of(resultList, postCommentViewList)
                    .flatMap(Collection::stream).toList();
        }
        return resultList;
    }


    protected FollowerPostView getFollowerPostViewFromUserPostsEntity(UserPostsEntity userPostsEntity)
    {
        return new FollowerPostView(userPostsEntity.getId(), userPostsEntity.getPostDate(),
                userPostsEntity.getText(), userPostsEntity.getRegisteredUsersEntity().getUsername());

    }

    protected UserPostWithLatestCommentsView getUserPostWithLatestCommentsView(UserPostsEntity userPostsEntity)
    {
        return new UserPostWithLatestCommentsView(userPostsEntity.getId(), userPostsEntity.getPostDate(),
                userPostsEntity.getRegisteredUsersEntity().getUsername(), userPostsEntity.getText());
    }

    protected PostCommentView getPostCommentViewFromPostCommentsEntity(PostCommentsEntity postCommentsEntity)
    {
        return new PostCommentView(postCommentsEntity.getId(), postCommentsEntity.getCommentDate(),
                postCommentsEntity.getCommentUser(), postCommentsEntity.getComment());
    }

    protected List<PostCommentView> getPostCommentViewListFromUserPostsEntity(UserPostsEntity userPostsEntity)
    {
        List<PostCommentView> postCommentViewList = new ArrayList<>();

        Iterator<PostCommentsEntity> postCommentsEntityIterator = userPostsEntity.getPostComments().
                iterator();

        while(postCommentsEntityIterator.hasNext())
        {
            PostCommentsEntity postCommentsEntity = postCommentsEntityIterator.next();
            postCommentViewList.add(getPostCommentViewFromPostCommentsEntity(postCommentsEntity));
        }
        return postCommentViewList;
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
