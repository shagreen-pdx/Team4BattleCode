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
  //Field enemyHqLoc of type MapLocation - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
  //Field hqLoc of type MapLocation - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
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
    unit.takeTurn();
  }

  @Test
  public void testTryBuildBuilding() throws Exception {
    unit.hqLoc = new MapLocation(1,1);
    when(rc.getLocation()).thenReturn(new MapLocation(1,1));
    when(rc.isReady()).thenReturn(true);
    when(rc.canBuildRobot(any(), any())).thenReturn(true);
    boolean result = unit.tryBuildBuilding(RobotType.HQ, Direction.NORTH);
    Assert.assertEquals(false, result);
  }

  @Test
  public void testTryBuildBuilding1() throws Exception {
    unit.hqLoc = new MapLocation(100,100);
    when(rc.getLocation()).thenReturn(new MapLocation(1,1));
    when(rc.isReady()).thenReturn(true);
    when(rc.canBuildRobot(any(), any())).thenReturn(true);

    boolean result = unit.tryBuildBuilding(RobotType.HQ, Direction.NORTH);
    Assert.assertEquals(true, result);
  }

  @Test
  public void testTryBuildBuilding2() throws Exception {
    unit.hqLoc = new MapLocation(100,100);
    when(rc.getLocation()).thenReturn(new MapLocation(1,1));
    when(rc.isReady()).thenReturn(false);
    when(rc.canBuildRobot(any(), any())).thenReturn(false);

    boolean result = unit.tryBuildBuilding(RobotType.HQ, Direction.NORTH);
    Assert.assertEquals(false, result);
  }

  @Test
  public void testCalcPosEnemyHqLoc() throws Exception {
    unit.hqLoc = new MapLocation(1,1);
    nav.mapWidth = 100;
    nav.mapHeight = 100;
    unit.calcPosEnemyHqLoc();
  }

  @Test
  public void testCalcPosEnemyHqLoc1() throws Exception {
    unit.hqLoc = new MapLocation(1,1);
    nav.mapWidth = 100;
    nav.mapHeight = 200;
    unit.calcPosEnemyHqLoc();
  }

  @Test
  public void testGetClosestLoc() throws Exception {
    ArrayList<MapLocation> mapLocations = new ArrayList<>();
    for (int i = 0; i < 5; i++)
    {
      MapLocation temp = new MapLocation(i,i);
      mapLocations.add(temp);
    }
    when(rc.getLocation()).thenReturn(new MapLocation(50,50));
    MapLocation closest = unit.getClosestLoc(mapLocations);
  }

}
