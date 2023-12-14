package ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.PostComment;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.PostCommentResponse;

public interface PostCommentPort
{
    public boolean isPremiumUser(int user_id);

    public int getPostCommentsNumber(int post_id, int user_id);

    public PostCommentResponse storePostComment(PostComment postComment);
}
