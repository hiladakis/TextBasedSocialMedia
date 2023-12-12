package ai.datawise.textbasedsocialmedia.app.usercases.adapters.in.web;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.*;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.LoadUserDataUseCase;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.GetInfoQuery;
import com.google.gson.Gson;
import io.javalin.http.Context;
import lombok.Value;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Value
public class LoadUserDataController
{
    LoadUserDataUseCase loadUserDataUseCase;
    private static final int statusOk = 200;
    private static final int statusError = 400;
    private static final Logger logger = LogManager.getLogger(LoadUserDataController.class);

    public void getFollowingPosts(Context ctx)
    {
        ctx.status(statusError);

        try
        {
            GetInfoQuery query = new GetInfoQuery(Integer.parseInt(ctx.pathParam("id")));
            List<FollowerPostView> followerPostViewList = loadUserDataUseCase.loadFollowingPosts(query);
            Gson gson = new Gson();
            ctx.json(gson.toJson(followerPostViewList));
            ctx.status(statusOk);
        } catch (Exception  t)
        {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
    }

    public void getUserPostAndLatestComments(Context ctx)
    {
        ctx.status(statusError);

        try
        {
            GetInfoQuery query = new GetInfoQuery(Integer.parseInt(ctx.pathParam("id")));
            UserPostWithLatestCommentsView userPostWithLatestCommentsView = loadUserDataUseCase.
                    loadUserPostAndLatestComments(query);
            Gson gson = new Gson();
            ctx.json(gson.toJson(userPostWithLatestCommentsView));
            ctx.status(statusOk);
        } catch (Exception  t)
        {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
    }

    public void getAllPostComments(Context ctx)
    {
        ctx.status(statusError);
        try
        {
            GetInfoQuery query = new GetInfoQuery(Integer.parseInt(ctx.pathParam("id")));
            List<PostCommentView> postCommentViewList = loadUserDataUseCase.
                    loadAllPostComments(query);
            Gson gson = new Gson();
            ctx.json(gson.toJson(postCommentViewList));
            ctx.status(statusOk);
        } catch (Exception  t)
        {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
    }

    public void getLatestCommentsOnAllUserOrFollowingPosts(Context ctx)
    {
        ctx.status(statusError);
        try
        {
            GetInfoQuery query = new GetInfoQuery(Integer.parseInt(ctx.pathParam("id")));
            List<PostCommentView> postCommentViewList = loadUserDataUseCase.
                    loadLatestCommentsOnAllUserOrFollowingPosts(query);
            Gson gson = new Gson();
            ctx.json(gson.toJson(postCommentViewList));
            ctx.status(statusOk);
        } catch (Exception  t)
        {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
    }
    public void getFollowerList(Context ctx)
    {
        ctx.status(statusError);
        try
        {
            GetInfoQuery query = new GetInfoQuery(Integer.parseInt(ctx.pathParam("id")));
            List<FollowerView> followerViewList = loadUserDataUseCase.
                    loadFollowerList(query);
            Gson gson = new Gson();
            ctx.json(gson.toJson(followerViewList));
            ctx.status(statusOk);
        } catch (Exception  t)
        {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
    }

    public void getFollowingList(Context ctx)
    {
        ctx.status(statusError);
        try
        {
            GetInfoQuery query = new GetInfoQuery(Integer.parseInt(ctx.pathParam("id")));
            List<FollowerView> followingViewList = loadUserDataUseCase.
                    loadFollowingList(query);
            Gson gson = new Gson();
            ctx.json(gson.toJson(followingViewList));
            ctx.status(statusOk);
        } catch (Exception  t)
        {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
    }
    public void getUsers(Context ctx)
    {
        ctx.status(statusError);
        try
        {
            List<UserView> userViewList = loadUserDataUseCase.
                    loadUsersByUsernameStr(ctx.pathParam("username"));
            Gson gson = new Gson();
            ctx.json(gson.toJson(userViewList));
            ctx.status(statusOk);
        } catch (Exception  t)
        {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
    }

}
