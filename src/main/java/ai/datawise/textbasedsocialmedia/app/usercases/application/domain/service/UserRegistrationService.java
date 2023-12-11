package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service;

import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserRegistrationCommand;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.UserRegistrationUseCase;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.UserRegistrationPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRegistrationService implements UserRegistrationUseCase
{
    private final UserRegistrationPort userRegistrationPort;
    @Override
    public boolean registerUser(UserRegistrationCommand command)
    {
        return(userRegistrationPort.storeRegisteredUser(command.getUser()));
    }
}
