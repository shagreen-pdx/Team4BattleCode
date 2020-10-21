package team4player;
import battlecode.common.*;

import java.util.Map;

import static java.lang.Math.min;

public strictfp class RobotPlayer {
    static RobotController rc;

    static Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST
    };
    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

    static int turnCount;
    static MapLocation hqloc;
    static int numMiners;
    static int numLandScapers;
    static int numDesignSchool;
    static int globePollution;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        team4player.RobotPlayer.rc = rc;

        turnCount = 0;
        numDesignSchool = 0;

        System.out.println("I'm a " + rc.getType() + " and I just got created!");
        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You can add the missing ones or rewrite this into your own control structure.
                System.out.println("I'm a " + rc.getType() + "! Location " + rc.getLocation());
                findHQ();
                switch (rc.getType()) {
                    case HQ:                 runHQ();                break;
                    case MINER:              runMiner();             break;
                    case REFINERY:           runRefinery();          break;
                    case VAPORATOR:          runVaporator();         break;
                    case DESIGN_SCHOOL:      runDesignSchool();      break;
                    case FULFILLMENT_CENTER: runFulfillmentCenter(); break;
                    case LANDSCAPER:         runLandscaper();        break;
                    case DELIVERY_DRONE:     runDeliveryDrone();     break;
                    case NET_GUN:            runNetGun();            break;
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }

    static void runHQ() throws GameActionException {
        if(numMiners < 10){
            for (Direction dir : directions){
                if(tryBuild(RobotType.MINER, dir)){
                    ++numMiners;
                }
            }
        }
    }

    static void runMiner() throws GameActionException {


        tryBlockchain();
//        tryBuild(randomSpawnedByMiner(), randomDirection());
//        for (Direction dir : directions)
//            tryBuild(RobotType.FULFILLMENT_CENTER, dir);
        for (Direction dir : directions)
            if (tryRefine(dir))
                System.out.println("I refined soup! " + rc.getTeamSoup());
        for (Direction dir : directions){
            if (tryMine(dir))
                System.out.println("I mined soup! " + rc.getSoupCarrying());
        }
        //only try to build design school every 50 turns, otherwise try to build a refinery
        if (turnCount % 50 == 0) {
            if (!nearbyRobot(RobotType.DESIGN_SCHOOL)) {
                if (tryBuild(RobotType.DESIGN_SCHOOL, randomDirection()))
                    System.out.println("Built Design School");
                ++numDesignSchool;
            }
        }

        //try building refinery
        if (!nearbyRobot(RobotType.REFINERY)) {
            if (tryBuild(RobotType.REFINERY, randomDirection()))
                System.out.println("Built Refinery");
        }

        if(rc.getSoupCarrying() == rc.getType().soupLimit) {
            System.out.println("At soup carrying limit " + rc.getType().soupLimit);
            if(goTo(hqloc)){
                System.out.println("Moving towards HQ");
            }
        }else if (goTo(randomDirection()))
            System.out.println("I moved in random direction!");

    }

    static void runRefinery() throws GameActionException {
        int old_soup = rc.getTeamSoup();
        RobotPlayer.globePollution = RobotType.REFINERY.globalPollutionAmount;
        //System.out.println("Pollution(sense): " + rc.sensePollution(rc.getLocation()));
        System.out.println("Team Soup deposited: " + rc.getTeamSoup());
        System.out.println("Soup Refined: " + min(RobotType.REFINERY.maxSoupProduced, rc.getSoupCarrying()));
        if (old_soup != rc.getTeamSoup())
            RobotPlayer.globePollution += 1;
        System.out.println("Globe pollution level: " + RobotPlayer.globePollution);
        System.out.println("Range of pollution: " + RobotType.REFINERY.pollutionRadiusSquared);
        System.out.println("Temp pollution in the range: " + RobotType.REFINERY.localPollutionAdditiveEffect);
    }

    static void runVaporator() throws GameActionException {

    }

    static void runDesignSchool() throws GameActionException {
        if (rc.isReady()) {
            for (Direction dir : directions) {
                if (tryBuild(RobotType.LANDSCAPER, dir)) {
                    System.out.println("Created a new landscaper!");
                    ++numLandScapers;
                    System.out.println("Number of landscapers:" + numLandScapers);
                }
            }
        }
    }

    static void runFulfillmentCenter() throws GameActionException {
        for (Direction dir : directions)
            tryBuild(RobotType.DELIVERY_DRONE, dir);
    }

    static void runLandscaper() throws GameActionException
    {
        /* Landscaper: moves dirt around the map to adjust elevation and destroy buildings.

Produced by the design school.
Can perform the action rc.digDirt() to remove one unit of dirt from an adjacent tile or its current tile, increasing the landscaper’s stored dirt by 1 up to a max of RobotType.LANDSCAPER.dirtLimit (currently set to 25). If the tile is empty, flooded, or contains another unit, this reduces the tile’s elevation by 1. If the tile contains a building, it removes one unit of dirt from the building, or if the building is not buried, has no effect.
Can perform the action rc.depositDirt() to reduce its stored dirt by one and place one unit of dirt onto an adjacent tile or its current tile. If the tile contains a building, the dirt partially buries it–the health of a building is how much dirt can be placed on it before it is destroyed. If the tile is empty, flooded, or contains another unit, the only effect is that the elevation of that tile increases by 1.
Note: all this means that buildings may never change elevation, so be careful to contain that water level.
When a landscaper dies, the dirt it’s carrying is dropped on the current tile.
If enough dirt is placed on a flooded tile to raise its elevation above the water level, it becomes not flooded. */
        if(rc.getDirtCarrying() == 0){
            tryDig();
        }

        MapLocation bestLocation = null;
        if(hqloc != null){

            int lowestElevation = 9999999;
            //Loops through all of the locations around hq and checks for the lowest elevation that can be dropped, then drops it
            for(Direction dir : directions){
                // Add function: Takes a map location add a direction and returns the first location plus the direction
                MapLocation tileToCheck = hqloc.add(dir);
                if(rc.getLocation().distanceSquaredTo(tileToCheck) < 4
                        && rc.canDepositDirt(rc.getLocation().directionTo(tileToCheck))){
                    if(rc.senseElevation(tileToCheck) < lowestElevation){
                        lowestElevation = rc.senseElevation(tileToCheck);
                        bestLocation = tileToCheck;
                    }
                }
            }
            // Will be null if it knows where the hq is but all of the locations are blocked
        }
        if(Math.random() < 0.4){
            if(bestLocation != null){
                rc.depositDirt(rc.getLocation().directionTo(bestLocation));
                System.out.println("Building a wall");
            }
        }

        // Try to get to hq
        if(hqloc != null){
            goTo(hqloc);
        }else{
            tryMove(randomDirection());
        }

    }

    static void runDeliveryDrone() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        if (!rc.isCurrentlyHoldingUnit()) {
            // See if there are any enemy robots within capturing range
            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);

            if (robots.length > 0) {
                // Pick up a first robot within range
                rc.pickUpUnit(robots[0].getID());
                System.out.println("I picked up " + robots[0].getID() + "!");
            }
        } else {
            // No close robots, so search for robots within sight radius
            tryMove(randomDirection());
        }
    }

    static void runNetGun() throws GameActionException {

    }

    static void findHQ() throws GameActionException{
        if(hqloc == null){
            RobotInfo[] robots = rc.senseNearbyRobots();
            for(RobotInfo robot : robots){
                if(robot.type == RobotType.HQ && robot.team == rc.getTeam()){
                    hqloc = robot.location;
                }
            }
        }

        // Later: Communicate via blockchain to find HQ location
        if(hqloc == null){
            getHqFromBlockchain();
        }
    }

    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    /**
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnedByMiner() {
        return spawnedByMiner[(int) (Math.random() * spawnedByMiner.length)];
    }

    static boolean tryMove() throws GameActionException {
        for (Direction dir : directions)
            if (tryMove(dir))
                return true;
        return false;
        // MapLocation loc = rc.getLocation();
        // if (loc.x < 10 && loc.x < loc.y)
        //     return tryMove(Direction.EAST);
        // else if (loc.x < 10)
        //     return tryMove(Direction.SOUTH);
        // else if (loc.x > loc.y)
        //     return tryMove(Direction.WEST);
        // else
        //     return tryMove(Direction.NORTH);
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMine(Direction dir) throws GameActionException {
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
    static boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }

    static boolean tryDig() throws GameActionException {
        Direction dir = randomDirection();
        if(rc.canDigDirt(dir)){
            rc.digDirt(dir);
            return true;
        }
        return false;
    }

    static boolean goTo(Direction dir) throws GameActionException {
        Direction[] toTry = {dir, dir.rotateLeft(), dir.rotateRight(), dir.rotateRight().rotateRight(), dir.rotateLeft().rotateLeft()};
        for(Direction d : toTry){
            if(tryMove(d)){
                return true;
            }
        }
        return false;
    }

    static boolean goTo(MapLocation destination) throws GameActionException {
        return goTo(rc.getLocation().directionTo(destination));
    }


    static void tryBlockchain() throws GameActionException {
        if (turnCount < 3) {
            int[] message = new int[7];
            for (int i = 0; i < 7; i++) {
                message[i] = 123;
            }
            if (rc.canSubmitTransaction(message, 10))
                rc.submitTransaction(message, 10);
        }
        // System.out.println(rc.getRoundMessages(turnCount-1));
    }

    /**
     *
     * @param target the robot that we want to see if nearby
     * @return true if target robot is nearby
     * @throws GameActionException
     */
    static boolean nearbyRobot(RobotType target) throws GameActionException{
        RobotInfo[] robots = rc.senseNearbyRobots();
        for(RobotInfo robot : robots){
            if(robot.getType() == target){
                return true;
            }
        }
        return false;
    }

    //beginning communication with blockchain
    static final int teamSecret = 12345;  //all messages from team4player will begin with this key
    static final String[] messageType = {"HQ loc", }; //every message has a message type

    /*
    * send message with location of HQ to the blockchain
    * */
    public static void sendHqLoc(MapLocation loc) throws GameActionException
    {
        int [] message = new int [7];
        message[0] = teamSecret;
        message[1] = 0; //index of message type
        message[2] = loc.x;
        message[3] = loc.y;

        if(rc.canSubmitTransaction(message, 3))  //3 is transaction cost?  can be increased to up chances of being visible in blockchain
            rc.submitTransaction(message, 3);
    }

    public static void getHqFromBlockchain() throws GameActionException
    {
        for(int i = 1; i < rc.getRoundNum(); i++)
        {
            for(Transaction tx : rc.getBlock(i))
            {
                int [] myMessage = tx.getMessage();
                if(myMessage[0] == teamSecret && myMessage[1] == 0) { //check that message is from our team and the type is hqloc
                    System.out.println("got HQ location!");
                    hqloc = new MapLocation(myMessage[2], myMessage[3]); //hqloc = mew map location with x and y coords from message
                   // System.out.println(hqloc);
                }
            }

        }
    }
}


