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
    boolean job = false;
    boolean protect = false;
    boolean search = false;
    boolean rush = false;
    int roundCreated = 0;
    boolean searchedBlockChainForEnemyHq = false;

    ArrayList<MapLocation> enemyBuildings = new ArrayList<>();

    public Landscaper(RobotController r){
        super(r);

    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        // Decipher all blockchain
        if(!teamMessagesSearched){
            decipherAllBlockChainMessages();
        }

        // Decipher current blockchain messages
        decipherCurrentBlockChainMessage();

        // Decide whether to rush enemy hq or defend hq
        if(!job){
            if(enemyHqLoc == null){
                protect = true;
            } else {
                int distanceToHq = rc.getLocation().distanceSquaredTo(hqLoc);
                int distanceToEnemyHq = rc.getLocation().distanceSquaredTo(enemyHqLoc);
                if(distanceToEnemyHq < distanceToHq){
                    rush = true;
                } else {
                    protect = true;
                }
            }
            job = true;
        }

        // If rushing hq
        if (rush){
            int distanceToEnemyHq = rc.getLocation().distanceSquaredTo(enemyHqLoc);
            Direction dirToEnemyHq = rc.getLocation().directionTo(enemyHqLoc);

            // If next to enemy hq, deposit dirt on top enemy hq
            if( distanceToEnemyHq < 4){
                if(rc.getDirtCarrying() == 0){
                    if(rc.canDigDirt(Direction.CENTER)){
                        rc.digDirt(Direction.CENTER);
                    }
                }else {
                    if (rc.canDepositDirt(dirToEnemyHq)){
                        rc.depositDirt(dirToEnemyHq);
                        System.out.println(rc.getLocation().add(dirToEnemyHq));
                    }
                }
            }

            // Try and get to enemy Hq
            else{
                //If next to wall, try to build up, else move to enemy hq
                if(distanceToEnemyHq < 14 && !rc.canMove(dirToEnemyHq)){
                    System.out.println("THERE IS A WALL");
                    // Try to move to enemy hq if possible
                    if(!nav.tryMoveForward(dirToEnemyHq)){
                        digToDir(rc.getLocation().directionTo(enemyHqLoc));
                    }

                } else {
                    nav.goTo(enemyHqLoc);
                }
            }
        } else {

            // PROTECTING HQ
            if (hqLoc.isAdjacentTo(rc.getLocation()) && rc.getRoundNum() > 200) {
                Direction dirtohq = rc.getLocation().directionTo(hqLoc);

                // Dig dirt off of hq if being attacked
                if(rc.canDigDirt(dirtohq)){
                    rc.digDirt(dirtohq);
                }else {
                    if (rc.getDirtCarrying() == 0){
                        Direction[] directions = {dirtohq.opposite(),dirtohq.opposite().rotateLeft(), dirtohq.rotateRight()};
                        boolean dugDirt = false;
                        for(int i = 0; i < directions.length && !dugDirt; ++i){
                            if(rc.canDigDirt(directions[i])){
                                rc.digDirt(directions[i]);
                                dugDirt = true;
                            }
                        }
                    }
                }

                // Find best location to place dirt
                MapLocation bestLocation = null;

                int lowestElevation = 9999999;
                //Loops through all of the locations around hq and checks for the lowest elevation that can be dropped, then drops it
                for(Direction dir : Util.directions){
                    // Add function: Takes a map location add a direction and returns the first location plus the direction
                    MapLocation tileToCheck = hqLoc.add(dir);
                    System.out.println(tileToCheck);
                    if(rc.getLocation().distanceSquaredTo(tileToCheck) < 4
                            && rc.canDepositDirt(rc.getLocation().directionTo(tileToCheck))){
                        if(rc.senseElevation(tileToCheck) < lowestElevation){
                            lowestElevation = rc.senseElevation(tileToCheck);
                            bestLocation = tileToCheck;
                        }
                    }
                }

                if(rc.isReady()){
                    if (Math.random() < .8) {
                        if (bestLocation != null) {
                            rc.depositDirt(rc.getLocation().directionTo(bestLocation));
                        }
                    } else {
                        if (bestLocation != null && rc.canMove(rc.getLocation().directionTo(bestLocation))){
                            rc.move(rc.getLocation().directionTo(bestLocation));
                        }
                    }
                }

            } else {
                // If next to enemy building, try to bury it
                RobotInfo[] robots = rc.senseNearbyRobots(30, rc.getTeam().opponent());
                for(RobotInfo robot : robots){
                    if(robot.getType() == RobotType.DESIGN_SCHOOL || robot.getType() == RobotType.NET_GUN){
                        System.out.println("Found enemy building");

                        if(rc.getLocation().isAdjacentTo(robot.location)){
                            if(rc.getDirtCarrying() == 0){
                                for(Direction dir : Util.directions){
                                    if(rc.canDigDirt(dir)){
                                        rc.digDirt(dir);
                                    }
                                }
                            } else {
                                if(rc.canDepositDirt(rc.getLocation().directionTo(robot.location))){
                                    rc.depositDirt(rc.getLocation().directionTo(robot.location));
                                }
                            }
                        } else {
                            nav.goTo(robot.location);
                        }
                    }
                }

                // If no enemy robots nearby, try to head to hq
                if(robots.length == 0)
                    nav.goTo(hqLoc);
            }
        }
    }

    // Digs up or down depending on the elevation
    void digToDir(Direction toGo) throws GameActionException {
        MapLocation currentLoc = rc.getLocation();
        int currentElevation = rc.senseElevation(currentLoc);
        int elevationToGo = rc.senseElevation(currentLoc.add(toGo));
        if(currentElevation < elevationToGo){
            digUp(toGo);
        } else {
            digDown(toGo);
        }
    }

    // Dig down
    void digDown(Direction toGo) throws GameActionException {
        if(rc.getDirtCarrying() == 0){
            if(rc.canDigDirt(Direction.CENTER)){
                rc.digDirt(Direction.CENTER);
            }
        } else {
            if(rc.canDepositDirt(toGo)){
                rc.depositDirt(toGo);
            }
        }
    }

    // Dig up
    void digUp(Direction toGo) throws GameActionException {
        if(rc.getDirtCarrying() == 0){
            if(rc.canDigDirt(toGo)){
                rc.digDirt(toGo);
            }
        } else {
            if(rc.canDepositDirt(Direction.CENTER)){
                rc.depositDirt(Direction.CENTER);
            }
        }
    }

    // Decipher all blockchain messages
    public void decipherAllBlockChainMessages(){
        for(int [] message : teamMessages){
            // Set Hq Location
            if(message[1] == 0){
                System.out.println("Get Hq Location");
                hqLoc = new MapLocation(message[2], message[3]);
                System.out.println(hqLoc);
            }
            // Set Enemy Hq Location
            else if(message[1] == 6){
                System.out.println("Got enemy location");
                enemyHqLoc = new MapLocation(message[2], message[3]);
                System.out.println(enemyHqLoc);
            }
            // Robot specific messages
            else if(message[1] == 7){
                System.out.print("Recieved personal message");
            }
        }
        teamMessagesSearched = true;
    }

    // Decipher current blockchain messages
    public void decipherCurrentBlockChainMessage() throws GameActionException {
        ArrayList<int []> currentBlockChainMessage = comms.getPrevRoundMessages();
        for(int [] message : currentBlockChainMessage){
            // Add enemy buildings
            if (message[1] == 10) {
                enemyBuildings.add(new MapLocation(message[2], message[3]));
            }
            // Add enemy hq loc
            else if (message[1] == 6) {
                enemyHqLoc = new MapLocation(message[2], message[3]);
            }
        }
    }

}
