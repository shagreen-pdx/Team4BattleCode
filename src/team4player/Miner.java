package team4player;

import battlecode.common.*;

import java.util.ArrayList;

public class Miner extends Unit{

    int numDesignSchools = 0;
    int numFulfillmentCenters = 0;
    int numRefineries = 0;
    int numVaporators = 0;
    ArrayList<MapLocation> soupLocations = new ArrayList<MapLocation>();
    ArrayList<MapLocation> refineryLocations = new ArrayList<MapLocation>();

    public Miner(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException{
        super.takeTurn();

        numDesignSchools += comms.getNewDesignSchoolCount();
        numFulfillmentCenters += comms.getNewFulfillmentCenterCount();
//        numRefineries += comms.getNewRefineryCount();
        comms.updateRefineryLocation(refineryLocations);
        comms.updateSoupLocation(soupLocations);
        checkIfSoupGone();
        checkIfRefineryNearby();

        // Try and refine
        for (Direction dir : Util.directions){
            if (tryRefine(dir))
                System.out.println("I refined soup! " + rc.getTeamSoup());
        }


        // Try and mine soup
        for (Direction dir : Util.directions) {

            if (rc.onTheMap(rc.getLocation().add(dir))) {
                if (refineryLocations.size() == 0 && !nearbyRobot(RobotType.HQ)) { //if there are no refineries and HQ not nearby, build a refinery
                    for (Direction dir2 : Util.directions) {
                        if (tryBuildBuilding(RobotType.REFINERY, dir2)) {
                            System.out.println("Built Refinery");
                            break;
                        }
                    }
                } else if (rc.senseSoup(rc.getLocation().add(dir)) > 0 && !nearbyRobot(RobotType.HQ) && !nearbyRobot(RobotType.REFINERY)) { //otherwise, check if there is soup nearby before building a refinery
                    for (Direction dir2 : Util.directions) {
                        if (tryBuildBuilding(RobotType.REFINERY, dir2)) {
                            System.out.println("Built Refinery");
                            break;
                        }
                    }
                }

                if (tryMine(dir)) {
                    System.out.println("I mined soup! " + rc.getSoupCarrying());
                    MapLocation soupLoc = rc.getLocation().add(dir);
                    if (!soupLocations.contains(soupLoc)) {
                        comms.broadcastMessage(soupLoc, 2);
                    }
                }
            }
        }

        if(numFulfillmentCenters < 1) {
            if (!nearbyRobot(RobotType.FULFILLMENT_CENTER)) {
                Direction dir = Util.randomDirection();
                if (tryBuildBuilding(RobotType.FULFILLMENT_CENTER, dir)) {
                    System.out.println("Built Fulfillment Center");
                }
            }
        }

        System.out.println("num of design school: " + numDesignSchools);
        if (numDesignSchools < 1) {
            Direction dir = Util.randomDirection();
            if (tryBuildBuilding(RobotType.DESIGN_SCHOOL, dir)) {
                System.out.println("Built Design School");
            }
        }
        if(numVaporators < 1){
            Direction dir = Util.randomDirection();
            if (tryBuildBuilding(RobotType.VAPORATOR, dir)) {
                System.out.println("Built Vaporator");
                numVaporators ++;
            }
        }

        if(rc.getSoupCarrying() == rc.getType().soupLimit) {
            System.out.println("At soup carrying limit " + rc.getType().soupLimit);
            //find refinery
            if(refineryLocations.size() != 0) {
                nav.goTo(findNearestRefinery());
                System.out.println("I moved toward a refinery!");
            } else  if (nav.goTo(hqLoc)){
                System.out.println("Moving towards HQ");
            }


        } else if (soupLocations.size() > 0){
            nav.goTo(soupLocations.get(0));
        }else if (nav.goTo(Util.randomDirection()))
            System.out.println("I moved in random direction!");

    }

    void checkIfRefineryNearby() throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots();
        for (RobotInfo robot : robots) {
            if (robot.type == RobotType.REFINERY && robot.team == rc.getTeam() && !refineryLocations.contains(robot.location)) {
                comms.broadcastMessage(robot.location, 3);
                System.out.println("Refinery Nearby");  //FOR TESTING DELETE LATER
            }
        }
    }

    MapLocation findNearestRefinery() throws GameActionException {
        MapLocation myLoc = rc.getLocation();
        MapLocation closestRefinery = hqLoc;
        int distance = myLoc.distanceSquaredTo(closestRefinery);
        for(MapLocation loc : refineryLocations){
            int distance2 = myLoc.distanceSquaredTo(loc);
            if(distance2 < distance){
                distance = distance2;
                closestRefinery = loc;
            }
        }
        if(closestRefinery == hqLoc){
            System.out.println("Nearest Refinery is HQ");
        }
        return closestRefinery;
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
