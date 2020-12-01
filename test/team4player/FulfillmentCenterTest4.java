package team4player;

import battlecode.common.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    @Mock
    Robot robot;
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
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int[]>(Arrays.asList(new int[]{0})));

        fulfillmentCenter.takeTurn();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurn2() throws Exception {
        comms.broadcastedCreation =false;
        fulfillmentCenter.canBuild = true;

        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int[]>(Arrays.asList(new int[]{0})));

        fulfillmentCenter.takeTurn();
    }

    @Test(expected = NullPointerException.class)
    public void testDecipherCurrentBlockChainMessage() throws Exception {
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int[]>(Arrays.asList(new int[]{0})));

        fulfillmentCenter.decipherCurrentBlockChainMessage();
    }

    @Test
    public void testDroneBuilt() throws Exception{
        MapLocation mapLocation = new MapLocation(1,1);
        when(rc.getLocation()).thenReturn(mapLocation);
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);
        int num = fulfillmentCenter.numDeliveryDrones;

        fulfillmentCenter.droneBuilt();
        System.out.println("Test");
        Assert.assertEquals(false, fulfillmentCenter.canBuild);
        Assert.assertEquals(num + 1, fulfillmentCenter.numDeliveryDrones);
    }

    @Test
    public void testBroadcastDroneID() throws Exception{
        MapLocation mapLocation = new MapLocation(1,1);
        when(rc.getLocation()).thenReturn(mapLocation);
        RobotInfo drone = new RobotInfo(1,null,RobotType.DELIVERY_DRONE,1,true,1,1,1,mapLocation);
        when(rc.senseRobotAtLocation(any())).thenReturn(drone);
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);

        Direction dir = Util.randomDirection();
        fulfillmentCenter.broadcastDroneID(dir);
    }

    @Test
    public void testTryBuildDrone() throws Exception{
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);
        when(robot.tryBuild(any(), any())).thenReturn(true);

        fulfillmentCenter.tryBuildDrone();
    }

    @Test
    public void testTryBuildDrone2() throws Exception{
        fulfillmentCenter.numDeliveryDrones = -1;
        when(robot.tryBuild(any(), any())).thenReturn(true);
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);

        fulfillmentCenter.tryBuildDrone();
    }
}
