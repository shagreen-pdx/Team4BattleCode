package team4player;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.scalatest.Ignore;

import java.util.ArrayList;

import static org.mockito.Mockito.*;



public class VaporatorTest {
    @Mock
    Communications comms;
    @InjectMocks
    Vaporator vaporator;
    @Mock
    RobotController rc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

//    @Test(expected = NullPointerException.class)
//    public void testTakeTurn() throws Exception {
//        vaporator.takeTurn();
//    }
    @Test
    public void testTakeTurn2() throws Exception {
        MapLocation location = new MapLocation(3,5);

        //comms.broadcastMessage(location, 0);
        comms.broadcastedCreation = true;
        vaporator.takeTurn();
    }
}
