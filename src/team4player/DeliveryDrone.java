package team4player;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static team4player.Util.directions;
import static team4player.Util.randomDirection;

public class DeliveryDrone extends Unit{

    int numRobotsAdjacentToHq = 0;
    MapLocation locEnemyBot = null;
    ArrayList<MapLocation> refineryLocations = new ArrayList<MapLocation>();
    int currentlyHeldRobotId = 0;
    boolean haveEnemyBot = false;
    boolean haveCow = false;
    boolean search = true;

    public DeliveryDrone(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        // Calculate all possible enemy locations
        if (posEnemyHqLoc.isEmpty()) {
            calcPosEnemyHqLoc();
        }

        // Decipher all blockchain messages. Only happens once.
        if (!teamMessagesSearched) {
            decipherAllBlockChainMessages();
        }

        // Decipher last blockchain message. Once per turn.
        decipherCurrentBlockChainMessage();

        // Record flooded locations
        recordWater();

        if(haveEnemyBot)
            takeTurnEnemyBot();
        else if(haveCow)
            moooveCow();
        else if(rush)
            takeTurnRush();
        else if(search)
            takeTurnSearch();
        else
            takeTurnRest();

    }

    public void takeTurnEnemyBot() throws GameActionException {
        // If holding enemy bot, try to drop in water
        for (Direction dir : Util.directions) {
            if (rc.canSenseLocation(rc.getLocation().add(dir)) && rc.senseFlooding(rc.getLocation().add(dir))) {
                rc.dropUnit(dir);
                haveEnemyBot = false;
            }
        }
        MapLocation closestFloodedLoc = getClosestLoc(floodedLocations);
        if (closestFloodedLoc != null) {
            nav.tryFly(rc.getLocation().directionTo(closestFloodedLoc));
        } else {
            nav.tryFly(randomDirection());
        }
    }

    public void takeTurnRush() throws GameActionException {
        System.out.println("Rush HQ");
        if (rc.getLocation().isWithinDistanceSquared(enemyHqLoc, 35)) {
            if (rc.isCurrentlyHoldingUnit()) {
                for (Direction dir : directions) {
                    if (rc.canDropUnit(dir) && !rc.senseFlooding(rc.getLocation().add(dir))) {
                        rc.dropUnit(dir);
                        comms.broadcastMessage(7, currentlyHeldRobotId, 2);
                    }
                }
                nav.tryFly(randomDirection());
            }

            pickupEnemyBots();

        } else {
            System.out.println("Flying to enemy hq");
            nav.flyTo(enemyHqLoc);
        }
    }

