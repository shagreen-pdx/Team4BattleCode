package team4player;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

public class DeliveryDroneTest4 {
    @Mock
    ArrayList<MapLocation> refineryLocations;
    @Mock
    ArrayList<MapLocation> floodedLocations;
    //Field enemyHqLoc of type MapLocation - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    //Field hqLoc of type MapLocation - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @Mock
    Navigation nav;
    @Mock
    ArrayList<MapLocation> posEnemyHqLoc;
    @Mock
    ArrayList<Integer> teamMessages;
    @Mock
    RobotController rc;
    @Mock
    Communications comms;
    @InjectMocks
    DeliveryDrone deliveryDrone;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurn() throws Exception {
//        when(nav.tryFly(any())).thenReturn(true);
//        when(nav.flyTo((Direction) any())).thenReturn(true);
//        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);
//        when(comms.broadcastMessage(anyInt(), anyInt())).thenReturn(true);
        //when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<Integer>(Arrays.asList(new int[]{0})));

        deliveryDrone.takeTurn();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurnBot() throws Exception {
        deliveryDrone.haveEnemyBot = true;
        when(nav.tryFly(any())).thenReturn(true);

        deliveryDrone.takeTurnBot();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurnRush() throws Exception {
        when(nav.tryFly(any())).thenReturn(true);
        when(nav.flyTo((Direction) any())).thenReturn(true);
        when(comms.broadcastMessage(anyInt(), anyInt())).thenReturn(true);

        deliveryDrone.takeTurnRush();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurnSearch() throws Exception {
        deliveryDrone.search = true;
        when(nav.tryFly(any())).thenReturn(true);
        when(nav.flyTo((Direction) any())).thenReturn(true);
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);

        deliveryDrone.takeTurnSearch();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurnRest() throws Exception {
        when(nav.tryFly(any())).thenReturn(true);
        deliveryDrone.takeTurnRest();
    }

    @Test(expected = NullPointerException.class)
    public void testSearchForEnemyHq() throws Exception {
        posEnemyHqLoc = null;
        when(nav.flyTo((Direction) any())).thenReturn(true);
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);

        deliveryDrone.searchForEnemyHq();
    }

    @Test(expected = NullPointerException.class)
    public void testFindEnemyHq() throws Exception {
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);

        deliveryDrone.findEnemyHq();
    }

    @Test(expected = NullPointerException.class)
    public void testPickupEnemyBots() throws Exception {
        when(nav.tryFly(any())).thenReturn(true);

        deliveryDrone.pickupEnemyBots();
    }

    @Test
    public void testDecipherAllBlockChainMessages() throws Exception {
        deliveryDrone.decipherAllBlockChainMessages();
    }

    @Test(expected = NullPointerException.class)
    public void testDecipherCurrentBlockChainMessage() throws Exception {
        //when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int>(Arrays.asList(new int[]{0})));

        deliveryDrone.decipherCurrentBlockChainMessage();
    }

    @Test
    public void testRecordWater() throws Exception {
        deliveryDrone.recordWater();
    }

    @Test
    public void testFindClosestFloodedLoc() throws Exception {
        MapLocation result = deliveryDrone.findClosestFloodedLoc(new ArrayList<MapLocation>(Arrays.asList()));
        //Assert.assertEquals(null, result);
    }

    @Test
    public void testCalcPosEnemyHqLoc() throws Exception {
        deliveryDrone.calcPosEnemyHqLoc();
    }
}

