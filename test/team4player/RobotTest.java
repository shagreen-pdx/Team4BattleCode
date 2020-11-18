package team4player;

import battlecode.common.Direction;
import battlecode.common.RobotController;
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

import static org.mockito.Mockito.*;

public class RobotTest {
  @Mock
  ArrayList<Integer> teamMessages;
  @Mock
  RobotController rc;
  @Mock
  Communications comms;
  @InjectMocks
  Robot robot;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Ignore
  @Test
  public void testTakeTurn() throws Exception {
    robot.takeTurn();
  }

  @Test
  public void testTryBuild() throws Exception {
    boolean result = robot.tryBuild(RobotType.HQ, Direction.NORTH);
    Assert.assertEquals(false, result);
  }

  @Test (expected = NullPointerException.class)
  public void testNearbyRobot() throws Exception {
    boolean result = robot.nearbyRobot(RobotType.HQ);
    Assert.assertEquals(true, result);
  }

  @Test(expected = NullPointerException.class)
  public void testNearbyRobot2() throws Exception {
    boolean result = robot.nearbyRobot(RobotType.HQ, Team.A);
    String expectedMessage = "java.lang.NullPointerException";
    Assert.assertEquals(expectedMessage, result);
  }
}