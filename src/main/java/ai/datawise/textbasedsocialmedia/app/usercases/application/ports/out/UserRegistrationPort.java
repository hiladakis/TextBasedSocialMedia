package ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.User;

public interface UserRegistrationPort
{
    public boolean storeRegisteredUser(User user);
}
