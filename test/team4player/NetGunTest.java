package team4player;

import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class NetGunTest {
    @Mock
    ArrayList<Integer> teamMessages;
    @Mock
    RobotController rc;
    @Mock
    Communications comms;
    @Mock
    RobotInfo robotInfo;
    @InjectMocks
    NetGun netGun;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurn() throws Exception {
        netGun.takeTurn();
    }

    @Test
    public void testFindDrone() throws Exception {
        RobotInfo robot1 = new RobotInfo(12, null, RobotType.DELIVERY_DRONE,2,false, 1,1,1, null);
        RobotInfo robot2 = new RobotInfo(12, null, RobotType.DELIVERY_DRONE,2,false, 1,1,1, null);

        RobotInfo robotInfo[] = {robot1, robot2};

        netGun.findDrone(robotInfo);
    }

    @Test
    public void testTryShootDrone() throws Exception {
        RobotInfo robot = new RobotInfo(12, null, RobotType.DELIVERY_DRONE,2,false, 1,1,1, null);
        netGun.tryShootDrone(robot);
    }
}

