package team4player;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Map;

public class Miner extends Unit{

    boolean buildDesignSchool = false;
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

        if(!teamMessagesSearched){
            for(int [] message : teamMessages){
                // Set Hq Location
                if(message[1] == 0){
                    System.out.println("Got Hq Location");
                    hqLoc = new MapLocation(message[2], message[3]);
                    System.out.println(hqLoc);
                }
                // Set Enemy Hq Location
                else if(message[1] == 6){
                    System.out.println("Got enemy location");
                    enemyHqLoc = new MapLocation(message[2], message[3]);
                    System.out.println(enemyHqLoc);
                }
                //Get Num of design schools
                else if(message[1] == 1){
                    ++numDesignSchools;
                }
                // Get num of fulfilment Centers
                else if(message[1] == 4){
                    ++numFulfillmentCenters;

                }
                else if(message[1] == 3){
                    ++numRefineries;
                    refineryLocations.add(new MapLocation(message[2],message[3]));
                }
                // Robot specific messages
                else if(message[1] == 7){
                    System.out.print("Recieved personal message");
                }
            }
            System.out.println("Num of design schools = " + numDesignSchools);
            System.out.println("Num of fulfilment centers = " + numFulfillmentCenters);
            System.out.println("Num of Refineries = " + numRefineries);
            teamMessagesSearched = true;
        }

        if(buildDesignSchool){
            if(!nearbyRobot(RobotType.DESIGN_SCHOOL, rc.getTeam())){
                for(Direction dir : Util.directions){

                    if(tryBuildBuilding(RobotType.DESIGN_SCHOOL, dir)){
                        buildDesignSchool = false;
                        RobotInfo designSchool = rc.senseRobotAtLocation(rc.getLocation().add(dir));
                        comms.broadcastMessage(designSchool.ID, 8);
                    }

                }
            }
        }

        ArrayList<int []> currentRoundMessages = comms.getPrevRoundMessages();
        for(int [] message : currentRoundMessages){
            if (message[1] == 1) {
                ++numDesignSchools;
            }
            else if (message[1] == 4) {
                ++numFulfillmentCenters;
            }
            else if (message[1] == 3) {
                ++numRefineries;
                refineryLocations.add(new MapLocation(message[2], message[3]));
            }
            else if (message[1] == 2) {
                soupLocations.add(new MapLocation(message[2], message[3]));
            }
            else if (message[1] == 7 && message[4] == rc.getID()){
                buildDesignSchool = true;
            }
        }
//        numDesignSchools += comms.getNewDesignSchoolCount();
//        numFulfillmentCenters += comms.getNewFulfillmentCenterCount();
//        numRefineries += comms.getNewRefineryCount();
//        comms.updateRefineryLocation(refineryLocations);
//        comms.updateSoupLocation(soupLocations);


        // Try and refine
        for (Direction dir : Util.directions){
            if (tryRefine(dir))
                System.out.println("I refined soup! " + rc.getTeamSoup());
        }

        checkIfSoupGone();

        // Try and mine soup
        for (Direction dir : Util.directions){
            if(rc.onTheMap(rc.getLocation().add(dir)) && rc.senseSoup(rc.getLocation().add(dir)) > 0){

                if(canBuildRefinery(rc.getLocation())){
                    for(Direction dir2 : Util.directions){
                        if (tryBuildBuilding(RobotType.REFINERY, dir2)) {
                            System.out.println("Built Refinery");
                            break;
                        }
                    }
                }

                if (tryMine(dir)){
                    System.out.println("I mined soup! " + rc.getSoupCarrying());
                    MapLocation soupLoc = rc.getLocation().add(dir);
                    if(!soupLocations.contains(soupLoc)){
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

        System.out.println("num of refineries: " + numRefineries);
        System.out.println("num of design school: " + numDesignSchools);
        if (numDesignSchools < 1) {
            Direction dir = Util.randomDirection();
            if (tryBuildBuilding(RobotType.DESIGN_SCHOOL, dir)) {
                System.out.println("Built Design School");
            }
        }

        if(rc.getSoupCarrying() == rc.getType().soupLimit) {
            System.out.println("At soup carrying limit " + rc.getType().soupLimit);
            //find refinery
            MapLocation closestRefinery = findClosestRefinery(refineryLocations);
            nav.goTo(closestRefinery);
        } else if (soupLocations.size() > 0){
            nav.goTo(soupLocations.get(0));
        }else if (nav.goTo(Util.randomDirection()))
            System.out.println("I moved in random direction!");

    }

    public MapLocation findClosestRefinery(ArrayList<MapLocation> refineryLocations){
        MapLocation currentLoc = rc.getLocation();
        int closestDistance = currentLoc.distanceSquaredTo(hqLoc);
        MapLocation closestRefinery = hqLoc;

        for(MapLocation refinery : refineryLocations){
            int distanceToRefinery = currentLoc.distanceSquaredTo(refinery);
            if( distanceToRefinery < closestDistance){
                closestDistance = distanceToRefinery;
                closestRefinery = refinery;
            }
        }
        return closestRefinery;
    }

//    void checkIfRefineryNearby() throws GameActionException {
//        RobotInfo[] robots = rc.senseNearbyRobots();
//        for (RobotInfo robot : robots) {
//            if (robot.type == RobotType.REFINERY && robot.team == rc.getTeam() && !refineryLocations.contains(robot.location)) {
//                comms.broadcastMessage(robot.location, 3);
//                System.out.println("Refinery Nearby");  //FOR TESTING DELETE LATER
//            }
//        }
//    }


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

    boolean canBuildRefinery(MapLocation locToBuild) throws GameActionException {

         if(numDesignSchools < 1){
             return false;
         }

         if(numFulfillmentCenters < 1){
             return false;
         }

         if(numRefineries > 3){
             return false;
         }

         // Check if close to hq
         if(locToBuild.isWithinDistanceSquared(hqLoc, 100)){
             System.out.println("To close to other refinery");
             return false;
         }
         System.out.println("Distance to HQ = " + locToBuild.distanceSquaredTo(hqLoc));

         // Check if close to refineries
         for (MapLocation refinery : refineryLocations){
             if(locToBuild.isWithinDistanceSquared(refinery, 75)){
                 System.out.println("To close to other refinery");
                 return false;
             }
         }

         return true;
    }
}
