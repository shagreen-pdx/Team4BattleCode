package team4player;

import battlecode.common.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import static org.mockito.Mockito.*;

public class MinerTest {
    @Mock
    ArrayList<MapLocation> soupLocations;
    @Mock
    ArrayList<MapLocation> refineryLocations;
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
    @Mock
    Unit unit;
    @InjectMocks
    Miner miner;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurn() throws Exception {
        when(nav.goTo((Direction) any())).thenReturn(true);
        when(nav.goTo((Direction) any())).thenReturn(true);
        when(comms.broadcastMessage(any(), anyInt())).thenReturn(true);
        when(comms.getPrevRoundMessages()).thenReturn(new ArrayList<int[]>(Arrays.asList(new int[]{0})));

        miner.takeTurn();
    }

    @Test
    public void testTakeTurn1() throws Exception {
        miner.stuck = 600;
        miner.takeTurn();
    }

    @Test (expected = NullPointerException.class)
    public void testTakeTurn2() throws Exception {
        miner.teamMessagesSearched = true;
        when(rc.getRoundNum()).thenReturn(2);
        miner.rush = true;
        miner.startedAttack = false;
        miner.isAttacking = true;

        miner.takeTurn();
    }

    @Test
    public void testCheckIfSoupGone() throws Exception {
        when(rc.canSenseLocation(any())).thenReturn(true);
        when(rc.senseSoup(any())).thenReturn(0);
        java.util.Random rand = new Random();
        for (int i = 0; i < 4; i++)
        {
            MapLocation loc = new MapLocation(rand.nextInt(), rand.nextInt());
            miner.soupLocations.add(loc);
        }
        miner.checkIfSoupGone();
    }

    @Test
    public void testTryMine() throws Exception {
        when(rc.isReady()).thenReturn(true);
        when(rc.canMineSoup(any())).thenReturn(true);
        boolean result = miner.tryMine(Direction.NORTH);
        Assert.assertEquals(true, result);
    }

