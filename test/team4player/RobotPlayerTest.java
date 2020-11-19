package team4player;

import battlecode.common.RobotController;
import battlecode.common.RobotType;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

public class RobotPlayerTest {
    @Mock
    RobotController rc;

    @Test(expected = NullPointerException.class)
    public void testRun() throws Exception {
        RobotPlayer.run(rc);
    }

    @Test(expected = NullPointerException.class)
    public void testRunException() throws Exception {
        Robot me = new DeliveryDrone(rc);
        RobotPlayer.run(rc);
        //when(mockObject.method(any())).thenThrow(new IllegalStateException());
    }
}

