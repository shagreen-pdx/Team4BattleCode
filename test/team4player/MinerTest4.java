package team4player;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.world.maps.NoU;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

public class MinerTest4 {
    @Mock
    ArrayList<MapLocation> soupLocations;
    @Mock
    ArrayList<MapLocation> refineryLocations;
    @Mock
    Navigation nav;
    @Mock
    ArrayList<MapLocation> posEnemyHqLoc;
    @Mock
    ArrayList<Integer> teamMessages;
    @Mock
    RobotController rc;
    @Mock
    Robot robot;
    @Mock
    Communications comms;
    @InjectMocks
    Miner miner;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurn() throws Exception {
        int[] messages = {7,7,7,7};
        robot.teamMessages.add(messages);
        when(nav.goTo((Direction) any())).thenReturn(true);
        when(nav.goTo((Direction) any())).thenReturn(true);
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);
        when(comms.broadcastMessage(anyInt(), anyInt())).thenReturn(true);
        //when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int>(Arrays.asList(new int[]{0})));

        miner.takeTurn();
    }

    @Test(expected = NullPointerException.class)
    public void testFindClosestRefinery() throws Exception {
        MapLocation result = miner.findClosestRefinery(new ArrayList<MapLocation>(Arrays.asList(null)));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testCheckIfSoupGone() throws Exception {
        miner.checkIfSoupGone();
    }

    @Test
    public void testTryMine() throws Exception {
        boolean result = miner.tryMine(Direction.NORTH);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testTryRefine() throws Exception {
        boolean result = miner.tryRefine(Direction.NORTH);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testCanBuildRefinery() throws Exception {
        boolean result = miner.canBuildRefinery(null);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testCalcPosEnemyHqLoc() throws Exception {
        miner.calcPosEnemyHqLoc();
    }
}

