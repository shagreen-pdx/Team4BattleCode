package team4player;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Iterator;

public class Miner extends Unit {

    boolean rush = true;

    // Building
    boolean canBuild = false;

    // Attack Variables
    boolean isAttacking = false;
    boolean startedAttack = false;
    int builtDesignSchool = 0;
    int builtNetGun = 0;

    boolean broadcastedEnemyLoc = false;
    RobotInfo[] robotsSensedDuringTurn = null;


    boolean isStuck = false;
    int stuck = 0;
    boolean buildDesignSchool = false;
    int numDesignSchools = 0;
    int numFulfillmentCenters = 0;
    int numRefineries = 0;
    int numVaporators = 0;
    ArrayList<MapLocation> soupLocations = new ArrayList<MapLocation>();
    ArrayList<MapLocation> refineryLocations = new ArrayList<MapLocation>();

    public Miner(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        // Destroy self
        if (stuck > 500) {
            rc.disintegrate();
            return;
        }

        // Search team messages
        if (!teamMessagesSearched) {
            decipherAllBlockChainMessages();
        }

        // If round 1, then you are the first miner created.
        if (rc.getRoundNum() == 2) {
            System.out.println("ATTACK HQ");
            isAttacking = true;
        }

//        if (rc.getRoundNum() > 125 && enemyHqLoc == null) {
//            canBuild = true;
//        }

        // Check if attack failed or not
        if(rush){
            if (!startedAttack){
                System.out.println("ATTACK NOT STARTED");
                // If attack has not started by round, call off attack
                if(rc.getRoundNum() >= roundNumberToCallOffRushIfAttackNotStarted){
                    System.out.println("ATTACK FAILED");
                    rush = false;
                    isAttacking = false;
                }
            } else {
                System.out.println("STARTED ATTACK");
            }
        }

        // Attack or defend
        if (isAttacking) {
            attackEnemyHq();
        } else {
            mineAndBuild();
        }
    }

    // Check the list of soup locations for soup. Removes if soup is not found
    void checkIfSoupGone() throws GameActionException {
        Iterator<MapLocation> iter = soupLocations.iterator();

        while(iter.hasNext()){
            MapLocation loc = iter.next();
            if (rc.canSenseLocation(loc) && rc.senseSoup(loc) == 0) {
                iter.remove();
            }
        }
    }


