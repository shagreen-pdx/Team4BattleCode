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

    public Landscaper(RobotController r){
        super(r);

    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if(!teamMessagesSearched){
            decipherAllBlockChainMessages();
        }

        decipherCurrentBlockChainMessage();



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

        if (rush){
            // Dig dirt
            if(rc.getDirtCarrying() == 0){
                tryDig();
            }

            if(rc.getLocation().distanceSquaredTo(enemyHqLoc) < 4
                    && rc.canDepositDirt(rc.getLocation().directionTo(enemyHqLoc))){
                rc.depositDirt(rc.getLocation().directionTo(enemyHqLoc));
            }
            else{
                System.out.println(enemyHqLoc.distanceSquaredTo(rc.getLocation()));
                if(rc.getLocation().distanceSquaredTo(enemyHqLoc) == 4 && !rc.canMove(rc.getLocation().directionTo(enemyHqLoc))){
                    System.out.println("THERE IS A WALL");
                    for (Direction direction : Util.directions){
                        if(rc.getLocation().add(direction).isAdjacentTo(enemyHqLoc))
                            if(rc.canDigDirt(direction)){
                                rc.digDirt(direction);
                            }
                    }
                    if(rc.canDepositDirt(Direction.CENTER)){
                        rc.depositDirt(Direction.CENTER);
                    }
                }
                nav.goTo(enemyHqLoc);
            }
        } else {
            // Dig dirt off of hq if being attacked
            if (hqLoc.isAdjacentTo(rc.getLocation())) {
                Direction dirtohq = rc.getLocation().directionTo(hqLoc);
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

                System.out.println(bestLocation);
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
                RobotInfo[] robots = rc.senseNearbyRobots(30, rc.getTeam().opponent());

                for(RobotInfo robot : robots){
                    if(robot.getType() == RobotType.DESIGN_SCHOOL || robot.getType() == RobotType.REFINERY){
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

                if(robots.length == 0)
                    nav.goTo(hqLoc);

//                int distanceToHq = rc.getLocation().distanceSquaredTo(hqLoc);
//                if(distanceToHq < 9 && distanceToHq > 3){
//                    int elevationChange = rc.senseElevation(rc.getLocation().add(rc.getLocation().directionTo(hqLoc))) - rc.senseElevation(rc.getLocation());
//                    if(elevationChange > 3){
//                        System.out.println("Wall found, can't move");
//                        if(rc.getDirtCarrying() == 0){
//                            Direction[] dirToDig = {rc.getLocation().directionTo(hqLoc).opposite(),rc.getLocation().directionTo(hqLoc).opposite().rotateRight(),rc.getLocation().directionTo(hqLoc).opposite().rotateLeft()};
//                            for(Direction dir : dirToDig){
//                                if(rc.canDigDirt(dir)){
//                                    rc.digDirt(dir);
//                                    System.out.println("Dug dirt");
//                                }
//                            }
//                        } else {
//                            if(rc.canDepositDirt((Direction.CENTER))){
//                                rc.depositDirt(Direction.CENTER);
//                            }
//                        }
//                    }
//                }

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

    public void decipherAllBlockChainMessages(){
        for(int [] message : teamMessages){
            // Set Hq Location
            if(message[1] == 0){
                System.out.println("Get Hq Location");
                hqLoc = new MapLocation(message[2], message[3]);
                System.out.println(hqLoc);
            }
            // Set Enemy Hq Location
            if(message[1] == 6){
                System.out.println("Got enemy location");
                enemyHqLoc = new MapLocation(message[2], message[3]);
                System.out.println(enemyHqLoc);
            }
            // Robot specific messages
            if(message[1] == 7){
                System.out.print("Recieved personal message");
            }
        }
        teamMessagesSearched = true;
    }

    public void decipherCurrentBlockChainMessage() throws GameActionException {
        ArrayList<int []> currentBlockChainMessage = comms.getPrevRoundMessages();
        for(int [] message : currentBlockChainMessage){
            if (message[1] == 6) {
                enemyHqLoc = new MapLocation(message[2], message[3]);
            }
        }
    }

}
