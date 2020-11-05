package SimpleRushTestBot;

import battlecode.common.*;

import java.util.ArrayList;

public class Unit extends Robot {

    MapLocation enemyHqLoc = null;
    MapLocation hqLoc = null;
    Navigation nav;
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
        RobotInfo[] robots = rc.senseNearbyRobots();
        for(RobotInfo robot : robots){
            if(robot.type == RobotType.HQ && robot.team == rc.getTeam()){
                return false;
            }
        }
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else
            return false;
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
}
