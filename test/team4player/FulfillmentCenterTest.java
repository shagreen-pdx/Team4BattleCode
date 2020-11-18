package team4player;

import battlecode.common.RobotController;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class FulfillmentCenterTest {
  @Mock
  ArrayList<Integer> teamMessages;
  @Mock
  RobotController rc;
  @Mock
  Communications comms;
  @InjectMocks
  FulfillmentCenter fulfillmentCenter;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Ignore
  @Test (expected = NullPointerException.class)
  public void testTakeTurn() throws Exception {
    when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);
    when(comms.broadcastMessage(anyInt(), anyInt())).thenReturn(true);

    fulfillmentCenter.takeTurn();
  }
}