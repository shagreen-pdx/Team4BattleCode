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

public class MinerTest {
    @Mock
    ArrayList<MapLocation> soupLocations;
    @Mock
    ArrayList<MapLocation> refineryLocations;
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
    Miner miner;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Ignore
    @Test(expected = NullPointerException.class)
    public void testTakeTurn() throws Exception {
        when(nav.goTo((Direction) any())).thenReturn(true);
        when(nav.goTo((Direction) any())).thenReturn(true);
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);
        when(comms.broadcastMessage(anyInt(), anyInt(), 1)).thenReturn(true);
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int[]>(Arrays.asList(new int[]{0})));

        miner.takeTurn();
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
    public void testDecipherAllBlockChainMessages() throws Exception {
        miner.decipherAllBlockChainMessages();
    }

    @Test(expected = NullPointerException.class)
    public void testDecipherCurrentBlockChainMessage() throws Exception {
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int[]>(Arrays.asList(new int[]{0})));

        miner.decipherCurrentBlockChainMessage();
    }

    @Test
    public void testCalcPosEnemyHqLoc() throws Exception {
        miner.calcPosEnemyHqLoc();
    }

    @Test(expected = NullPointerException.class)
    public void testIsPickable() throws Exception {
        boolean result = miner.isPickable(null);
        Assert.assertEquals(true, result);
    }

    @Test
    public void testGetClosestLoc() throws Exception {
        MapLocation result = miner.getClosestLoc(new ArrayList<MapLocation>(Arrays.asList()));
        Assert.assertEquals(null, result);
    }
}

