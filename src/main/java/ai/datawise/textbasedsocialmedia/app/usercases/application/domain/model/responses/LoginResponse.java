package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses;

import lombok.Value;

@Value
public class LoginResponse
{
    private int userId;
    private String username;
    private String role;

    @Override
    public String toString()
    {
        return "{'userId':"+ userId +","+"'username':'"+username+ "',"+ "'role':'"+role+"'}";
    }
}
