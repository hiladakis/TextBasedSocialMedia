package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service;

import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.FollowerUseCase;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.FollowerCommand;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.FollowerPort;
import ai.datawise.textbasedsocialmedia.app.utils.DbUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FollowerService implements FollowerUseCase
{
    private final FollowerPort followerPort;
    @Override
    public boolean addFollower(FollowerCommand followerCommand) throws Exception{
        return DbUtils.inTransaction(entityManager ->
                followerPort.storeFollower(followerCommand.getFollowUser()));
    }

    @Override
    public boolean removeFollower(FollowerCommand followerCommand) throws Exception{
        return DbUtils.inTransaction(entityManager ->
                followerPort.deleteFollower(followerCommand.getFollowUser()));
    }
}
