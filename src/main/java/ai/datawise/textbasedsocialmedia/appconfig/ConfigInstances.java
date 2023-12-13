package ai.datawise.textbasedsocialmedia.appconfig;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.in.web.*;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.*;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.Getter;

public class ConfigInstances
{
    @Getter
    private static final EntityManagerFactory entityManagerFactory;

    static
    {
        entityManagerFactory = Persistence.createEntityManagerFactory("persistenceUnit");
    }

    public static UserRegistrationController getUserRegistrationControllerInstance()
    {
        UserRegistrationDbAdapter userRegistrationDbAdapter = new UserRegistrationDbAdapter();
        UserRegistrationService userRegistrationService = new UserRegistrationService(userRegistrationDbAdapter);
        return new UserRegistrationController(userRegistrationService);
    }

    public static UserLoginController getUserLoginControllerInstance()
    {
        UserLoginDbAdapter userLoginDbAdapter = new UserLoginDbAdapter();
        UserLoginService userLoginService = new UserLoginService(userLoginDbAdapter);
        return new UserLoginController(userLoginService);
    }

    public static UserPostController getUserPostControllerInstance()
    {
        UserPostDbAdapter userPostDbAdapter = new UserPostDbAdapter();
        userPostDbAdapter.setEntityManagerFactory(entityManagerFactory);
        UserPostService userPostService = new UserPostService(userPostDbAdapter);
        return new UserPostController(userPostService);
    }

    public static PostCommentController getPostCommentControllerInstance()
    {
        PostCommentDbAdapter postCommentDbAdapter = new PostCommentDbAdapter();
        postCommentDbAdapter.setEntityManagerFactory(entityManagerFactory);
        PostCommentService postCommentService = new PostCommentService(postCommentDbAdapter);
        return new PostCommentController(postCommentService);
    }

    public static FollowerController getFollowerControllerInstance()
    {
        FollowerDbAdapter followerDbAdapter = new FollowerDbAdapter();
        followerDbAdapter.setEntityManagerFactory(entityManagerFactory);
        FollowerService followerService = new FollowerService(followerDbAdapter);
        return new FollowerController(followerService);
    }

    public static LoadUserDataController getLoadUserDataControllerInstance()
    {
        LoadUserDataDbAdapter loadUserDataDbAdapter = new LoadUserDataDbAdapter();
        loadUserDataDbAdapter.setEntityManagerFactory(entityManagerFactory);
        LoadUserDataService loadUserDataService = new LoadUserDataService(loadUserDataDbAdapter);
        return new LoadUserDataController(loadUserDataService);
    }

}