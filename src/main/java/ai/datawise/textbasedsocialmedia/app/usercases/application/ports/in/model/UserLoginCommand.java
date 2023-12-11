package ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.LoginUser;
import ai.datawise.textbasedsocialmedia.app.utils.Validator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class UserLoginCommand implements Serializable
{
    @Email(message = "username must be a valid email")
    @NotNull(message = "username must not be null")
    private final String username;
    @Size(min = 5, max = 20, message = "password must be between 5 and 20 characters")
    @Pattern(regexp = "[a-zA-Z0-9]+", message="password must contain letters or decimal digits")
    private final String password;

    public UserLoginCommand(String username, String password)
    {
        this.username = username;
        this.password = password;
        Validator.validate(this);
    }

    public LoginUser getLoginUser()
    {
        return new LoginUser(this.getUsername(), this.getPassword());
    }

    @Override
    public String toString()
    {
        return "{'username':'"+username+ "',"+ "'password':'"+password+"'}";
    }
}

