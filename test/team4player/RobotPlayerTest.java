package team4player;

import battlecode.common.RobotController;
import battlecode.common.RobotType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class RobotPlayerTest {
    @Mock
    RobotController rc;
    @Mock
    Robot robot;
    @InjectMocks
    RobotPlayer robotPlayer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NullPointerException.class)
    public void testRun() throws Exception {
        robotPlayer.run(rc);
    }

    @Test(expected = NullPointerException.class)
    public void testRunException() throws Exception {
        Robot me = new DeliveryDrone(rc);
        RobotPlayer.run(rc);
        //when(mockObject.method(any())).thenThrow(new IllegalStateException());
    }
}

