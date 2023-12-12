package ai.datawise.textbasedsocialmedia;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.in.web.UserPostController;
import ai.datawise.textbasedsocialmedia.appconfig.ConfigInstances;
import ai.datawise.textbasedsocialmedia.app.utils.InputControllerEndPoints;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainApp {
    private static final Logger logger = LogManager.getLogger(MainApp.class);

    public static void main( String[] args )
    {
        InputControllerEndPoints.init();
        try
        {
            InputControllerEndPoints.generateRegisterUserEndPoint();
            InputControllerEndPoints.generateLoginUserEndPoint();
            InputControllerEndPoints.generateUserPostEndPoint();
            InputControllerEndPoints.generatePostCommentEndPoint();
            InputControllerEndPoints.generateFollowerEndPoint();
            InputControllerEndPoints.generateLoadUserDataControllerEndPoints();
        }
        catch(Exception  t)
        {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
    }
}
