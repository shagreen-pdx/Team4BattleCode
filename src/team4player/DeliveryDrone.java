package team4player;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Arrays;

import static team4player.Util.directions;
import static team4player.Util.randomDirection;

public class DeliveryDrone extends Unit{

    ArrayList<MapLocation> refineryLocations = new ArrayList<MapLocation>();
    ArrayList<MapLocation> floodedLocations = new ArrayList<MapLocation>();
    int currentlyHeldRobotId = 0;
    boolean haveEnemyBot = false;
    boolean search = false;
    boolean rush = false;
    int roundCreated = 0;


    public DeliveryDrone(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException{
        super.takeTurn();

        // Decipher all blockchain messages. Only happens once.
        if(!teamMessagesSearched){
           decipherAllBlockChainMessages();
        }

        // Decipher last blockchain message. Once per turn.
        decipherCurrentBlockChainMessage();

        // Record flooded locations
        recordWater();

        // If holding enemy bot, try to drop in water
        if(haveEnemyBot){
            for(Direction dir : Util.directions){
                if(rc.canSenseLocation(rc.getLocation().add(dir)) && rc.senseFlooding(rc.getLocation().add(dir))){
                    rc.dropUnit(dir);
                    haveEnemyBot = false;
                }
            }
            MapLocation closestFloodedLoc = findClosestFloodedLoc(floodedLocations);
            if(closestFloodedLoc != null){
                nav.tryFly(rc.getLocation().directionTo(closestFloodedLoc));
            }else {
                nav.tryFly(randomDirection());
            }

        } else if(rush){
            System.out.println("Rush HQ");
            if(rc.canSenseLocation(enemyHqLoc)){
                if(rc.isCurrentlyHoldingUnit()){
                    for(Direction dir : directions){
                        if (rc.canDropUnit(dir)) {
                            rc.dropUnit(dir);
                            comms.broadcastMessage(currentlyHeldRobotId, 7);
                        }
                    }
                }

                pickupEnemyBots();

            } else {
                nav.flyTo(enemyHqLoc);
            }


        } else if(search){
            System.out.println("Search for hq");
            // Calculate all possible enemey locations
            if(posEnemyHqLoc.isEmpty()){
                calcPosEnemyHqLoc();
            }
            // Pick up miner before traveling to enemy hq
            if (!rc.isCurrentlyHoldingUnit()) {
                RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, rc.getTeam());

                System.out.println(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED);
                System.out.println();
                if (robots.length > 0) {
                    // Pick up a first robot within range
                    for(RobotInfo robot : robots){
                        if(robot.getType() == RobotType.MINER){
                            rc.pickUpUnit(robot.getID());
                            currentlyHeldRobotId = robot.getID();
                            System.out.println("I picked up " + robot.getID() + "!");
                            break;
                        }
                    }
                }
                if (!refineryLocations.isEmpty()){
                    MapLocation newestRefinery = refineryLocations.get(refineryLocations.size() -1);
                    System.out.println("Moving towards: " + newestRefinery);
                    if (rc.getLocation().isWithinDistanceSquared(newestRefinery,100)){
                        nav.tryFly(randomDirection());
                    } else {
                        nav.flyTo(newestRefinery);
                    }
                }
                else {
                    nav.tryFly(randomDirection());
                }
            }
            // Once have miner, fly to enemy hq
            if (rc.isCurrentlyHoldingUnit()) {
                searchForEnemyHq();
            }

        } else {
            System.out.println("Protect Hq");
            if(!rc.getLocation().isWithinDistanceSquared(hqLoc, 9)){
                nav.flyTo(hqLoc);
            }
            pickupEnemyBots();
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
        if (!rc.isCurrentlyHoldingUnit()) {
            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, rc.getTeam().opponent());
            System.out.println(robots.length);
            if (robots.length > 0) {
                // Pick up a first robot within range
                if(rc.canPickUpUnit(robots[0].getID())){
                    rc.pickUpUnit(robots[0].getID());
                    System.out.println("I picked up an enemy bot");
                    haveEnemyBot = true;
                }else {
                    System.out.println("Flying to enemey location");
                    nav.flyTo(robots[0].location);
                }
            }else {

                System.out.println("No enemy units found");
            }
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
                search = true;
            }
        }
        teamMessagesSearched = true;
    }

    public void decipherCurrentBlockChainMessage() throws GameActionException{
        ArrayList<int []> currentBlockChainMessage = comms.getPrevRoundMessages();
        for(int [] message : currentBlockChainMessage){
            if (message[1] == 8) {
                rush = true;
            }
            // Set Enemy Hq Location
            else if(message[1] == 6){
                System.out.println("Got enemy location");
                enemyHqLoc = new MapLocation(message[2], message[3]);
                System.out.println(enemyHqLoc);
                search = false;
            }
            else if (message[1] == 5 && message[4] == rc.getID()){
                search = true;
            }
            else if (message[1] == 3) {
                refineryLocations.add(new MapLocation(message[2], message[3]));
            }
        }
    }

    public void recordWater() throws GameActionException {
        if(rc.senseFlooding(rc.getLocation())){
            floodedLocations.add(rc.getLocation());
        }
    }

    public MapLocation findClosestFloodedLoc(ArrayList<MapLocation> floodedLocations){
        MapLocation currentLoc = rc.getLocation();
        int closestDistance = 9999;
        MapLocation closestFloodedLoc = null;

        for(MapLocation water : floodedLocations){
            int distanceToWater = currentLoc.distanceSquaredTo(water);
            if( distanceToWater < closestDistance){
                closestDistance = distanceToWater;
                closestFloodedLoc = water;
            }
        }
        return closestFloodedLoc;
    }
}
