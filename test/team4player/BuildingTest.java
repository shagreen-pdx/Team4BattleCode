package team4player;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class BuildingTest {
  @Mock
  ArrayList<Integer> teamMessages;
  @Mock
  RobotController rc;
  @Mock
  Communications comms;
  @InjectMocks
  Building building;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testTakeTurn() throws Exception {
    building.takeTurn();
  }
}

