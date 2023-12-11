package ai.datawise.textbasedsocialmedia.app.utils;

import ai.datawise.textbasedsocialmedia.appconfig.ConfigInstances;
import io.javalin.Javalin;

public class InputControllerEndPoints
{
    private static Javalin app = null;

    public static void init()
    {
        if( app == null )
        {
            app = Javalin.create().start(7000);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                app.stop(); }));
        }
    }
    public static void generateRegisterUserEndPoint()
    {
        if( app == null )
        {
            throw new RuntimeException("app is not initialized");
        }
        app.post("/users/register/", ctx -> {
            ConfigInstances.getUserRegistrationControllerInstance().registerUser(ctx);
         });
    }

    public static void generateLoginUserEndPoint()
    {
        if( app == null )
        {
            throw new RuntimeException("app is not initialized");
        }
        app.post("/users/login/", ctx -> {
            ConfigInstances.getUserLoginControllerInstance().loginUser(ctx);
        });
    }

    public static void generateUserPostEndPoint()
    {
        if( app == null )
        {
            throw new RuntimeException("app is not initialized");
        }
        app.post("/users/post/", ctx -> {
            ConfigInstances.getUserPostControllerInstance().makePost(ctx);
        });
    }

    public static void generatePostCommentEndPoint()
    {
        if( app == null )
        {
            throw new RuntimeException("app is not initialized");
        }
        app.post("/users/post/comment/", ctx -> {
            ConfigInstances.getPostCommentControllerInstance().postComment(ctx);
        });
    }

    public static void generateFollowerEndPoint()
    {
        if( app == null )
        {
            throw new RuntimeException("app is not initialized");
        }
        app.post("/users/follow/", ctx -> {
            ConfigInstances.getFollowerControllerInstance().followOperation(ctx);
        });
    }

    public static void generateLoadUserDataControllerEndPoints()
    {
        if( app == null )
        {
            throw new RuntimeException("app is not initialized");
        }
        app.get("/users/data/posts/all-comments/{id}", ctx -> {
            ConfigInstances.getLoadUserDataControllerInstance().getAllPostComments(ctx);
        });
        app.get("/users/data/posts/latest-comments/{id}", ctx -> {
            ConfigInstances.getLoadUserDataControllerInstance().getUserPostAndLatestComments(ctx);
        });
        app.get("/users/data/posts/following/latest-comments/{id}", ctx -> {
            ConfigInstances.getLoadUserDataControllerInstance().getLatestCommentsOnAllUserOrFollowingPosts(ctx);
        });
        app.get("/users/data/posts/following/{id}", ctx -> {
            ConfigInstances.getLoadUserDataControllerInstance().getFollowingPosts(ctx);
        });
        app.get("/users/data/following/{id}", ctx -> {
            ConfigInstances.getLoadUserDataControllerInstance().getFollowingList(ctx);
        });
        app.get("/users/data/followers/{id}", ctx -> {
            ConfigInstances.getLoadUserDataControllerInstance().getFollowerList(ctx);
        });
        app.get("/users/search/{username}", ctx -> {
            ConfigInstances.getLoadUserDataControllerInstance().getUsers(ctx);
        });

    }
}
