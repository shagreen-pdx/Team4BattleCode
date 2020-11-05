package team4player;

import battlecode.common.*;

public class DesignSchool extends Building{

    int numLandscapers = 0;
    boolean broadcastedCreation = false;

    public DesignSchool(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException {

        numLandscapers += comms.getNewLandscaperCount();
        System.out.println(numLandscapers);

        if(!broadcastedCreation){
            broadcastedCreation = comms.broadcastMessage(rc.getLocation(), 1);
        }
        if(numLandscapers < 5) {
            if (rc.isReady()) {
                for (Direction dir : Util.directions) {
                    if (tryBuild(RobotType.LANDSCAPER, dir)) {
                        numLandscapers++;
                        System.out.println("Created a new landscaper!");
                        if(numLandscapers < 3){
                            RobotInfo[] robots = rc.senseNearbyRobots();
                            for(RobotInfo robot : robots){
                                if(robot.getType() == RobotType.LANDSCAPER){
                                    int robotId = robot.getID();
                                    comms.broadcastMessage(robotId, 5);
                                    System.out.println("Sent message to search for enemy Hq");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