    @Test
    public void testTryMine1() throws Exception {
        when(rc.isReady()).thenReturn(false);
        when(rc.canMineSoup(any())).thenReturn(true);
        boolean result = miner.tryMine(Direction.NORTH);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testTryRefine() throws Exception {
        when(rc.isReady()).thenReturn(true);
        when(rc.canDepositSoup(any())).thenReturn(true);
        boolean result = miner.tryRefine(Direction.NORTH);
        Assert.assertEquals(true, result);
    }

    @Test
    public void testTryRefine1() throws Exception {
        when(rc.isReady()).thenReturn(false);
        when(rc.canDepositSoup(any())).thenReturn(true);
        boolean result = miner.tryRefine(Direction.NORTH);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testCanBuildRefinery() throws Exception {
        miner.numDesignSchools = 0;
        boolean result = miner.canBuildRefinery(new MapLocation(1,1));
        Assert.assertEquals(false, result);
    }

    @Test(expected = NullPointerException.class)
    public void testCanBuildRefinery1() throws Exception {
        miner.numDesignSchools = 2;
        miner.numFulfillmentCenters = 2;
        miner.numRefineries = 2;
        when(robot.isNearbyRobot(any(),any(),any())).thenReturn(true);
        boolean result = miner.canBuildRefinery(new MapLocation(1,1));
        Assert.assertEquals(true, result);
    }

    @Test
    public void testTrackPreviousLocations() throws Exception {
        nav.prevLocations = new ArrayList<>();
        for(int i = 0; i < 12; i++){
            MapLocation loc = new MapLocation(i, i);
            nav.prevLocations.add(loc);
        }
        miner.trackPreviousLocations(new MapLocation(1,1));
    }

    @Test
    public void testTryUnstuck() throws Exception {
        nav.targetDestination = null;
        boolean results = miner.tryUnstuck();
        Assert.assertEquals(false, results);
    }

    @Test
    public void testTryUnstuck1() throws Exception {
        MapLocation loc = new MapLocation(1,1);
        nav.targetDestination = loc;
        soupLocations.add(loc);
        refineryLocations.add(loc);
        boolean results = miner.tryUnstuck();
        Assert.assertEquals(false, results);
    }

    @Test(expected = NullPointerException.class)
    public void testBuildInDirection() throws Exception {
        RobotInfo bot = new RobotInfo(1,null,RobotType.DESIGN_SCHOOL,0,false,0,0,0,new MapLocation(0,0));
        when(rc.canBuildRobot(any(), any())).thenReturn(true);
        when(rc.senseRobotAtLocation(any())).thenReturn(bot);
        int result = miner.buildInDirection(RobotType.DESIGN_SCHOOL, Direction.NORTH);
        Assert.assertEquals(1,result);
    }

    @Test
    public void testFindReachableSoupLocations() throws Exception {
        when(rc.getLocation()).thenReturn(new MapLocation(1,1));
        MapLocation temp1 = new MapLocation(1,1);
        MapLocation temp2 = new MapLocation(2,2);
        MapLocation temp3 = new MapLocation(3,3);

        MapLocation loc[] = {temp1,temp2,temp3};
        miner.findReachableSoupLocations(loc);
    }

    @Test
    public void testIsSoupReachable() throws Exception {
        when(rc.getLocation()).thenReturn(new MapLocation(1,1));
        when(rc.canSenseLocation(any())).thenReturn(true);
        when(rc.senseFlooding(any())).thenReturn(false);
        when(rc.senseElevation(any())).thenReturn(1);
        boolean result = miner.isSoupReachable(new MapLocation(2,2));
    }

    @Test
    public void testIsSoupReachable1() throws Exception {
        when(rc.getLocation()).thenReturn(new MapLocation(1,1));
        when(rc.canSenseLocation(any())).thenReturn(false);
        when(rc.senseFlooding(any())).thenReturn(false);
        when(rc.senseElevation(any())).thenReturn(1);
        boolean result = miner.isSoupReachable(new MapLocation(2,2));
    }

    @Test
    public void testIsSoupReachable2() throws Exception {
        when(rc.getLocation()).thenReturn(new MapLocation(1,1));
        when(rc.canSenseLocation(any())).thenReturn(true);
        when(rc.senseFlooding(any())).thenReturn(true);
        when(rc.senseElevation(any())).thenReturn(1);
        boolean result = miner.isSoupReachable(new MapLocation(2,2));
    }

    @Test
    public void testDecipherAllBlockChainMessages() throws Exception {
        ArrayList<int[]> list = new ArrayList<>();
        int temp[]={0,0,0,0,0};
        int temp1[] = {0,6,0,0,0};
        int temp2[] = {0,1,0,0,0};
        int temp3[] = {0,4,0,0,0};
        int temp4[] = {0,3,0,0,0};
        int temp5[] = {0,7,0,0,0};
        list.add(temp);
        list.add(temp1);
        list.add(temp2);
        list.add(temp3);
        list.add(temp4);
        list.add(temp5);

        miner.teamMessages = list;
        miner.decipherAllBlockChainMessages();
    }

    @Test(expected = NullPointerException.class)
    public void testDecipherCurrentBlockChainMessage() throws Exception {
        ArrayList<int[]> list = new ArrayList<>();
        int temp[] = {0,1,0,0,0};
        int temp1[] = {0,4,0,0,0};
        int temp2[] = {0,3,0,0,0};
        int temp3[] = {0,2,0,0,0};
        int temp4[] = {0,6,0,0,0};
        int temp5[] = {0,7,0,0,0};
        int temp6[] = {0,13,0,0,0};
        int temp7[] = {0,14,0,0,0};
        list.add(temp);
        list.add(temp1);
        list.add(temp2);
        list.add(temp3);
        list.add(temp4);
        list.add(temp5);
        list.add(temp6);
        list.add(temp7);
        when(rc.getRoundNum()).thenReturn(0);
        when(comms.getPrevRoundMessages()).thenReturn(list);
        miner.decipherCurrentBlockChainMessage();
    }

    @Test
    public void testCanBuildUnit() throws Exception {
        when(rc.getTeamSoup()).thenReturn(100);
        boolean result = miner.canBuildUnit(RobotType.DESIGN_SCHOOL, 10);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testCanBuildUnit1() throws Exception {
        when(rc.getTeamSoup()).thenReturn(1000);
        boolean result = miner.canBuildUnit(RobotType.DESIGN_SCHOOL, 10);
        Assert.assertEquals(true, result);
    }

    @Test
    public void testAttackEnemyHQ() throws Exception {
        
        miner.attackEnemyHq();
    }

    @Test(expected = NullPointerException.class)
    public void testMineAndBuild() throws Exception {
        miner.mineAndBuild();
    }

    @Test
    public void testIfIsStuck() throws Exception {
        nav.targetDestination = null;
        when(rc.getLocation()).thenReturn(new MapLocation(1,1));
        miner.ifIsStuck();
    }

    @Test(expected = NullPointerException.class)
    public void testGoTowardsSoup() throws Exception {
        when(unit.getClosestLoc(any())).thenReturn(new MapLocation(1,1));
        when(nav.goTo((MapLocation) any())).thenReturn(true);
        miner.goTowardSoup();
    }

    @Test
    public void testAttemptBuildDesignSchool() throws Exception {
        miner.hqLoc = new MapLocation(0,0);
        when(unit.tryBuildBuilding(any(),any())).thenReturn(true);
        when(rc.getLocation()).thenReturn(new MapLocation(1,1));
        miner.attemptBuildDesignSchool();
    }

    @Test
    public void testAttemptBuildRefinery() throws Exception {
        miner.hqLoc = new MapLocation(0,0);
        when(unit.tryBuildBuilding(any(),any())).thenReturn(true);
        when(rc.getLocation()).thenReturn(new MapLocation(1,1));
        miner.attemptBuildRefinery();
    }

    @Test
    public void testGoTowardsRefinery() throws Exception {
        miner.hqLoc = new MapLocation(0,0);
        when(rc.getRoundNum()).thenReturn(2);
        when(nav.goTo((MapLocation) any())).thenReturn(true);
        when(rc.getType()).thenReturn(RobotType.MINER);
        when(rc.getLocation()).thenReturn(new MapLocation(1,1));
        miner.goTowardsRefinery();
    }

}