    // Attempts to mine soup in a given direction.
    boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        } else return false;
    }


     // Attempts to refine soup in a given direction.
    boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }


    // Checks if miner can build refinery. If it can sense another refinery return false
    boolean canBuildRefinery(MapLocation locToBuild) throws GameActionException {

        if (numDesignSchools < 1 || numFulfillmentCenters < 1 || numRefineries > 3) {
            return false;
        }

        // Check if close to refineries
        robotsSensedDuringTurn = rc.senseNearbyRobots(-1, rc.getTeam());

        // Check if refinery is near
        if(isNearbyRobot(robotsSensedDuringTurn, RobotType.REFINERY, rc.getTeam())){
            return false;
        }

        return true;
    }

    void trackPreviousLocations(MapLocation location) {
        double distTravelled = 0;
        //check if the miner has moved 10 times before checking how far it's moved in those last 10 moves
        if (nav.prevLocations.size() > 10) {
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


    boolean tryUnstuck() {
        //if the miner is stuck and is trying to get to a specific location, remove that location so it can go somewhere else
        if (nav.targetDestination != null) {
            if (soupLocations.contains(nav.targetDestination)) {
                soupLocations.remove(nav.targetDestination);
            }
            if (refineryLocations.contains(nav.targetDestination)) {
                refineryLocations.remove(nav.targetDestination);
            }
            return true;
        } else {
            return false;
        }
    }


    public int buildInDirection(RobotType robotType, Direction dir) throws GameActionException {
        Direction[] toTry = {dir, dir.rotateLeft(), dir.rotateRight(), dir.rotateLeft().rotateLeft(), dir.rotateRight().rotateRight(), dir.opposite().rotateLeft(), dir.opposite().rotateRight(), dir.opposite()};
        for (Direction d : toTry) {
            if (rc.canBuildRobot(robotType, d)) {
                rc.buildRobot(robotType, d);
                RobotInfo builtRobot = rc.senseRobotAtLocation(rc.getLocation().add(d));
                return builtRobot.getID();
            }
        }
        return 0;
    }

    // Broadcasts reachable soup locations
    public void findReachableSoupLocations(MapLocation[] possibleSoupLocations) throws GameActionException {
        boolean firstSoupLocationBroadcasted = false;
        System.out.println("Checking soup locations");
        System.out.println(Clock.getBytecodesLeft());

        for(int i = 0; i < possibleSoupLocations.length; ++i){
            System.out.println(possibleSoupLocations[i]);
            System.out.println(Clock.getBytecodesLeft());
            if (!soupLocations.contains(possibleSoupLocations[i])) {
                System.out.println("New location");
                System.out.println(Clock.getBytecodesLeft());
                if (isSoupReachable(possibleSoupLocations[i])) {
                    System.out.println("Soup is reachable");
                    System.out.println(Clock.getBytecodesLeft());
                    soupLocations.add(possibleSoupLocations[i]);
//                    if(!firstSoupLocationBroadcasted){
//                        comms.broadcastMessage(2, possibleSoupLocations[i], 1);
//                        firstSoupLocationBroadcasted = true;
//                    }
                }
            }
        }

        System.out.println("End search");
        System.out.println(Clock.getBytecodesLeft());
    }


    public boolean isSoupReachable(MapLocation soupLoc) throws GameActionException {
        MapLocation curLoc = rc.getLocation();
        System.out.println("TESTING FOUND SOUP LOC: " + soupLoc);
        while (true) {
            if (curLoc.equals(soupLoc)) {
                return true;
            } else {
                MapLocation nextLocTo = curLoc.add(curLoc.directionTo(soupLoc));
                System.out.println(curLoc);
                System.out.println(nextLocTo);
                if (!isSoupReachable(curLoc, nextLocTo)) {
                    return false;
                }
                curLoc = nextLocTo;
            }
        }
    }


    public boolean isSoupReachable(MapLocation locFrom, MapLocation locTo) throws GameActionException {
        int elevFrom = rc.senseElevation(locFrom);
        if (!rc.canSenseLocation(locTo)) {
            return false;
        }
        int elevTo = rc.senseElevation(locTo);
        int elevDifference = elevFrom - elevTo;

        if (rc.senseFlooding(locTo)) {
            return false;
        }

        if (elevDifference > 3 || elevDifference < -3) {
            return false;
        }

        return true;
    }


    // Rush HQ
    public void attackEnemyHq() throws GameActionException {

        // Calculate possible Enemy Locations
        if (posEnemyHqLoc.isEmpty()) {
            calcPosEnemyHqLoc();
            MapLocation enemyHqLocFromBlockChain = comms.getEnemyHqLocFromBlockChain(posEnemyHqLoc);
            posEnemyHqLoc.add(0,enemyHqLocFromBlockChain);
        }

        // Sense enemy robots
        robotsSensedDuringTurn = rc.senseNearbyRobots(-1, rc.getTeam().opponent());

        // If enemy hq location is confirmed
        if (enemyHqLoc != null){

            if(!broadcastedEnemyLoc){
                broadcastedEnemyLoc = comms.broadcastMessage(6, enemyHqLoc, 3);
            }

            buildBuildingsForRush();
        } else {
            // If arrived at enemy location
            if (rc.canSenseLocation(posEnemyHqLoc.get(0))) {
                RobotInfo robotAtPosEnemyHQLoc = rc.senseRobotAtLocation(posEnemyHqLoc.get(0));
                if(robotAtPosEnemyHQLoc.type == RobotType.HQ){
                    System.out.println("FOUND ENEMY HQ");
                    enemyHqLoc = robotAtPosEnemyHQLoc.location;
                    broadcastedEnemyLoc = comms.broadcastMessage(6, enemyHqLoc, 3);
                } else {
                    posEnemyHqLoc.remove(0);
                }
            }

            nav.goTo(posEnemyHqLoc.get(0));
        }
    }

    // Starts to build buildings needed for rush
    public void buildBuildingsForRush() throws GameActionException {
        if (builtDesignSchool == 0) {
            if (rc.getLocation().isWithinDistanceSquared(enemyHqLoc, 8)) {
                if (rc.getTeamSoup() >= 152) {
                    builtDesignSchool = buildInDirection(RobotType.DESIGN_SCHOOL, rc.getLocation().directionTo(enemyHqLoc));

                    if (builtDesignSchool != 0) {
                        comms.broadcastMessage(8, builtDesignSchool, 2);
                    }
                }
            } else {
                nav.goTo(enemyHqLoc);
            }
        } else {
            if (isNearbyRobot(robotsSensedDuringTurn, RobotType.DELIVERY_DRONE)) {
                if (builtNetGun == 0) {
                    builtNetGun = buildInDirection(RobotType.NET_GUN, Direction.CENTER);
                }
            }
        }
    }

    public void mineAndBuild() throws GameActionException {

        decipherCurrentBlockChainMessage();

        //allow enough time to pass for miners to no longer be moving randomly
        if (rc.getRoundNum() > 150) {
            //check if the miner is stuck
            trackPreviousLocations(rc.getLocation());
            if (isStuck) {
                ifIsStuck();
            }
        }

        if (buildDesignSchool) {
            if (rc.getTeamSoup() >= 152) {
                attemptBuildDesignSchool();
            }
        }

        // Try and refine
        for (Direction dir : Util.directions) {
            if (tryRefine(dir)) {
                nav.prevLocations.clear();
                System.out.println("I refined soup! " + rc.getTeamSoup());
            }
        }

        if(rc.getRoundNum() % 5 == 0){
            System.out.println("Before removing soup Locations: " + Clock.getBytecodesLeft());
            checkIfSoupGone();
        }


        System.out.println("Before checking soup Locations: " + Clock.getBytecodesLeft());
        if(rc.getRoundNum() % 7 == 0){
            MapLocation[] possibleSoupLocations = rc.senseNearbySoup();
            findReachableSoupLocations(possibleSoupLocations);
        }
        System.out.println("After checking soup Locations: " + Clock.getBytecodesLeft());


        for (Direction dir : Util.miningDirections) {
            if (rc.onTheMap(rc.getLocation().add(dir)) && rc.senseSoup(rc.getLocation().add(dir)) > 0) {
                attemptBuildRefinery();

                if (tryMine(dir)) {
                    System.out.println("I mined soup! " + rc.getSoupCarrying());
                }
            }
        }


        System.out.println("NUM DESIGN: " + numDesignSchools);
        if (numDesignSchools < 1 && !rush) {
            if(canBuildUnit(RobotType.DESIGN_SCHOOL, 4)){
                if(buildInDirection(RobotType.DESIGN_SCHOOL,rc.getLocation().directionTo(hqLoc).opposite()) != 0){
                    comms.broadcastMessage(1, 1);
                }
            }
        }

        if (numFulfillmentCenters < 1 && !rush) {
            if(canBuildUnit(RobotType.FULFILLMENT_CENTER, 4)){
                if(buildInDirection(RobotType.FULFILLMENT_CENTER,rc.getLocation().directionTo(hqLoc).opposite()) != 0){
                    comms.broadcastMessage(4, 1);
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
        if (rc.getSoupCarrying() == rc.getType().soupLimit) {
            goTowardsRefinery();

        } else if (soupLocations.size() > 0) {
            goTowardSoup();
        } else {
            if (nav.goTo(Util.randomDirection())) {
                stuck = 0;
            } else {
                ++stuck;
            }
        }
        System.out.println(Clock.getBytecodesLeft());
    }

    public void ifIsStuck() throws GameActionException{
        System.out.println(nav.prevLocations);
        if (!tryUnstuck()) {
            System.out.println("Miner cannot get unstuck.");
            //if stuck, try to build a refineru
            if (canBuildRefinery(rc.getLocation())) {
                for (Direction dir2 : Util.directions) {
                    if (tryBuildBuilding(RobotType.REFINERY, dir2)) {
                        break;
                    }
                }
            }
        }
    }

    public void goTowardSoup() throws GameActionException{
        System.out.println(Clock.getBytecodesLeft());
        MapLocation closestSoup = getClosestLoc(soupLocations);

        System.out.println("Moving toward soup loc: " + closestSoup);
        if (nav.goTo(closestSoup)) {
            stuck = 0;
        } else {
            ++stuck;
        }
    }

    public void attemptBuildDesignSchool() throws GameActionException {
        for (Direction dir : Util.directions) {

            if (tryBuildBuilding(RobotType.DESIGN_SCHOOL, dir)) {
                buildDesignSchool = false;
                RobotInfo designSchool = rc.senseRobotAtLocation(rc.getLocation().add(dir));
                comms.broadcastMessage(8, designSchool.ID, 2);
            }
        }
    }

    public void attemptBuildRefinery() throws GameActionException {
        if (canBuildRefinery(rc.getLocation())) {
            for (Direction dir2 : Util.directions) {
                if (tryBuildBuilding(RobotType.REFINERY, dir2)) {
                    break;
                }
            }
        }
    }

    public void goTowardsRefinery() throws GameActionException {
        System.out.println("At soup carrying limit " + rc.getType().soupLimit);
        // If early in the game head to hq, else head to closest refinery
        if (rc.getRoundNum() < 150 || refineryLocations.size() < 1) {
            nav.goTo(hqLoc);
        } else {
            MapLocation closestRefinery = getClosestLoc(refineryLocations);
            if (nav.goTo(closestRefinery)) {
                stuck = 0;
            } else {
                ++stuck;
            }
        }
    }
    // ****************************** Block Chain *********************************

    public void decipherAllBlockChainMessages() {
        for (int[] message : teamMessages) {
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
        ArrayList<int[]> currentRoundMessages = comms.getPrevRoundMessages();
        for (int[] message : currentRoundMessages) {
            System.out.println(message[1]);
            if (message[1] == 1) {
                ++numDesignSchools;
            } else if (message[1] == 4) {
                ++numFulfillmentCenters;
            } else if (message[1] == 3) {
                ++numRefineries;
                refineryLocations.add(new MapLocation(message[2], message[3]));
            } else if (message[1] == 2) {
                System.out.println("New Soup Location");
                MapLocation newSoup = new MapLocation(message[2], message[3]);
                if (!soupLocations.contains(newSoup)) {
                    soupLocations.add(new MapLocation(message[2], message[3]));
                }
            }
            // Set Enemy Hq Location
            else if (message[1] == 6) {
                System.out.println("Got enemy location");
                enemyHqLoc = new MapLocation(message[2], message[3]);
                System.out.println(enemyHqLoc);
            } else if (message[1] == 7 && message[4] == rc.getID()) {
                buildDesignSchool = true;
            } else if (message[1] == 13) {
                MapLocation soupGone = new MapLocation(message[2], message[3]);
                soupLocations.remove(soupGone);
            } else if (message[1] == 14) {
                canBuild = true;
            }
        }
    }

    public boolean canBuildUnit(RobotType robotType, int costToBroadcast){
        int teamSoup = rc.getTeamSoup();

        return teamSoup > robotType.cost + costToBroadcast;
    }

}

