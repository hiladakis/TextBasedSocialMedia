package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model;

import lombok.Value;

@Value
public class FollowUser
{
    int followerUserId;
    int followingUserId;
}
