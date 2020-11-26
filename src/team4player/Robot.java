package team4player;
import battlecode.common.*;

import java.util.ArrayList;

public class Robot {

    int roundNumberToCallOffRushIfAttackNotStarted = 125;
    MapLocation enemyHqLoc = null;
    boolean rush = false;
    ArrayList<int []> teamMessages = new ArrayList<int []>();
    boolean teamMessagesSearched = false;
    RobotController rc;
    Communications comms;

    int turnCount = 0;

    public Robot(RobotController r) {
        this.rc = r;
        comms = new Communications(rc);
    }

    public void takeTurn() throws GameActionException {
        if(teamMessages.isEmpty()){
            comms.getAllTeamMessages(teamMessages);
        }
        turnCount += 1;
    }


    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }

    // Returns true if found robot type
    public boolean isNearbyRobot(RobotInfo[] robots, RobotType robotType){
        for(RobotInfo robot : robots){
            if(robotType == robot.type){
                return true;
            }
        }
        return false;
    }

    // Returns true if found robot type
    public boolean isNearbyRobot(RobotInfo[] robots, RobotType robotType, Team team){
        for(RobotInfo robot : robots){
            System.out.println(robot.type);
            if(team == robot.team && robotType == robot.type){
                return true;
            }
        }
        return false;
    }
}
