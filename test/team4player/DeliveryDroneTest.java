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

public class DeliveryDroneTest {
  @Mock
  ArrayList<MapLocation> refineryLocations;
  @Mock
  ArrayList<MapLocation> floodedLocations;
  Navigation nav;
  @Mock
  ArrayList<MapLocation> posEnemyHqLoc;
  @Mock
  ArrayList<Integer> teamMessages;
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

  @Test (expected = NullPointerException.class)
  public void testSearchForEnemyHq() throws Exception {
    when(nav.flyTo((Direction) any())).thenReturn(true);
    when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);

    deliveryDrone.searchForEnemyHq();
  }

  @Test(expected = NullPointerException.class)
  public void testFindEnemyHq() throws Exception {
    when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);

    deliveryDrone.findEnemyHq();
  }

  @Test
  public void testDecipherAllBlockChainMessages() throws Exception {
    deliveryDrone.decipherAllBlockChainMessages();
  }


  @Test
  public void testRecordWater() throws Exception {
    deliveryDrone.recordWater();
  }

  @Test
  public void testCalcPosEnemyHqLoc() throws Exception {
    deliveryDrone.calcPosEnemyHqLoc();
  }
}

