package team4player;

import battlecode.common.*;

public class Unit extends Robot{

    MapLocation enemyHqLoc = null;
    MapLocation hqLoc = null;
    Navigation nav;

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
        findHQ();
        System.out.println("I'm a Unit");
    }

    public void findHQ() throws GameActionException{
        if(hqLoc == null){
            RobotInfo[] robots = rc.senseNearbyRobots();
            for(RobotInfo robot : robots){
                if(robot.type == RobotType.HQ && robot.team == rc.getTeam()){
                    hqLoc = robot.location;
                }
            }
        }

        if(hqLoc == null){
            hqLoc = comms.getHqFromBlockchain();
        }
    }
}
