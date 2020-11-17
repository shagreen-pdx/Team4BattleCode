package team4player;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class UnitTest {
  @Mock
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
  Unit unit;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testTakeTurn() throws Exception {
    nav.prevLocations = new ArrayList<MapLocation>(10);
    for(int i = 0; i < 10; i++)
      nav.prevLocations.add(new MapLocation(i + 1,i + 10));
    //Assert.assertEquals(10, nav.prevLocations.size());
    unit.takeTurn();
    Assert.assertEquals(10, nav.prevLocations.size());
  }

  @Test (expected = NullPointerException.class)
  public void testTryBuildBuilding() throws Exception {
    boolean result = unit.tryBuildBuilding(RobotType.HQ, Direction.NORTH);
    Assert.assertEquals(true, result);
  }
  public void testTryBuildBuildingFirstCondition() throws Exception {

    boolean result = unit.tryBuildBuilding(RobotType.HQ, Direction.NORTH);
    Assert.assertEquals(true, result);
  }


  @Test
  public void testCalcPosEnemyHqLoc() throws Exception {
    unit.calcPosEnemyHqLoc();
  }

  @Test
  public void testCalcPosEnemyHqLocNotNull() throws Exception {
    unit.hqLoc = new MapLocation(3,4);
    unit.calcPosEnemyHqLoc();
  }
}

