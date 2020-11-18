package team4player;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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

  @Ignore
  @Test
  public void testTakeTurn() throws Exception {
    unit.takeTurn();
  }

  @Test (expected = NullPointerException.class)
  public void testTryBuildBuilding() throws Exception {
    boolean result = unit.tryBuildBuilding(RobotType.HQ, Direction.NORTH);
    Assert.assertEquals(true, result);
  }

  @Test
  public void testCalcPosEnemyHqLoc() throws Exception {
    unit.calcPosEnemyHqLoc();
  }
}
