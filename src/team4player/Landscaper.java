package team4player;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Map;

/* Landscaper: moves dirt around the map to adjust elevation and destroy buildings.

Produced by the design school.
Can perform the action rc.digDirt() to remove one unit of dirt from an adjacent tile or its current tile, increasing the landscaper’s stored dirt by 1 up to a max of RobotType.LANDSCAPER.dirtLimit (currently set to 25). If the tile is empty, flooded, or contains another unit, this reduces the tile’s elevation by 1. If the tile contains a building, it removes one unit of dirt from the building, or if the building is not buried, has no effect.
Can perform the action rc.depositDirt() to reduce its stored dirt by one and place one unit of dirt onto an adjacent tile or its current tile. If the tile contains a building, the dirt partially buries it–the health of a building is how much dirt can be placed on it before it is destroyed. If the tile is empty, flooded, or contains another unit, the only effect is that the elevation of that tile increases by 1.
Note: all this means that buildings may never change elevation, so be careful to contain that water level.
When a landscaper dies, the dirt it’s carrying is dropped on the current tile.
If enough dirt is placed on a flooded tile to raise its elevation above the water level, it becomes not flooded. */
public class Landscaper extends Unit{
    boolean search = false;
    boolean rush = false;
    int roundCreated = 0;
    ArrayList<MapLocation> posEnemyHqLoc = new ArrayList<MapLocation>();

    public Landscaper(RobotController r){
        super(r);

    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        // Get instructions when just created
        if(roundCreated == 0){
            search = comms.getMessageFromBlockchain(5, rc.getID());
            if(search)
                System.out.println("Searching for enemy Hq");
            else{
                System.out.println("Defending Hq");
            }
            roundCreated = rc.getRoundNum();
        }

        // Dig dirt
        if(rc.getDirtCarrying() == 0){
            tryDig();
        }

        // Check blockchain for enemy HQ
        if (enemyHqLoc == null){
            enemyHqLoc = comms.getEnemyHQFromBlockchain();
            if(enemyHqLoc != null){
                rush = true;
                System.out.println("Got enemy Hq from Blockchain");
            }
        }

        // Rush Hq
        if(rush) {
            System.out.println("RUSHING HQ");
            if(rc.getLocation().distanceSquaredTo(enemyHqLoc) < 4
                    && rc.canDepositDirt(rc.getLocation().directionTo(enemyHqLoc))){
                rc.depositDirt(rc.getLocation().directionTo(enemyHqLoc));
            }
            else{
                nav.goTo(enemyHqLoc);
            }
        } else {
            // Search Hq
            if(search){
                System.out.println("SEARCHING HQ");
                if(posEnemyHqLoc.isEmpty()){
                    calcPosEnemyHqLoc();
                }
                searchForEnemyHq();

            } else { // Protect Hq
                System.out.println("PROTECTING HQ");
                MapLocation bestLocation = null;
                if(hqLoc != null){

                    int lowestElevation = 9999999;
                    //Loops through all of the locations around hq and checks for the lowest elevation that can be dropped, then drops it
                    for(Direction dir : Util.directions){
                        // Add function: Takes a map location add a direction and returns the first location plus the direction
                        MapLocation tileToCheck = hqLoc.add(dir);
                        if(rc.getLocation().distanceSquaredTo(tileToCheck) < 4
                                && rc.canDepositDirt(rc.getLocation().directionTo(tileToCheck))){
                            if(rc.senseElevation(tileToCheck) < lowestElevation){
                                lowestElevation = rc.senseElevation(tileToCheck);
                                bestLocation = tileToCheck;
                            }
                        }
                    }
                    // Will be null if it knows where the hq is but all of the locations are blocked
                } else {
                    System.out.println("Can't find HQ");
                }
                if(Math.random() < 0.6){
                    if(bestLocation != null){
                        rc.depositDirt(rc.getLocation().directionTo(bestLocation));
                        System.out.println("Building a wall");
                    }
                }

                // Try to get to hq
                if(hqLoc != null){
                    nav.goTo(hqLoc);
                }else{
                    nav.tryMove(Util.randomDirection());
                }
            }
        }
    }

    boolean tryDig() throws GameActionException {
        Direction dir = Util.randomDirection();
        if(rc.canDigDirt(dir)){
            rc.digDirt(dir);
            return true;
        }
        return false;
    }

    public void searchForEnemyHq() throws GameActionException{

        if(enemyHqLoc == null){
            System.out.println("Searching the following Enemy locations: ");
            System.out.println(posEnemyHqLoc);

            findEnemyHq();

            // Still havent found enemy Hq
            if(enemyHqLoc == null){
                System.out.println("I'm at the location: ");
                System.out.println(rc.getLocation());

                // If at one of the possible locations, remove it
                if(rc.canSenseLocation(posEnemyHqLoc.get(0))){
                    System.out.println("Enemy HQ not found at: " + posEnemyHqLoc.get(0));
                    posEnemyHqLoc.remove(0);
                }else{
                    nav.goTo(posEnemyHqLoc.get(0));
                }
            }
        }
    }

    public void calcPosEnemyHqLoc(){
        if(hqLoc != null){

            MapLocation enemyHqSymetric = new MapLocation((nav.mapWidth - 1 - hqLoc.x),(nav.mapHeight - 1 - hqLoc.y));
            MapLocation enemyHqHorizontal = new MapLocation((nav.mapWidth - 1 - hqLoc.x),(hqLoc.y));
            MapLocation enemyHqVertical = new MapLocation((hqLoc.x),(nav.mapHeight - 1 - hqLoc.y));
            posEnemyHqLoc.add(enemyHqHorizontal);
            posEnemyHqLoc.add(enemyHqSymetric);
            posEnemyHqLoc.add(enemyHqVertical);
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

}
