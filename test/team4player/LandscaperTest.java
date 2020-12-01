package team4player;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.mockito.Mockito.*;

public class LandscaperTest {
    @Mock
    ArrayList<MapLocation> enemyBuildings;
    @Mock
    Navigation nav;
    @Mock
    ArrayList<MapLocation> floodedLocations;
    @Mock
    ArrayList<MapLocation> posEnemyHqLoc;
    @Mock
    ArrayList<int[]> teamMessages;
    @Mock
    RobotController rc;
    @Mock
    Communications comms;
    @Mock
    Robot robot;
    @InjectMocks
    Landscaper landscaper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurn() throws Exception {
        when(nav.tryMoveForward(any())).thenReturn(true);
        when(nav.goTo((Direction) any())).thenReturn(true);
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int[]>(Arrays.asList(new int[]{0})));

        landscaper.takeTurn();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurn1() throws Exception {
        robot.teamMessagesSearched = false;
        landscaper.job = false;
        when(nav.tryMoveForward(any())).thenReturn(true);
        when(nav.goTo((Direction) any())).thenReturn(true);
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int[]>(Arrays.asList(new int[]{0})));

        landscaper.takeTurn();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurn2() throws Exception {
        robot.teamMessagesSearched = true;
        landscaper.job = true;
        landscaper.rush = true;
        when(nav.tryMoveForward(any())).thenReturn(true);
        when(nav.goTo((Direction) any())).thenReturn(true);
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int[]>(Arrays.asList(new int[]{0})));

        landscaper.takeTurn();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurn3() throws Exception {
        robot.teamMessagesSearched = true;
        landscaper.job = true;
        landscaper.rush = false;
        when(nav.tryMoveForward(any())).thenReturn(true);
        when(nav.goTo((Direction) any())).thenReturn(true);
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int[]>(Arrays.asList(new int[]{0})));

        landscaper.takeTurn();
    }

    @Test
    public void testTakeTurnJob() throws Exception {
        robot.enemyHqLoc = null;

        landscaper.takeTurnJob();
    }

    @Test
    public void testTakeTurnJob1() throws Exception {
        Random rand = new Random();
        MapLocation loc = new MapLocation(1,1);
        robot.enemyHqLoc = loc;
        when(rc.getLocation()).thenReturn(loc);

        landscaper.takeTurnJob();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurnRush() throws Exception {
        robot.enemyHqLoc = new MapLocation(0,0);
        when(rc.getLocation()).thenReturn(new MapLocation(1,1));

        when(rc.getDirtCarrying()).thenReturn(0);
        when(rc.canDigDirt(any())).thenReturn(true);
        when(nav.tryMoveForward(any())).thenReturn(true);
        when(nav.goTo((Direction) any())).thenReturn(true);

        landscaper.takeTurnRush();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurnRest() throws Exception {
        when(nav.goTo((Direction) any())).thenReturn(true);

        landscaper.takeTurnRest();
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurnAtEnemyHQ() throws Exception {
        when(nav.goTo((Direction) any())).thenReturn(true);

        landscaper.takeTurnAtEnemyHQ();
    }

    @Test
    public void testDigToDir() throws Exception {
        Random random = new Random();
        MapLocation loc = new MapLocation(1,1);
        when(rc.getLocation()).thenReturn(loc);
        when(rc.senseElevation(any())).thenReturn(random.nextInt());
        when(rc.getDirtCarrying()).thenReturn(0);

        landscaper.digToDir(Direction.NORTH);
    }

    @Test
    public void testDigDown() throws Exception {
        when(rc.getDirtCarrying()).thenReturn(0);
        landscaper.digDown(Direction.NORTH);
    }

    @Test
    public void testDigDown1() throws Exception {
        when(rc.getDirtCarrying()).thenReturn(1);
        landscaper.digDown(Direction.NORTH);
    }


    @Test
    public void testDigUp() throws Exception {
        when(rc.getDirtCarrying()).thenReturn(0);
        landscaper.digUp(Direction.NORTH);
    }

    @Test
    public void testDigUp1() throws Exception {
        when(rc.getDirtCarrying()).thenReturn(1);
        landscaper.digUp(Direction.NORTH);
    }

    @Test
    public void testDecipherAllBlockChainMessages() throws Exception {
        ArrayList<int[]> list = new ArrayList<>();
        int temp[]={0,0,0,0,0};
        int temp1[] = {0,6,0,0,0};
        int temp2[] = {0,7,0,0,0};
        list.add(temp);
        list.add(temp1);
        list.add(temp2);

        landscaper.teamMessages = list;

        landscaper.decipherAllBlockChainMessages();
    }

    @Test(expected = NullPointerException.class)
    public void testDecipherCurrentBlockChainMessage() throws Exception {
        ArrayList<int[]> list = new ArrayList<>();
        int temp[]={0,10,0,0,0};
        int temp1[] = {0,6,0,0,0};
        list.add(temp);
        list.add(temp1);
        when(comms.getPrevRoundMessages()).thenReturn(list);
        when(rc.getRoundNum()).thenReturn(1);

        landscaper.decipherCurrentBlockChainMessage();
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme