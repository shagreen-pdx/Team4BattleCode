package team4player;

import battlecode.common.RobotController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import scala.xml.Null;

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

  @Test (expected = NullPointerException.class)
  public void testTakeTurn() throws Exception {
    when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int[]>(Arrays.asList(new int[]{0})));

    designSchool.teamMessagesSearched = false;
    designSchool.rampUpProduction = true;
    designSchool.takeTurn();
  }

  @Test(expected = NullPointerException.class)
  public void testTakeTurn2() throws Exception {
    designSchool.teamMessagesSearched = true;
    designSchool.rampUpProduction = false;
    designSchool.takeTurn();
  }

  @Test
  public void testDecipherAllBlockChainMessages() throws Exception {
    ArrayList<int[]> list = new ArrayList<>();
    int temp[]={0,8,0,0,1};
    list.add(temp);
    when(rc.getID()).thenReturn(1);

    designSchool.teamMessages = list;
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
