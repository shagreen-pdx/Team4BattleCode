package team4player;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Map;

public class Miner extends Unit{

    int stuck = 0;
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

        // Destroy self
        if(stuck > 500){
            rc.disintegrate();
        }

        if(hqLoc == null){
            System.out.println("HQ LOC is null");
            decipherAllBlockChainMessages();
            System.out.println("Read messages");
        }

        if(buildDesignSchool){
            if(!nearbyRobot(RobotType.DESIGN_SCHOOL, rc.getTeam())){
                for(Direction dir : Util.directions){

                    if(rc.getTeamSoup() > 160 && tryBuildBuilding(RobotType.DESIGN_SCHOOL, dir)){
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


//        if(turnCount % 5==0){
//            MapLocation[] sensedSoup = rc.senseNearbySoup();
//            System.out.println(soupLocations);
//            for(MapLocation location : sensedSoup){
//                System.out.println(location);
//                if(!soupLocations.contains(location))
//                    comms.broadcastMessage(location, 2);
//            }
//
//        }
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

        // If miner reached soup carrying capacity
        if(rc.getSoupCarrying() == rc.getType().soupLimit) {
            System.out.println("At soup carrying limit " + rc.getType().soupLimit);
            // If early in the game head to hq, else head to closest refinery
            if(rc.getRoundNum() < 150){
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
            System.out.println(message);
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
        }
    }
}
