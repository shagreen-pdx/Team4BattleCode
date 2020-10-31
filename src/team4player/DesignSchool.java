package team4player;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class DesignSchool extends Building{

    int numLandscapers = 0;

    public DesignSchool(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException {

        numLandscapers += comms.getNewLandscaperCount();

        if(!comms.broadcastedCreation){
            comms.broadcastedCreation = comms.broadcastMessage(rc.getLocation(), 1);
        }
        if(numLandscapers < 10) {
            if (rc.isReady()) {
                for (Direction dir : Util.directions) {
                    if (tryBuild(RobotType.LANDSCAPER, dir)) {
                        System.out.println("Created a new landscaper!");
                    }
                }
            }
        }
    }
}
