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

public class LandscaperTest {
    @Mock
    ArrayList<MapLocation> enemyBuildings;
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
    Landscaper landscaper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurn() throws Exception {
        when(nav.tryMoveForward(any())).thenReturn(true);
        when(nav.goTo((Direction) any())).thenReturn(true);
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int[]>(Arrays.asList(new int[]{0})));

        landscaper.takeTurn();
    }

    @Test
    public void testTakeTurnJob() throws Exception {
        landscaper.takeTurnJob();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurnRush() throws Exception {
        when(nav.tryMoveForward(any())).thenReturn(true);
        when(nav.goTo((Direction) any())).thenReturn(true);

        landscaper.takeTurnRush();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurnRest() throws Exception {
        when(nav.goTo((Direction) any())).thenReturn(true);

        landscaper.takeTurnRest();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurnAtEnemyHQ() throws Exception {
        when(nav.goTo((Direction) any())).thenReturn(true);

        landscaper.takeTurnAtEnemyHQ();
    }

    @Test(expected = NullPointerException.class)
    public void testDigToDir() throws Exception {
        landscaper.digToDir(Direction.NORTH);
    }

    @Test
    public void testDigDown() throws Exception {
        landscaper.digDown(Direction.NORTH);
    }

    @Test
    public void testDigUp() throws Exception {
        landscaper.digUp(Direction.NORTH);
    }

    @Test
    public void testDecipherAllBlockChainMessages() throws Exception {
        landscaper.decipherAllBlockChainMessages();
    }

    @Test(expected = NullPointerException.class)
    public void testDecipherCurrentBlockChainMessage() throws Exception {
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int[]>(Arrays.asList(new int[]{0})));

        landscaper.decipherCurrentBlockChainMessage();
    }

    @Test
    public void testCalcPosEnemyHqLoc() throws Exception {
        landscaper.calcPosEnemyHqLoc();
    }

    @Test(expected = NullPointerException.class)
    public void testIsPickable() throws Exception {
        boolean result = landscaper.isPickable(null);
        Assert.assertEquals(true, result);
    }

    @Test
    public void testGetClosestLoc() throws Exception {
        MapLocation result = landscaper.getClosestLoc(new ArrayList<MapLocation>(Arrays.asList()));
        Assert.assertEquals(null, result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme