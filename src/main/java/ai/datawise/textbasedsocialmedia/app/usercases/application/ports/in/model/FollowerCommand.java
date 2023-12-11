package ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.FollowUser;
import ai.datawise.textbasedsocialmedia.app.utils.Validator;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class FollowerCommand
{
    @Positive(message = "followerUserId must be a positive integer")
    int followerUserId;

    @Positive(message = "followedUserId must be a positive integer")
    int followedUserId;

    @Pattern(regexp = "add|remove|Add|Remove|ADD|REMOVE", message="followOperation must be add or remove")
    String followOperation;

    public FollowerCommand(int followerUserId, int followedUserId, String followOperation)
    {
        this.followerUserId = followerUserId;
         this.followedUserId = followedUserId;
         this.followOperation = followOperation;

        Validator.validate(this);
    }

    public FollowUser getFollowUser()
    {
        return new FollowUser(this.getFollowerUserId(), this.getFollowedUserId());
    }

    @Override
    public String toString()
    {
        return "{'followerUserId':"+ followerUserId +",'followedUserId':"+ followedUserId +
                ",'followOperation':'"+followOperation+"'}";
    }

}
