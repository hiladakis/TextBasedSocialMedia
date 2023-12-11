package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@AllArgsConstructor
public class PostCommentResponse
{
    final int postCommentId;
    final Timestamp commentDate;

    @Override
    public String toString()
    {
        return "{'postCommentId':"+ postCommentId +",'commentDate':'"+ commentDate +"'}";
    }
}
