package team4player;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

@Ignore
public class DeliveryDroneTest4 {
    //Field locEnemyBot of type MapLocation - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @Mock
    ArrayList<MapLocation> refineryLocations;
    //Field enemyHqSymetric of type MapLocation - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    //Field enemyHqHorizontal of type MapLocation - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    //Field enemyHqVertical of type MapLocation - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    //Field enemyHqLoc of type MapLocation - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    //Field hqLoc of type MapLocation - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @Mock
    Navigation nav;
    @Mock
    ArrayList<MapLocation> floodedLocations;
    @Mock
    ArrayList<MapLocation> posEnemyHqLoc;
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

    @Ignore
    @Test(expected = NullPointerException.class)
    public void testTakeTurn() throws Exception {
        when(nav.tryFly(any())).thenReturn(true);
        when(nav.flyTo((Direction) any())).thenReturn(true);
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);
        when(comms.broadcastMessage(anyInt(), anyInt(), 1)).thenReturn(true);
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int[]>(Arrays.asList(new int[]{0})));

        deliveryDrone.takeTurn();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurnEnemyBot() throws Exception {
        when(nav.tryFly(any())).thenReturn(true);

        deliveryDrone.takeTurnEnemyBot();
    }

    @Ignore
    @Test(expected = NullPointerException.class)
    public void testTakeTurnRush() throws Exception {
        when(nav.tryFly(any())).thenReturn(true);
        when(nav.flyTo((Direction) any())).thenReturn(true);
        when(comms.broadcastMessage(anyInt(), anyInt(), 1)).thenReturn(true);

        deliveryDrone.takeTurnRush();
    }

    @Ignore
    @Test(expected = NullPointerException.class)
    public void testTakeTurnSearch() throws Exception {
        when(nav.tryFly(any())).thenReturn(true);
        when(nav.flyTo((Direction) any())).thenReturn(true);
        when(comms.broadcastMessage(any(), anyInt(), 1)).thenReturn(true);

        deliveryDrone.takeTurnSearch();
    }

    @Ignore
    @Test(expected = NullPointerException.class)
    public void testTakeTurnRest() throws Exception {
        when(nav.tryFly(any())).thenReturn(true);
        when(nav.flyTo((Direction) any())).thenReturn(true);

        deliveryDrone.takeTurnRest();
    }

    @Test(expected = NullPointerException.class)
    public void testSearchForEnemyHq() throws Exception {
        when(nav.flyTo((Direction) any())).thenReturn(true);
        when(comms.broadcastMessage(any(), anyInt(), 1)).thenReturn(true);

        deliveryDrone.searchForEnemyHq();
    }

    @Test(expected = NullPointerException.class)
    public void testFindEnemyHq() throws Exception {
        when(comms.broadcastMessage(any(), anyInt(), 1)).thenReturn(true);

        deliveryDrone.findEnemyHq();
    }

    @Test(expected = NullPointerException.class)
    public void testPickupEnemyBots() throws Exception {
        when(nav.flyTo((Direction) any())).thenReturn(true);

        deliveryDrone.pickupEnemyBots();
    }

    @Ignore
    @Test(expected = NullPointerException.class)
    public void testMoveLandscaper() throws Exception {
        when(nav.tryFly(any())).thenReturn(true);

        deliveryDrone.moveLandscaper();
    }

    @Ignore
    @Test
    public void testDecipherAllBlockChainMessages() throws Exception {
        deliveryDrone.decipherAllBlockChainMessages();
    }


    @Test(expected = NullPointerException.class)
    public void testDecipherCurrentBlockChainMessage() throws Exception {
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int[]>(Arrays.asList(new int[]{0})));

        deliveryDrone.decipherCurrentBlockChainMessage();
    }

    @Test
    public void testRecordWater() throws Exception {
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);

        deliveryDrone.recordWater();
    }

    @Test
    public void testCalcPosEnemyHqLoc() throws Exception {
        deliveryDrone.calcPosEnemyHqLoc();
    }

    @Ignore
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
}