    public void takeTurnSearch() throws GameActionException {
        System.out.println("Search for hq");
        // Pick up miner before traveling to enemy hq
        if (!rc.isCurrentlyHoldingUnit()) {
            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, rc.getTeam());

            System.out.println(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED);
            System.out.println();
            if (robots.length > 0) {
                // Pick up a first robot within range
                for (RobotInfo robot : robots) {
                    if (robot.getType() == RobotType.MINER) {
                        rc.pickUpUnit(robot.getID());
                        currentlyHeldRobotId = robot.getID();
                        System.out.println("I picked up " + robot.getID() + "!");
                        break;
                    }
                }
            }
            if (!refineryLocations.isEmpty()) {
                MapLocation newestRefinery = refineryLocations.get(refineryLocations.size() - 1);
                System.out.println("Moving towards: " + newestRefinery);
                if (rc.getLocation().isWithinDistanceSquared(newestRefinery, 100)) {
                    nav.tryFly(randomDirection());
                } else {
                    nav.flyTo(newestRefinery);
                }
            } else {
                nav.tryFly(randomDirection());
            }
        }
        // Once have miner, fly to enemy hq
        if (rc.isCurrentlyHoldingUnit()) {
            searchForEnemyHq();
        }
    }

    public void takeTurnRest() throws GameActionException {
        System.out.println("Protect Hq");
        if(!rc.getLocation().isWithinDistanceSquared(hqLoc, 20)){
            nav.flyTo(hqLoc);
        }

        // Drop landscaper off at wall
        if(rc.isCurrentlyHoldingUnit()){
            if(numRobotsAdjacentToHq == 8){
                for(Direction dir : directions){
                    if(rc.canDropUnit(dir)){
                        rc.dropUnit(dir);
                    }
                }
            }else {
                for(Direction dir : directions){
                    if(rc.getLocation().add(dir).isAdjacentTo(hqLoc) && rc.canDropUnit(dir)){
                        rc.dropUnit(dir);
                    }
                }
            }

            nav.flyTo(hqLoc);
        } else {
            pickupEnemyBots();
            pickupCows();
            moveLandscaper();
        }
    }

    public void searchForEnemyHq() throws GameActionException{
        if(enemyHqLoc == null){
            System.out.println("Searching the following Enemy locations: ");
            System.out.println(posEnemyHqLoc);
            findEnemyHq();

            // Still havent found enemy Hq
            if(enemyHqLoc == null){
                System.out.println(rc.getLocation());

                // If at one of the possible locations, remove it
                if(rc.canSenseLocation(posEnemyHqLoc.get(0))){
                    System.out.println("Enemy HQ not found at: " + posEnemyHqLoc.get(0));
                    posEnemyHqLoc.remove(0);
                }else{
                    nav.flyTo(posEnemyHqLoc.get(0));
                }
            }
        }
    }

    public void findEnemyHq() throws GameActionException{
        if(enemyHqLoc == null){
            RobotInfo[] robots = rc.senseNearbyRobots();
            for(RobotInfo robot : robots){
                if(robot.type == RobotType.HQ && robot.team != rc.getTeam()){
                    enemyHqLoc = robot.location;
                    System.out.println("FOUND ENEMY HQ");
                    comms.broadcastMessage(enemyHqLoc, 6);
                    search = false;
                    rush = true;
                }
            }
        }
    }

    public void pickupEnemyBots() throws GameActionException {
        System.out.println("Trying to pick up enemy bot");
        if (!rc.isCurrentlyHoldingUnit()) {
            RobotInfo[] robots = rc.senseNearbyRobots(20, rc.getTeam().opponent());
            System.out.println(robots.length);
            if (robots.length > 0) {
                // Pick up a first robot within range
                boolean enemyRobotFound = false;
                int closestEnemyBotDistance = 9999;
                RobotInfo closestEnemyBot = null;
                for(int i = 0; i < robots.length && !haveEnemyBot; ++i){
                    System.out.println("Robot locaiton: " + robots[i].location);
                    if(rc.canPickUpUnit(robots[i].getID())){
                        rc.pickUpUnit(robots[i].getID());
                        System.out.println("I picked up an enemy bot");
                        haveEnemyBot = true;
                    } else {
                        if(isPickable(robots[i])){
                            if(enemyHqLoc != null){
                                if(!robots[i].location.isWithinDistanceSquared(enemyHqLoc,GameConstants.NET_GUN_SHOOT_RADIUS_SQUARED)){
                                    System.out.println("Not within distance");
                                    int distanceToEnemyBot = rc.getLocation().distanceSquaredTo(robots[i].location);
                                    if( distanceToEnemyBot < closestEnemyBotDistance){
                                        closestEnemyBot = robots[i];
                                        closestEnemyBotDistance = distanceToEnemyBot;
                                    }
                                }
                            }else {
                                System.out.println("Found enemy bot");
                                int distanceToEnemyBot = rc.getLocation().distanceSquaredTo(robots[i].location);
                                if( distanceToEnemyBot < closestEnemyBotDistance){
                                    closestEnemyBot = robots[i];
                                    closestEnemyBotDistance = distanceToEnemyBot;
                                }
                            }
                        }
                    }
                }
                if(!haveEnemyBot && closestEnemyBot != null){
                    nav.flyTo(closestEnemyBot.location);
                    System.out.println("Flying to location: " + closestEnemyBot.location);
                }
            }
        }
    }

    public void moveLandscaper() throws GameActionException {
        System.out.println("Can't sense enemy robots");
        if(numRobotsAdjacentToHq < 8){
            System.out.println("Trying to pick up friendly landscaper");
            RobotInfo[] landscapers = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, rc.getTeam());
            for(RobotInfo landscaper : landscapers){
                if(landscaper.getType() == RobotType.LANDSCAPER && !hqLoc.isAdjacentTo(landscaper.location)){
                    if(rc.canPickUpUnit(landscaper.getID())){
                        rc.pickUpUnit(landscaper.getID());
                    }
                }
            }
        }
        if(Math.random() < .8){
            nav.tryFly(rc.getLocation().directionTo(enemyHqSymetric));
        }else {
            nav.tryFly(randomDirection());
        }
    }

    public void decipherAllBlockChainMessages(){
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
            else if(message[1] == 3){
                refineryLocations.add(new MapLocation(message[2],message[3]));
            }
            else if (message[1] == 8){
                rush = true;
            }
            // Robot specific messages
            else if (message[1] == 5 && message[4] == rc.getID()){
                search = false;
            }
            else if (message[1] == 12){
                numRobotsAdjacentToHq = message[4];
                System.out.println(numRobotsAdjacentToHq);
            }
        }
        teamMessagesSearched = true;
    }

    public void decipherCurrentBlockChainMessage() throws GameActionException{
        ArrayList<int []> currentBlockChainMessage = comms.getPrevRoundMessages();
        for(int [] message : currentBlockChainMessage){
            if (message[1] == 8) {
                rush = true;
            }else if (message[1] == 9){
                locEnemyBot = new MapLocation(message[2], message[3]);
            }else if (message[1] == 11){
                floodedLocations.add(new MapLocation(message[2], message[3]));
            } else if (message[1] == 12){
                numRobotsAdjacentToHq = message[4];
                System.out.println(numRobotsAdjacentToHq);
            }
            // Set Enemy Hq Location
            else if(message[1] == 6){
                System.out.println("Got enemy location");
                enemyHqLoc = new MapLocation(message[2], message[3]);
                System.out.println(enemyHqLoc);
                search = false;
            }
            else if (message[1] == 5 && message[4] == rc.getID()){
                search = false;
            }
            else if (message[1] == 3) {
                refineryLocations.add(new MapLocation(message[2], message[3]));
            }
        }
    }

    public void recordWater() throws GameActionException {
        MapLocation curLoc = rc.getLocation();

        // If flooded location is not near other recorded flooded locations, broadcast location.
        if(rc.senseFlooding(curLoc)){
            if(!floodedLocations.isEmpty()){
                boolean toClose = false;
                for(MapLocation floodedLoc : floodedLocations){
                    System.out.println("found flooded location");
                    if(floodedLoc.distanceSquaredTo(curLoc) < 15){
                        toClose = true;
                    }
                }
                if(!toClose){
                    System.out.println("broadcast");
                    comms.broadcastMessage(curLoc, 11);
                }
            } else {
                comms.broadcastMessage(curLoc, 11);
            }
        }
    }
    public void pickupCows() throws GameActionException {
        boolean cowFound = false;
        int closestCowDistance = 9999;
        RobotInfo closestCow = null;

        System.out.println("Trying to pick up cow");
        if (!rc.isCurrentlyHoldingUnit()) {
            RobotInfo[] robots = rc.senseNearbyRobots();
            if (robots.length > 0) {
                // Pick up a first robot within range
                for (int i = 0; i < robots.length && !haveCow; ++i) {
                    if (robots[i].getType() == RobotType.COW) {
                        System.out.println("Cow location: " + robots[i].location);
                        if (rc.canPickUpUnit(robots[i].getID())) {
                            rc.pickUpUnit(robots[i].getID());
                            System.out.println("I picked up a cow");
                            haveCow = true;
                        } else {
                            int cowDistance = rc.getLocation().distanceSquaredTo(robots[i].location);
                            if (cowDistance < closestCowDistance) {
                                closestCowDistance = cowDistance;
                                closestCow = robots[i];
                            }

                        }
                    }
                }
                if (!haveCow && closestCow != null) {
                    nav.flyTo(closestCow.location);
                    System.out.println("Flying to location: " + closestCow.location);
                }
            }
        }
    }

    public void moooveCow() throws GameActionException {
        //if holding a cow, drop near enemy hq.  if enemy hq loc is null, drop in water
        if (haveCow) {
            if (enemyHqLoc != null) {
                int enemyHQdistance = rc.getLocation().distanceSquaredTo(enemyHqLoc);
                if (enemyHQdistance < 9) {
                    for (Direction dir : directions) {
                        if (rc.canDropUnit(dir)) {
                            rc.dropUnit(dir);
                            haveCow = false;
                            break;
                        }
                    }
                } else {
                    nav.flyTo(enemyHqLoc);
                }
            } else {
                for (Direction dir : Util.directions) {
                    if (rc.canSenseLocation(rc.getLocation().add(dir)) && rc.senseFlooding(rc.getLocation().add(dir))) {
                        rc.dropUnit(dir);
                        haveCow = false;
                        break;
                    }
                }
                if(haveCow){
                    MapLocation closestFloodedLoc = getClosestLoc(floodedLocations);
                    if (closestFloodedLoc != null) {
                        nav.tryFly(rc.getLocation().directionTo(closestFloodedLoc));
                    } else {
                        nav.tryFly(randomDirection());
                    }
                }
            }
        }
    }
}

