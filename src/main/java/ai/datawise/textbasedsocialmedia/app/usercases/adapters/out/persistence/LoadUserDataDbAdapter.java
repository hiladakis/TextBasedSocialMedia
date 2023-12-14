package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.FollowersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.RegisteredUsersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.UserPostsEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.*;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.LoadUserDataPort;
import ai.datawise.textbasedsocialmedia.app.utils.DbUtils;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoadUserDataDbAdapter extends LoadUserDataDbAdapterBase implements LoadUserDataPort
{
    private static final Logger logger = LogManager.getLogger(LoadUserDataDbAdapter.class);

    public List<FollowerPostView> getFollowingPosts(int userId)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();
        RegisteredUsersEntity userEntity = entityManager
                .find(RegisteredUsersEntity.class, userId);

        List<FollowersEntity> followingEntities = userEntity.getFollowingEntities();
        Iterator<FollowersEntity> followingEntitiesIterator = followingEntities.iterator();

        List<FollowerPostView> followerPostViewList = new ArrayList<>();

        while( followingEntitiesIterator.hasNext() )
        {
            FollowersEntity followersEntity = followingEntitiesIterator.next();
            RegisteredUsersEntity followedByUserEntity = followersEntity.getFollowed();

            List<UserPostsEntity> followerPosts = followedByUserEntity.getUserPosts();
            addFollowerPostsToPostViewList(followerPostViewList, followerPosts);
        }

        Comparator<FollowerPostView> followerPostViewComparator = (pv1, pv2) -> pv2.getPostDate().
                compareTo(pv1.getPostDate());
        Collections.sort(followerPostViewList, followerPostViewComparator);
        return followerPostViewList;
    }

    public UserPostWithLatestCommentsView getUserPostAndLatestComments(int postId)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();

        UserPostsEntity userPostsEntity = entityManager
                .find(UserPostsEntity.class, postId);

        UserPostWithLatestCommentsView userPostWithLatestCommentsView =
                getUserPostWithLatestCommentsView(userPostsEntity);

        List<PostCommentView> postCommentViewList = getPostCommentViewListFromUserPostsEntity(userPostsEntity);

        Comparator<PostCommentView> postCommentViewComparator = (pc1, pc2) -> pc2.getCommentDate().
                compareTo(pc1.getCommentDate());
        Collections.sort(postCommentViewList, postCommentViewComparator);
        if(postCommentViewList.size()>100)
        {
            userPostWithLatestCommentsView.setLatestComments( postCommentViewList.stream().
                    limit(100).collect(Collectors.toList()));
        }
        else{
            userPostWithLatestCommentsView.setLatestComments(postCommentViewList);
        }
        return userPostWithLatestCommentsView;
    }
    public List<PostCommentView> getAllPostComments(int postId)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();
        UserPostsEntity userPostsEntity = entityManager
                .find(UserPostsEntity.class, postId);

        List<PostCommentView> postCommentViewList = getPostCommentViewListFromUserPostsEntity(userPostsEntity);
        return postCommentViewList;
    }
    public List<PostCommentView> getLatestCommentsOnAllUserOrFollowingPosts(int userId)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();
        RegisteredUsersEntity userEntity = entityManager
                .find(RegisteredUsersEntity.class, userId);

        List<PostCommentView> postCommentViewList = new ArrayList<>();

        Iterator<UserPostsEntity> userPostsEntityIterator = userEntity.getUserPosts().iterator();

        while(userPostsEntityIterator.hasNext())
        {
            List<PostCommentView> postCommentViewListTmp =
                    getPostCommentViewListFromUserPostsEntity(userPostsEntityIterator.next());
            postCommentViewList = Stream.of(postCommentViewList, postCommentViewListTmp)
                    .flatMap(Collection::stream).toList();
        }

        Iterator<FollowersEntity> followingEntitiesIterator = userEntity.getFollowingEntities().iterator();

        while( followingEntitiesIterator.hasNext() )
        {
            FollowersEntity followersEntity = followingEntitiesIterator.next();
            RegisteredUsersEntity followedByUserEntity = followersEntity.getFollowed();

            List<UserPostsEntity> followingPosts = followedByUserEntity.getUserPosts();
            postCommentViewList = addFollowingPostsToPostCommentViewList(postCommentViewList, followingPosts);
        }
        //avoiding UnsupportedOperationException
        List<PostCommentView> postCommentViewMutableList = new ArrayList<>(postCommentViewList);
        Comparator<PostCommentView> postCommentViewComparator = (pc1, pc2) -> pc2.getCommentDate().
                compareTo(pc1.getCommentDate());
        Collections.sort(postCommentViewMutableList, postCommentViewComparator);

        if(postCommentViewMutableList.size()>100)
        {
            postCommentViewMutableList = postCommentViewList.stream().
                    limit(100).collect(Collectors.toList());
        }
        return postCommentViewMutableList;
    }
    public List<FollowerView> getFollowerList(int userId)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();
        RegisteredUsersEntity userEntity = entityManager
                .find(RegisteredUsersEntity.class, userId);

        List<FollowersEntity> followersEntityList = userEntity.getFollowersEntities();
        return getFollowerViewListFromFollowersEntities(followersEntityList);
    }
    public List<FollowerView> getFollowingList(int userId)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();
        RegisteredUsersEntity userEntity = entityManager
                .find(RegisteredUsersEntity.class, userId);

        List<FollowersEntity> followingEntities = userEntity.getFollowingEntities();
        List<FollowerView> followingViewList =  getFollowingViewListFromFollowersEntities(followingEntities);
        return followingViewList;
    }

    public List<UserView> getUsersByUsernameStr(String usernameStr)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();

        String queryStr = "SELECT id, username FROM giannis.registered_users WHERE username LIKE '%"
                +usernameStr+"%'";
        List<Object[]> resultList = entityManager.createNativeQuery(queryStr).getResultList();
        Iterator<Object[]> iteratorResultList = resultList.iterator();
        List<UserView> targetList = new ArrayList<>();
        while(iteratorResultList.hasNext())
        {
            Object[] resultObj = iteratorResultList.next();
            targetList.add(new UserView((Integer)resultObj[0],(String)resultObj[1]));
        }
        return targetList;
    }
}
