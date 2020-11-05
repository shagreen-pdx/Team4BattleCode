package team4player;

import battlecode.common.RobotController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class RefineryTest {
  @Mock
  ArrayList<Integer> teamMessages;
  @Mock
  RobotController rc;
  @Mock
  Communications comms;
  @InjectMocks
  Refinery refinery;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test (expected = NullPointerException.class)
  public void testTakeTurn() throws Exception {
    when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);

    refinery.takeTurn();
  }
}
