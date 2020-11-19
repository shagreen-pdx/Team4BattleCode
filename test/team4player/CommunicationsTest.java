package team4player;

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

public class CommunicationsTest {
    @Mock
    RobotController rc;
    @InjectMocks
    Communications communications;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetSecretCode() throws Exception {
        int result = communications.getSecretCode();
        Assert.assertEquals(9234, result);
    }

    @Test(expected = NullPointerException.class)
    public void testBroadcastMessage() throws Exception {
        boolean result = communications.broadcastMessage(null, 0);
        Assert.assertEquals(true, result);
    }

    @Test
    public void testBroadcastMessage2() throws Exception {
        boolean result = communications.broadcastMessage(0, 0);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testGetAllTeamMessages() throws Exception {
        communications.getAllTeamMessages(new ArrayList<int[]>(Arrays.asList(new int[]{0})));
    }

    @Test(expected = NullPointerException.class)
    public void testGetPrevRoundMessages() throws Exception {
        ArrayList<int[]> result = communications.getPrevRoundMessages();
        Assert.assertEquals(new ArrayList<int[]>(Arrays.asList(new int[]{0})), result);
    }

    @Test(expected = NullPointerException.class)
    public void testGetEnemyPrevRoundMessages() throws Exception {
        ArrayList<int[]> result = communications.getEnemyPrevRoundMessages();
        Assert.assertEquals(new ArrayList<int[]>(Arrays.asList(new int[]{0})), result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme