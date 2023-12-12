package ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.FollowUser;

public interface FollowerPort
{
    public boolean storeFollower(FollowUser followUser) throws Exception;
    public boolean deleteFollower(FollowUser followUser) throws Exception;
}
