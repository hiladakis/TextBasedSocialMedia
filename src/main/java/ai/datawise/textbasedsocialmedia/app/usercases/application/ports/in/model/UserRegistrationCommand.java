package ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.User;
import ai.datawise.textbasedsocialmedia.app.utils.Validator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class UserRegistrationCommand implements Serializable
{
    @Email(message = "username must be a valid email")
    @NotNull(message = "username must not be null")
    private final String username;
    @Size(min = 5, max = 20, message = "password must be between 5 and 20 characters")
    @Pattern(regexp = "[a-zA-Z0-9]+", message="password must contain letters or decimal digits")
    private final String password;
    @Pattern(regexp = "Free|Premium|free|premium|FREE|PREMIUM", message="user role must be either Free or Premium")
    private final String role;

    public UserRegistrationCommand(String username, String password, String role)
    {
        this.username = username;
        this.password = password;
        this.role = role;
        Validator.validate(this);
    }

    public User getUser()
    {
        return new User(this.getUsername(), this.getPassword(), this.getRole());
    }

    @Override
    public String toString()
    {
        return "{'username':'"+username+ "',"+ "'password':'"+password+"','role':'"+role+"'}";
    }
}
