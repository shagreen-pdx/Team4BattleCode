package team4player;

import battlecode.common.*;

import java.util.ArrayList;

public class Miner extends Unit{

    // Building
    boolean canBuild = false;

    // Rush Variables
    int builtDesignSchool = 0;
    int builtNetGun = 0;
    boolean rushFailed = false;
    boolean broadcastedEnemyLoc = false;


    boolean isStuck = false;
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

        if(!soupLocations.isEmpty()){
            System.out.println(soupLocations);
        }

        // Destroy self
        if(stuck > 500){
            rc.disintegrate();
        }

        // If round 1, then you are the first miner created.
        if (rc.getRoundNum() == 2){
            System.out.println("RUSH");
            rush = true;
        }

        // Rush Hq if close
        if(rush){
            if(!teamMessagesSearched){
                decipherAllBlockChainMessages();
            }

            // Calculate possible Enemy Locations
            if(posEnemyHqLoc.isEmpty()){
                calcPosEnemyHqLoc();
                enemyHqLoc = comms.getEnemyHqLocFromBlockChain(posEnemyHqLoc);
            }

            RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());

            // If close to hq, build design school. If delivery drones nearby, build net gun
            if(findRobot(robots,RobotType.HQ)){
                System.out.println("FOUND ENEMY HQ");

                if(!broadcastedEnemyLoc){
                    for(RobotInfo robot : robots){
                        if(robot.getType().equals(RobotType.HQ)){
                            enemyHqLoc = robot.location;
                            broadcastedEnemyLoc = comms.broadcastMessage(6, enemyHqLoc, 3);
                        }
                    }
                }

                if(builtDesignSchool == 0){

                    if(rc.getLocation().isWithinDistanceSquared(enemyHqLoc,8)){
                        if(rc.getTeamSoup() >= 152){
                            builtDesignSchool = buildInDirection(RobotType.DESIGN_SCHOOL, rc.getLocation().directionTo(enemyHqLoc));

                            if(builtDesignSchool != 0){
                                comms.broadcastMessage( 8, builtDesignSchool, 2);
                            }
                        }
                    } else {
                        nav.goTo(enemyHqLoc);
                    }
                }
                else {
                    if(findRobot(robots,RobotType.DELIVERY_DRONE)){
                        if(builtNetGun == 0){
                            builtNetGun = buildInDirection(RobotType.NET_GUN, Direction.CENTER);
                        }
                    }
                }

            } else {
                if(enemyHqLoc != null){
                    nav.goTo(enemyHqLoc);
                } else {
                    nav.goTo(posEnemyHqLoc.get(1));
                }
            }
        }

        else {
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
                if(rc.getTeamSoup() >= 152){
                    if(!nearbyRobot(RobotType.DESIGN_SCHOOL, rc.getTeam())){
                        for(Direction dir : Util.directions){

                            if(tryBuildBuilding(RobotType.DESIGN_SCHOOL, dir)){
                                buildDesignSchool = false;
                                RobotInfo designSchool = rc.senseRobotAtLocation(rc.getLocation().add(dir));
                                comms.broadcastMessage( 8, designSchool.ID, 2);
                            }
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
//                MapLocation tileToCheckForFlooding = rc.getLocation().add(dir);
//                if(rc.canSenseLocation(tileToCheckForFlooding) && rc.senseFlooding(tileToCheckForFlooding)){
//                    for(MapLocation loc : floodedLocations){
//                        if(!tileToCheckForFlooding.isWithinDistanceSquared(loc ,25)){
//                            comms.broadcastMessage(11, tileToCheckForFlooding, 1);
//                            floodedLocations.add(tileToCheckForFlooding);
//                        }
//                    }
//                }
            }

            checkIfSoupGone();


            MapLocation [] possibleSoupLocations = rc.senseNearbySoup();

            for(MapLocation loc : possibleSoupLocations){

                if(!soupLocations.contains(loc)){
                    if(isSoupReachable(loc)){
                        comms.broadcastMessage(2, loc, 1);
                    }
                }
            }

            for (Direction dir : Util.miningDirections){
                if(rc.onTheMap(rc.getLocation().add(dir)) && rc.senseSoup(rc.getLocation().add(dir)) > 0){

                    if(canBuildRefinery(rc.getLocation())){
                        for(Direction dir2 : Util.directions){
                            if (tryBuildBuilding(RobotType.REFINERY, dir2)) {
                                break;
                            }
                        }
                    }

                    if (tryMine(dir)){
                        System.out.println("I mined soup! " + rc.getSoupCarrying());
                    }
                    MapLocation soupLoc = rc.getLocation().add(dir);
                    if(!soupLocations.contains(soupLoc)){
                        comms.broadcastMessage(2, soupLoc, 1);
                    }
                }
            }

            System.out.println("NUM DESIGN: " + numDesignSchools);
            if (numDesignSchools < 1 && canBuild) {
                Direction dir = Util.randomDirection();
                if (tryBuildBuilding(RobotType.DESIGN_SCHOOL, dir)) {
                    System.out.println("Built Design School");
                    comms.broadcastMessage(1, 1);
                }
            }

            if(numFulfillmentCenters < 1 && canBuild) {
                if (!nearbyRobot(RobotType.FULFILLMENT_CENTER)) {
                    Direction dir = Util.randomDirection();
                    if (tryBuildBuilding(RobotType.FULFILLMENT_CENTER, dir)) {
                        System.out.println("Built Fulfillment Center");
                        comms.broadcastMessage(4, 1);
                    }
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
                MapLocation closestSoup = getClosestLoc(soupLocations);

                System.out.println("Moving toward soup loc: " + closestSoup);
                if(nav.goTo(closestSoup)){
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
    }

    void checkIfSoupGone() throws GameActionException {
        System.out.println("CHECK");
        for(MapLocation loc : soupLocations){
            if(rc.canSenseLocation(loc) && rc.senseSoup(loc) == 0){
                System.out.println("Loc is empty");
                soupLocations.remove(loc);
                //comms.broadcastMessage(13,loc,1);
                return;
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
            System.out.println(message[1]);
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
                MapLocation newSoup = new MapLocation(message[2], message[3]);
                if(!soupLocations.contains(newSoup)){
                    soupLocations.add(new MapLocation(message[2], message[3]));
                }
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
                soupLocations.remove(soupGone);
            }
            else if (message[1] == 14){
                canBuild = true;
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

    // Returns id of build robot
    int buildInAnyDirection(RobotType robotType) throws GameActionException {
         for(Direction dir : Util.directions){
             if(rc.canBuildRobot(robotType,dir)){
                 rc.buildRobot(robotType,dir);
                 RobotInfo builtRobot = rc.senseRobotAtLocation(rc.getLocation().add(dir));
                 return builtRobot.getID();
             }
         }
         return 0;
    }

    int buildInDirection(RobotType robotType, Direction dir) throws GameActionException {
        Direction[] toTry = {dir, dir.rotateLeft(), dir.rotateRight(),dir.rotateLeft().rotateLeft(), dir.rotateRight().rotateRight(), dir.opposite().rotateLeft(), dir.opposite().rotateRight(),  dir.opposite() };
        for(Direction d : toTry){
            if(rc.canBuildRobot(robotType,d)){
                rc.buildRobot(robotType,d);
                RobotInfo builtRobot = rc.senseRobotAtLocation(rc.getLocation().add(d));
                return builtRobot.getID();
            }
        }
        return 0;
    }

    public boolean isSoupReachable(MapLocation soupLoc) throws GameActionException {
         MapLocation curLoc = rc.getLocation();
         System.out.println("TESTING FOUND SOUP LOC: " + soupLoc);
         while(true){
             if(curLoc.equals(soupLoc)){
                 return true;
             } else {
                 MapLocation nextLocTo = curLoc.add(curLoc.directionTo(soupLoc));
                 System.out.println(curLoc);
                 System.out.println(nextLocTo);
                 if(!isSoupReachable(curLoc, nextLocTo)){
                    return false;
                 }
                 curLoc = nextLocTo;
             }
         }
    }

    public boolean isSoupReachable(MapLocation locFrom, MapLocation locTo) throws GameActionException {
         int elevFrom = rc.senseElevation(locFrom);
         if(!rc.canSenseLocation(locTo)){
             return false;
         }
         int elevTo = rc.senseElevation(locTo);
         int elevDifference = elevFrom - elevTo;

         if(rc.senseFlooding(locTo)){
             return false;
         }

         if(elevDifference > 3 || elevDifference < -3){
             return false;
         }

         return true;
    }
}
