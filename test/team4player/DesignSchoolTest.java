package team4player;

import battlecode.common.RobotController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

public class DesignSchoolTest {
  @Mock
  ArrayList<Integer> teamMessages;
  @Mock
  RobotController rc;
  @Mock
  Communications comms;
  @Mock
  Robot robot;
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

  @Test(expected = NullPointerException.class)
  public void testDecipherCurrentBlockChainMessages() throws Exception {
    when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int[]>(Arrays.asList(new int[]{0})));

    designSchool.decipherCurrentBlockChainMessage();
  }

  @Test
  public void testRampingUpProduction() throws Exception {
    when(robot.tryBuild(any(), any())).thenReturn(true);
    when(rc.getDirtCarrying()).thenReturn(14);

    designSchool.rampingUpProduction();
  }

  @Test
  public void testCreatingNewLandscaper() throws Exception {
    designSchool.numLandscapers = 1;
    designSchool.canBuild = true;
    when(rc.getTeamSoup()).thenReturn(211);
    when(rc.isReady()).thenReturn(true);
    when(robot.tryBuild(any(), any())).thenReturn(true);
    when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);

    designSchool.creatingNewLandscaper();

    Assert.assertEquals(1, designSchool.numLandscapers);
    Assert.assertEquals(true,designSchool.canBuild);
  }

}
