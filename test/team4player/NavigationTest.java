package team4player;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class NavigationTest {
  @Mock
  RobotController rc;
  @Mock
  ArrayList<MapLocation> prevLocations;
  @InjectMocks
  Navigation navigation;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testTryMove() throws Exception {
    boolean result = navigation.tryMove(Direction.NORTH);
    Assert.assertEquals(false, result);
  }

  @Test
  public void testTryMove2() throws Exception {
    boolean result = navigation.tryMove();
    Assert.assertEquals(false, result);
  }

  @Test(expected = NullPointerException.class)
  public void testGoTo() throws Exception {
    boolean result = navigation.goTo(Direction.NORTH);
    Assert.assertEquals(true, result);
  }

  @Test(expected = NullPointerException.class)
  public void testGoTo2() throws Exception {
    MapLocation location = new MapLocation(1, 1);
    boolean result = navigation.goTo(location);
    Assert.assertEquals(true, result);
  }

  @Ignore
  @Test
  public void testTryFly() throws Exception {
    boolean result = navigation.tryFly(Direction.NORTH);
    Assert.assertEquals(false, result);
  }

  @Ignore
  @Test
  public void testFlyTo() throws Exception {
    boolean result = navigation.flyTo(Direction.NORTH);
    Assert.assertEquals(false, result);
  }

  @Ignore
  @Test(expected = NullPointerException.class)
  public void testFlyTo2() throws Exception {
    MapLocation location = new MapLocation(1, 1);
    boolean result = navigation.flyTo(location);
    Assert.assertEquals(true, result);
  }
}
