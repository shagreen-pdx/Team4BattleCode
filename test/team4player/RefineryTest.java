package team4player;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class RefineryTest {
    @Mock
    RobotController rc;
    @Mock
    Communications comms;
    @InjectMocks
    Refinery refinery;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testTakeTurnBroadcastedCreation() throws Exception {
        comms.broadcastedCreation = true;
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);

        refinery.takeTurn();
    }

    @Test
    public void testTakeTurn() throws Exception {
        comms.broadcastedCreation = false;
        MapLocation mapLocation = new MapLocation(1,1);
        when(rc.getLocation()).thenReturn(mapLocation);
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);

        refinery.takeTurn();
    }
}

