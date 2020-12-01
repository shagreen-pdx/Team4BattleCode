package team4player;

import battlecode.common.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

public class DeliveryDroneTest {
    //Field locEnemyBot of type MapLocation - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @Mock
    ArrayList<MapLocation> refineryLocations;
    //Field enemyHqSymetric of type MapLocation - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    //Field enemyHqHorizontal of type MapLocation - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    //Field enemyHqVertical of type MapLocation - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    //Field hqLoc of type MapLocation - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @Mock
    Navigation nav;
    @Mock
    ArrayList<MapLocation> floodedLocations;
    @Mock
    ArrayList<MapLocation> posEnemyHqLoc;
    //Field enemyHqLoc of type MapLocation - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @Mock
    ArrayList<int[]> teamMessages;
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
        when(nav.tryFly(any())).thenReturn(true);
        when(nav.flyTo((Direction) any())).thenReturn(true);
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);
        when(comms.broadcastMessage(anyInt(), anyInt(), anyInt())).thenReturn(true);
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<>(Arrays.asList(new int[]{0})));

        deliveryDrone.takeTurn();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurnEnemyBot() throws Exception {
        when(nav.tryFly(any())).thenReturn(true);

        deliveryDrone.takeTurnEnemyBot();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurnRush() throws Exception {
        when(nav.tryFly(any())).thenReturn(true);
        when(nav.flyTo((Direction) any())).thenReturn(true);
        when(comms.broadcastMessage(anyInt(), anyInt(), anyInt())).thenReturn(true);

        deliveryDrone.takeTurnRush();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurnSearch() throws Exception {
        when(nav.tryFly(any())).thenReturn(true);
        when(nav.flyTo((Direction) any())).thenReturn(true);
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);

        deliveryDrone.takeTurnSearch();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurnRest() throws Exception {
        when(nav.tryFly(any())).thenReturn(true);
        when(nav.flyTo((Direction) any())).thenReturn(true);

        deliveryDrone.takeTurnRest();
    }

    @Test(expected = NullPointerException.class)
    public void testSearchForEnemyHq() throws Exception {
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
        when(nav.flyTo((Direction) any())).thenReturn(true);

        deliveryDrone.pickupEnemyBots();
    }

    @Test(expected = NullPointerException.class)
    public void testMoveLandscaper() throws Exception {
        when(nav.tryFly(any())).thenReturn(true);

        deliveryDrone.moveLandscaper();
    }

    @Test
    public void testDecipherAllBlockChainMessages() throws Exception {
        deliveryDrone.decipherAllBlockChainMessages();
    }

    @Test(expected = NullPointerException.class)
    public void testDecipherCurrentBlockChainMessage() throws Exception {
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<>(Arrays.asList(new int[]{0})));

        deliveryDrone.decipherCurrentBlockChainMessage();
    }

    @Test
    public void testRecordWater() throws Exception {
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);

        deliveryDrone.recordWater();
    }

    @Test(expected = NullPointerException.class)
    public void testPickupCows() throws Exception {
        when(nav.flyTo((Direction) any())).thenReturn(true);

        deliveryDrone.pickupCows();
    }

    @Test
    public void testMoooveCow() throws Exception {
        when(nav.tryFly(any())).thenReturn(true);
        when(nav.flyTo((Direction) any())).thenReturn(true);

        deliveryDrone.moooveCow();
    }

    @Test
    public void testCalcPosEnemyHqLoc() throws Exception {
        deliveryDrone.calcPosEnemyHqLoc();
    }

    @Test(expected = NullPointerException.class)
    public void testIsPickable() throws Exception {
        boolean result = deliveryDrone.isPickable(null);
        Assert.assertEquals(true, result);
    }

    @Test
    public void testGetClosestLoc() throws Exception {
        MapLocation result = deliveryDrone.getClosestLoc(new ArrayList<MapLocation>(Arrays.asList()));
        Assert.assertEquals(null, result);
    }

    @Test(expected = NullPointerException.class)
    public void testIsNearbyRobot() throws Exception {
        boolean result = deliveryDrone.isNearbyRobot(new RobotInfo[]{null}, RobotType.HQ);
        Assert.assertEquals(true, result);
    }

    @Test(expected = NullPointerException.class)
    public void testIsNearbyRobot2() throws Exception {
        boolean result = deliveryDrone.isNearbyRobot(new RobotInfo[]{null}, RobotType.HQ, Team.A);
        Assert.assertEquals(true, result);
    }
}