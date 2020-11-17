package team4player;

import battlecode.common.*;
import com.sun.javafx.collections.MappingChange;

import java.util.ArrayList;

public class Unit extends Robot{
    MapLocation enemyHqSymetric;
    MapLocation enemyHqHorizontal;
    MapLocation enemyHqVertical;

    MapLocation enemyHqLoc = null;
    MapLocation hqLoc = null;
    Navigation nav;
    ArrayList<MapLocation> floodedLocations = new ArrayList<MapLocation>();
    ArrayList<MapLocation> posEnemyHqLoc = new ArrayList<MapLocation>();

    public Unit(RobotController r) {
        super(r);
        nav = new Navigation(rc);
    }

    public void takeTurn() throws GameActionException {
        if(nav.prevLocations.size() > 7){
            nav.prevLocations.remove(0);
        }

        nav.prevLocations.add(rc.getLocation());
        super.takeTurn();
    }

    boolean tryBuildBuilding(RobotType type, Direction dir) throws GameActionException {


        if(rc.getLocation().add(dir).isWithinDistanceSquared(hqLoc,9)){
            return false;
        }

        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else
            return false;
    }

    public void calcPosEnemyHqLoc(){
        if(hqLoc != null){
            enemyHqSymetric = new MapLocation((nav.mapWidth - 1 - hqLoc.x),(nav.mapHeight - 1 - hqLoc.y));
            enemyHqHorizontal = new MapLocation((nav.mapWidth - 1 - hqLoc.x),(hqLoc.y));
            enemyHqVertical = new MapLocation((hqLoc.x),(nav.mapHeight - 1 - hqLoc.y));
            if(nav.mapHeight == nav.mapWidth){
                posEnemyHqLoc.add(enemyHqSymetric);
                posEnemyHqLoc.add(enemyHqHorizontal);
                posEnemyHqLoc.add(enemyHqVertical);
            } else {
                posEnemyHqLoc.add(enemyHqHorizontal);
                posEnemyHqLoc.add(enemyHqSymetric);
                posEnemyHqLoc.add(enemyHqVertical);
            }

        }
    }

    public boolean isPickable(RobotInfo robot){
        return robot.getType() == RobotType.MINER || robot.getType() == RobotType.LANDSCAPER;
    }

    // Given an array of locations, return the closest location
    public MapLocation getClosestLoc(ArrayList<MapLocation> locations) {
        MapLocation currentLoc = rc.getLocation();

        int closestDistance = 9999;
        MapLocation closestLoc = null;

        for(MapLocation loc : locations){
            int distanceToLoc = currentLoc.distanceSquaredTo(loc);
            if( distanceToLoc < closestDistance){
                closestDistance = distanceToLoc;
                closestLoc = loc;
            }
        }
        return closestLoc;
    }
}
