package team4player;

import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
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

public class DesignSchoolTest {
    //Field enemyHqLoc of type MapLocation - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @Mock
    ArrayList<int[]> teamMessages;
    @Mock
    RobotController rc;
    @Mock
    Communications comms;
    @InjectMocks
    DesignSchool designSchool;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurn() throws Exception {
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);
        when(comms.broadcastMessage(anyInt(), anyInt())).thenReturn(true);
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<>(Arrays.asList(new int[]{0})));

        designSchool.takeTurn();
    }

    @Test
    public void testRampUp() throws Exception {
        when(comms.broadcastMessage(anyInt(), anyInt())).thenReturn(true);

        designSchool.rampUp();
    }

    @Test
    public void testElseProduction() throws Exception {
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);

        designSchool.elseProduction();
    }

    @Test
    public void testDecipherAllBlockChainMessages() throws Exception {
        designSchool.decipherAllBlockChainMessages();
    }

    @Test(expected = NullPointerException.class)
    public void testDecipherCurrentBlockChainMessage() throws Exception {
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<>(Arrays.asList(new int[]{0})));

        designSchool.decipherCurrentBlockChainMessage();
    }

    @Ignore
    @Test
    public void testDecipherCurrentBlockChainMessageHelper() throws Exception {
        int[] result = designSchool.decipherCurrentBlockChainMessageHelper(new int[]{0});
        //Assert.assertArrayEquals(new int[]{0}, result);
    }

    @Test(expected = NullPointerException.class)
    public void testIsNearbyRobot() throws Exception {
        boolean result = designSchool.isNearbyRobot(new RobotInfo[]{null}, RobotType.HQ);
        Assert.assertEquals(true, result);
    }

    @Test(expected = NullPointerException.class)
    public void testIsNearbyRobot2() throws Exception {
        boolean result = designSchool.isNearbyRobot(new RobotInfo[]{null}, RobotType.HQ, Team.A);
        Assert.assertEquals(true, result);
    }
}

