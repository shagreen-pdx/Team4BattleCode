package team4player;

import battlecode.common.*;

import java.util.ArrayList;

public class Miner extends Unit{

    int numDesignSchools = 0;
    int numFulfillmentCenters = 0;
    int numRefineries = 0;
    ArrayList<MapLocation> soupLocations = new ArrayList<MapLocation>();
    ArrayList<MapLocation> refineryLocations = new ArrayList<MapLocation>();

    public Miner(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException{
        super.takeTurn();

        numDesignSchools += comms.getNewDesignSchoolCount();
        numDesignSchools += comms.getNewFulfillmentCenterCount();
        numRefineries += comms.getNewRefineryCount();
        comms.updateRefineryLocation(refineryLocations);
        comms.updateSoupLocation(soupLocations);
        checkIfSoupGone();

//        tryBlockchain();
//        tryBuild(randomSpawnedByMiner(), randomDirection());
//        for (Direction dir : directions)
//            tryBuild(RobotType.FULFILLMENT_CENTER, dir);
        for (Direction dir : Util.directions)
            if (tryRefine(dir))
                System.out.println("I refined soup! " + rc.getTeamSoup());
        for (Direction dir : Util.directions){
            if (tryMine(dir)){
                System.out.println("I mined soup! " + rc.getSoupCarrying());
                MapLocation soupLoc = rc.getLocation().add(dir);
                if(!soupLocations.contains(soupLoc)){
                    comms.broadcastSoupLocation(soupLoc);
                }
            }


        }

        if(numFulfillmentCenters < 2) {
            if (!nearbyRobot(RobotType.FULFILLMENT_CENTER)) {
                if (tryBuild(RobotType.FULFILLMENT_CENTER, Util.randomDirection())) {
                    System.out.println("Built Fulfillment Center");
                }
            }
        }

        System.out.println("num of design school: " + numDesignSchools);
        if (numDesignSchools < 3) {
            if (tryBuild(RobotType.DESIGN_SCHOOL, Util.randomDirection())) {
                System.out.println("Built Design School");
            }
        }

        //try building refinery
        if (!nearbyRobot(RobotType.REFINERY)) {
            if (tryBuild(RobotType.REFINERY, Util.randomDirection()))
                System.out.println("Built Refinery");
        }

        if(rc.getSoupCarrying() == rc.getType().soupLimit) {
            System.out.println("At soup carrying limit " + rc.getType().soupLimit);
            if (nav.goTo(hqLoc)) {
                System.out.println("Moving towards HQ");
            }
        } else if (soupLocations.size() > 0){
            nav.goTo(soupLocations.get(0));
        }else if (nav.goTo(Util.randomDirection()))
            System.out.println("I moved in random direction!");

    }

    void checkIfSoupGone() throws GameActionException {
        if(soupLocations.size() > 0){
            MapLocation targetSoupLoc = soupLocations.get(0);
            if(rc.canSenseLocation(targetSoupLoc) && rc.senseSoup(targetSoupLoc) == 0){
                soupLocations.remove(0);
            }
        }
    }

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
     boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to refine soup in a given direction.
     *
     * @param dir The intended direction of refining
     * @return true if a move was performed
     * @throws GameActionException
     */
     boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }
}
