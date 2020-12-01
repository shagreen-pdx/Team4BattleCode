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

public class HQTest {
    @Mock
    ArrayList<MapLocation> allEnemyDesignSchoolLocations;
    @Mock
    ArrayList<int[]> teamMessages;
    @Mock
    RobotController rc;
    @Mock
    Communications comms;
    @InjectMocks
    HQ hQ;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurn() throws Exception {
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);
        when(comms.broadcastMessage(anyInt(), anyInt())).thenReturn(true);
        when(comms.broadcastMessage(anyInt(), any(), anyInt())).thenReturn(true);
        when(comms.broadcastMessage(anyInt(), anyInt(), anyInt())).thenReturn(true);
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<>(Arrays.asList(new int[]{0})));

        hQ.takeTurn();
    }

    @Test
    public void testTakeRush() throws Exception {
        when(comms.broadcastMessage(anyInt(), anyInt())).thenReturn(true);

        RobotInfo[] result = hQ.takeRush(new RobotInfo[]{null});
        Assert.assertArrayEquals(new RobotInfo[]{null}, result);
    }

    @Test(expected = NullPointerException.class)
    public void testTakeElse() throws Exception {
        when(comms.broadcastMessage(anyInt(), anyInt())).thenReturn(true);
        when(comms.broadcastMessage(anyInt(), any(), anyInt())).thenReturn(true);
        when(comms.broadcastMessage(anyInt(), anyInt(), anyInt())).thenReturn(true);

        RobotInfo[] result = hQ.takeElse(new RobotInfo[]{null});
        Assert.assertArrayEquals(new RobotInfo[]{null}, result);
    }

    @Test
    public void testBuildMiners() throws Exception {
        int result = hQ.buildMiners();
        Assert.assertEquals(0, result);
    }

    @Test(expected = NullPointerException.class)
    public void testDefendHq() throws Exception {
        when(comms.broadcastMessage(anyInt(), any(), anyInt())).thenReturn(true);
        hQ.defendHq(new RobotInfo[]{null});
    }

    @Test(expected = NullPointerException.class)
    public void testBroadcastNumUnitsAdjacentToHq() throws Exception {
        when(comms.broadcastMessage(anyInt(), anyInt(), anyInt())).thenReturn(true);

        hQ.broadcastNumUnitsAdjacentToHq(new RobotInfo[]{null});
    }
    @Test(expected = NullPointerException.class)
    public void testBroadcastNumUnitsAdjacentToHq2() throws Exception {
        when(comms.broadcastMessage(anyInt(), anyInt(), anyInt())).thenReturn(true);
        hQ.numRobotsAdjacentToHq = 3;
        hQ.broadcastNumUnitsAdjacentToHq(new RobotInfo[]{null});
    }


    @Test(expected = NullPointerException.class)
    public void testDecipherEnemyBlockChainMessage() throws Exception {
        when(comms.getEnemyPrevRoundMessages()).thenReturn(new ArrayList<>(Arrays.asList(new int[]{0})));

        hQ.decipherEnemyBlockChainMessage();
    }

    @Test(expected = NullPointerException.class)
    public void testDecipherCurrentBlockChainMessage() throws Exception {
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<>(Arrays.asList(new int[]{0})));

        hQ.decipherCurrentBlockChainMessage();
    }

    @Test(expected = NullPointerException.class)
    public void testIsNearbyRobot() throws Exception {
        boolean result = hQ.isNearbyRobot(new RobotInfo[]{null}, RobotType.HQ);
        Assert.assertEquals(true, result);
    }

    @Test(expected = NullPointerException.class)
    public void testIsNearbyRobot2() throws Exception {
        boolean result = hQ.isNearbyRobot(new RobotInfo[]{null}, RobotType.HQ, Team.A);
        Assert.assertEquals(true, result);
    }
}
