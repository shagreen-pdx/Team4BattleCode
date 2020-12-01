package team4player;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Map;

public class Miner extends Unit{

    boolean isStuck = false;
    int stuck = 0;
    boolean buildDesignSchool = false;
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

        // Destroy self
        if(stuck > 500){
            rc.disintegrate();
        }

        //allow enough time to pass for miners to no longer be moving randomly
        if(rc.getRoundNum() > 150) {
            //check if the miner is stuck
            trackPreviousLocations(rc.getLocation());
            if (isStuck) {
                System.out.println(nav.prevLocations);
                if (!tryUnstuck()) {
                    System.out.println("Miner cannot get unstuck.");
                    //call a drone to pick you up?
                }
            }
        }

        if(!teamMessagesSearched){
            decipherAllBlockChainMessages();
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

        decipherCurrentBlockChainMessage();

        // Try and refine
        for (Direction dir : Util.directions){
            if (tryRefine(dir)){
                nav.prevLocations.clear();
                System.out.println("I refined soup! " + rc.getTeamSoup());
            }
            MapLocation tileToCheckForFlooding = rc.getLocation().add(dir);
            if(rc.canSenseLocation(tileToCheckForFlooding) && rc.senseFlooding(tileToCheckForFlooding)){
                for(MapLocation loc : floodedLocations){
                    if(!tileToCheckForFlooding.isWithinDistanceSquared(loc ,25)){
                        comms.broadcastMessage(tileToCheckForFlooding,11);
                        floodedLocations.add(tileToCheckForFlooding);
                    }
                }
            }

        }

        checkIfSoupGone();
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

        if (numDesignSchools < 1) {
            Direction dir = Util.randomDirection();
            if (tryBuildBuilding(RobotType.DESIGN_SCHOOL, dir)) {
                System.out.println("Built Design School");
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
        if(numVaporators < 1) {
            Direction dir = Util.randomDirection();
            if (tryBuildBuilding(RobotType.VAPORATOR, dir)) {
                System.out.println("Built Vaporator");
                numVaporators++;
            }
        }

        // If miner reached soup carrying capacity
        if(rc.getSoupCarrying() == rc.getType().soupLimit) {
            System.out.println("At soup carrying limit " + rc.getType().soupLimit);
            // If early in the game head to hq, else head to closest refinery
            if(rc.getRoundNum() < 150 || refineryLocations.size() < 1){
                nav.goTo(hqLoc);
            } else {
                MapLocation closestRefinery = getClosestLoc(refineryLocations);
                if(nav.goTo(closestRefinery)){
                    stuck = 0;
                } else {
                    ++stuck;
                }
            }

        } else if (soupLocations.size() > 0){
            System.out.println("Moving toward soup loc: " + soupLocations.get(0));
            if(nav.goTo(soupLocations.get(0))){
                stuck = 0;
            } else {
                ++stuck;
            }
        }else {
            if (nav.goTo(Util.randomDirection())){
                stuck = 0;
            }else {
                ++stuck;
            }
        }
    }

    void checkIfSoupGone() throws GameActionException {
        if(soupLocations.size() > 0){
            MapLocation targetSoupLoc = soupLocations.get(0);
            if(rc.canSenseLocation(targetSoupLoc) && rc.senseSoup(targetSoupLoc) == 0){
                soupLocations.remove(0);
                comms.broadcastMessage(targetSoupLoc, 13);
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

         // Check if close to refineries
         RobotInfo[] robots = rc.senseNearbyRobots(30, rc.getTeam());

         for(RobotInfo robot : robots){
             if(robot.getType() == RobotType.REFINERY){
                 return false;
             }
         }
//         for (MapLocation refinery : refineryLocations){
//             if(locToBuild.isWithinDistanceSquared(refinery, 75)){
//                 System.out.println("To close to other refinery");
//                 return false;
//             }
//         }

         return true;
    }

    public void decipherAllBlockChainMessages() {
        for(int [] message : teamMessages) {
            // Set Hq Location
            if (message[1] == 0) {
                System.out.println("Got Hq Location");
                hqLoc = new MapLocation(message[2], message[3]);
                System.out.println(hqLoc);
            }
            // Set Enemy Hq Location
            else if (message[1] == 6) {
                System.out.println("Got enemy Hq location");
                enemyHqLoc = new MapLocation(message[2], message[3]);
                System.out.println(enemyHqLoc);
            }
            //Get Num of design schools
            else if (message[1] == 1) {
                ++numDesignSchools;
            }
            // Get num of fulfilment Centers
            else if (message[1] == 4) {
                ++numFulfillmentCenters;

            } else if (message[1] == 3) {
                ++numRefineries;
                refineryLocations.add(new MapLocation(message[2], message[3]));
            }
            // Robot specific messages
            else if (message[1] == 7) {
                System.out.print("Recieved personal message");
            }
        }
        teamMessagesSearched = true;
    }

    public void decipherCurrentBlockChainMessage() throws GameActionException {
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
                System.out.println("New Soup Location");
                soupLocations.add(new MapLocation(message[2], message[3]));
            }
            // Set Enemy Hq Location
            else if (message[1] == 6) {
                System.out.println("Got enemy location");
                enemyHqLoc = new MapLocation(message[2], message[3]);
                System.out.println(enemyHqLoc);
            }
            else if (message[1] == 7 && message[4] == rc.getID()){
                buildDesignSchool = true;
            }
            else if (message[1] == 13){
                MapLocation soupGone = new MapLocation(message[2], message[3]);
                if(soupLocations.contains(soupGone)){
                    soupLocations.remove(soupGone);
                }
            }
        }
    }

    void trackPreviousLocations(MapLocation location){
         double distTravelled = 0;
         //check if the miner has moved 10 times before checking how far it's moved in those last 10 moves
         if(nav.prevLocations.size() > 10) {
             //find furthest distance travelled
             for (MapLocation loc : nav.prevLocations) {
                 double temp = Math.sqrt(Math.pow(loc.x - location.x, 2) + Math.pow(loc.y - location.y, 2));
                 if (temp > distTravelled) {
                     distTravelled = temp;
                 }
             }
             //if distance is less than 2, the miner is stuck
             if (distTravelled < 2) {
                 System.out.println("Miner is stuck.");
                 isStuck = true;
             }
         }
        return;
    }

    boolean tryUnstuck(){
         //if the miner is stuck and is trying to get to a specific location, remove that location so it can go somewhere else
        if (nav.targetDestination != null){
            if (soupLocations.contains(nav.targetDestination)){
                soupLocations.remove(nav.targetDestination);
            }
            if (refineryLocations.contains(nav.targetDestination)){
                refineryLocations.remove(nav.targetDestination);
            }
            return true;
        }
        else{
            return false;
        }
    }
}
