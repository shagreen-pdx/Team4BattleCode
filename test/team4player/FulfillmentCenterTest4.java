package team4player;

import battlecode.common.Direction;
import battlecode.common.RobotController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

public class FulfillmentCenterTest4 {
    @Mock
    ArrayList<int[]> teamMessages;
    @Mock
    RobotController rc;
    @Mock
    Communications comms;
    @InjectMocks
    FulfillmentCenter fulfillmentCenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurn() throws Exception {
        comms.broadcastedCreation =true;
        fulfillmentCenter.canBuild = true;
        
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);
        when(comms.broadcastMessage(anyInt(), anyInt())).thenReturn(true);
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int[]>(Arrays.asList(new int[]{0})));

        fulfillmentCenter.takeTurn();
    }

    @Test(expected = NullPointerException.class)
    public void testDecipherCurrentBlockChainMessage() throws Exception {
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int[]>(Arrays.asList(new int[]{0})));

        fulfillmentCenter.decipherCurrentBlockChainMessage();
    }
}
