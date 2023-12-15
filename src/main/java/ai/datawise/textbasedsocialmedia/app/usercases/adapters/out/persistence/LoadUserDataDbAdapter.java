package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.FollowersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.*;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.LoadUserDataPort;
import ai.datawise.textbasedsocialmedia.app.utils.DbUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class LoadUserDataDbAdapter extends LoadUserDataDbAdapterBase implements LoadUserDataPort
{
    private static final Logger logger = LogManager.getLogger(LoadUserDataDbAdapter.class);

    public List<FollowerPostView> getFollowingPosts(int userId)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();
        List<FollowerPostView> followerPostViewList = new ArrayList<>();
        //inner join between three tables : registered_users, user_posts and followers
        String queryFollowingPosts = "SELECT P.id, P.post_date, P.text, R.username " +
                "FROM giannis.registered_users AS R " +
                "INNER JOIN giannis.user_posts AS P ON P.user_id = R.id " +
                "INNER JOIN giannis.followers AS F ON F.followed_user_id = P.user_id" +
                " AND F.follower_user_id = "+userId + " ORDER BY P.post_date DESC LIMIT 600;";

        List<Object[]> resultList = entityManager.createNativeQuery(queryFollowingPosts).getResultList();
        addFollowerPostsToPostViewList(followerPostViewList, resultList);

        return followerPostViewList;
    }

    public UserPostWithLatestCommentsView getUserPostAndLatestComments(int postId)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();
        String queryLatestPostComments = "SELECT id, comment_date, comment_user, comment FROM "
                + "giannis.post_comments WHERE post_id="+postId+" ORDER BY comment_date DESC LIMIT 100;";

        List<Object[]> postCommentList = entityManager.createNativeQuery(queryLatestPostComments)
                .getResultList();

        List<PostCommentView> postCommentViewList = getPostCommentViewListFromQuery(postCommentList);

        String queryPost = "SELECT P.id, P.post_date, R.username, P.text FROM "
                + "giannis.user_posts AS P " +
                "INNER JOIN giannis.registered_users AS R ON P.user_id = R.id AND P.id = "
                +postId+";";

        List<Object[]> resultPost = entityManager.createNativeQuery(queryPost)
                .getResultList();

        return getUserPostWithLatestCommentsViewFromQuery(resultPost, postCommentViewList);
    }
    public List<PostCommentView> getAllPostComments(int postId)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();
        String queryPostComments = "SELECT id, comment_date, comment_user, comment FROM "
                + "giannis.post_comments WHERE post_id="+postId+" LIMIT 600;";

        List<Object[]> postCommentList = entityManager.createNativeQuery(queryPostComments)
                .getResultList();

        List<PostCommentView> postCommentViewList = getPostCommentViewListFromQuery(postCommentList);
        return postCommentViewList;
    }
    public List<PostCommentView> getLatestCommentsOnAllUserOrFollowingPosts(int userId)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();
        //inner join between three tables :  post_comments, user_posts and follower
        //and a UNION with another inner join between user_posts and post_comments
        String queryFollowingPosts = "SELECT C.id, C.comment_date, C.comment_user, C.comment " +
                "FROM giannis.post_comments AS C " +
                "INNER JOIN giannis.user_posts AS P ON P.id = C.post_id " +
                "INNER JOIN giannis.followers AS F ON F.followed_user_id = P.user_id" +
                " AND F.follower_user_id = "+userId +
                " UNION " +
                "SELECT C.id, C.comment_date, C.comment_user, C.comment " +
                "FROM giannis.post_comments AS C " +
                "INNER JOIN giannis.user_posts AS P ON P.id = C.post_id AND P.user_id = " + userId +
                " ORDER BY comment_date DESC LIMIT 100;";

        List<Object[]> postCommentList = entityManager.createNativeQuery(queryFollowingPosts).getResultList();
        return getPostCommentViewListFromQuery(postCommentList);
    }
    public List<FollowerView> getFollowerList(int userId)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();
        String queryFollowerList = "select F from FollowersEntity F inner join F.followed where F.followingUserId = "
                +userId;
        TypedQuery<FollowersEntity> queryFollowers = entityManager.createQuery(queryFollowerList,FollowersEntity.class);
        queryFollowers.setFirstResult(0);
        queryFollowers.setMaxResults(600);
        List<FollowersEntity> followersEntityList = queryFollowers.getResultList();

        return getFollowerViewListFromFollowersEntities(followersEntityList);
    }
    public List<FollowerView> getFollowingList(int userId)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();
        String queryFollowerList = "select F from FollowersEntity F inner join F.follower where F.followerUserId = "
                +userId;
        TypedQuery<FollowersEntity> queryFollowers = entityManager.createQuery(queryFollowerList,FollowersEntity.class);
        queryFollowers.setFirstResult(0);
        queryFollowers.setMaxResults(600);
        List<FollowersEntity> followingEntities = queryFollowers.getResultList();
        List<FollowerView> followingViewList =  getFollowingViewListFromFollowersEntities(followingEntities);
        return followingViewList;
    }

    public List<UserView> getUsersByUsernameStr(String usernameStr)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();

        String queryStr = "SELECT id, username FROM giannis.registered_users WHERE username LIKE '%"
                +usernameStr+"%' LIMIT 100";
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
