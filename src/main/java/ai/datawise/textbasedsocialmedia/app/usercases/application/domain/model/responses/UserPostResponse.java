package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@AllArgsConstructor
@Getter
public class UserPostResponse
{
    final int postId;
    final Timestamp postDate;

    @Override
    public String toString()
    {
        return "{'postId':"+ postId +",'postDate':'"+ postDate +"'}";
    }
}
