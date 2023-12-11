package ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in;

import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.FollowerCommand;

public interface FollowerUseCase
{
    public boolean addFollower(FollowerCommand followerCommand);
    public boolean removeFollower(FollowerCommand followerCommand);
}
