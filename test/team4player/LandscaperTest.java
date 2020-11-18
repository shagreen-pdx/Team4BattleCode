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
import org.mockito.internal.configuration.MockAnnotationProcessor;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

public class LandscaperTest {
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
    Landscaper landscaper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurn() throws Exception {
        when(nav.goTo((Direction) any())).thenReturn(true);
        //when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int>(Arrays.asList(new int[]{0})));

        landscaper.takeTurn();
    }

    @Test
    public void testTakeTurnJob() throws Exception {
        MapLocation enemyHqLoc = null;
        boolean result = landscaper.takeTurnJob(true);
        Assert.assertEquals(true, result);
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurnRush() throws Exception {
        when(nav.goTo((Direction) any())).thenReturn(true);

        boolean result = landscaper.takeTurnRush(true);
        Assert.assertEquals(true, result);
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurnRushFalse() throws Exception {
        when(nav.goTo((Direction) any())).thenReturn(false);

        boolean result = landscaper.takeTurnRush(true);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testTryDig() throws Exception {
        boolean result = landscaper.tryDig();
        Assert.assertEquals(false, result);
    }

    @Test(expected = NullPointerException.class)
    public void testDecipherAllBlockChainMessages() throws Exception {
        MapLocation hqLoc = null;
        int[] message1 = {0,0,1,1,2,2};
        int[] message2 = {0,0,1,1,2,2};
        robot.teamMessages.add(message1);
        robot.teamMessages.add(message2);
        boolean result = landscaper.decipherAllBlockChainMessages();
        Assert.assertEquals(true, result);
    }

    @Test(expected = NullPointerException.class)
    public void testDecipherCurrentBlockChainMessage() throws Exception {
        //when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int>(Arrays.asList(new int[]{0})));

        landscaper.decipherCurrentBlockChainMessage();
    }

    @Test
    public void testCalcPosEnemyHqLoc() throws Exception {
        landscaper.calcPosEnemyHqLoc();
    }
}
