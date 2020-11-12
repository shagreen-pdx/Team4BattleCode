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
import java.util.Collection;

import static org.mockito.Mockito.*;

public class MinerTest {
  @Mock
  ArrayList<MapLocation> soupLocations;
  @Mock
  ArrayList<MapLocation> refineryLocations;
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
  Miner miner;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testCheckIfSoupGone() throws Exception {
    miner.checkIfSoupGone();
  }

  @Test
  public void testTryRefine() throws Exception {
    boolean result = miner.tryRefine(Direction.NORTH);
    Assert.assertEquals(false, result);
  }

  @Test
  public void testCanBuildRefinery() throws Exception {
    boolean result = miner.canBuildRefinery(null);
    Assert.assertEquals(false, result);
  }

  @Test
  public void testCalcPosEnemyHqLoc() throws Exception {
    miner.calcPosEnemyHqLoc();
  }
}
