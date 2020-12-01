package team4player;

import battlecode.common.RobotController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

public class DesignSchoolTestOld {
  @Mock
  ArrayList<Integer> teamMessages;
  @Mock
  RobotController rc;
  @Mock
  Communications comms;
  @InjectMocks
  DesignSchool designSchool;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }


  @Test
  public void testDecipherAllBlockChainMessages() throws Exception {
    designSchool.decipherAllBlockChainMessages();
  }

}
