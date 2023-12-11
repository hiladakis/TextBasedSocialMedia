package ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.UserPost;
import ai.datawise.textbasedsocialmedia.app.utils.Validator;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class UserPostCommand implements Serializable
{
    @Positive(message = "userId must be a positive integer")
    int userId;

    @Size(min = 1, max = 3000, message = "text cannot exceed 3000 characters")
    String text;

    public UserPostCommand(int userId, String text)
    {
        this.userId = userId;
        this.text = text;
        Validator.validate(this);
    }

    public UserPost getUserPost()
    {
        return new UserPost(this.userId, this.getText());
    }

    @Override
    public String toString()
    {
        return "{'userId':"+ userId +","+ "'text':'"+text+"'}";
    }

}
