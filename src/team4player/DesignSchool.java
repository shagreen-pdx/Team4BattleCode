package team4player;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class DesignSchool extends Building{

    public DesignSchool(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException {
        if(!comms.broadcastedCreation){
            comms.broadcastDesignSchoolCreation(rc.getLocation());
        }
        if (rc.isReady()) {
            for (Direction dir : Util.directions) {
                if (tryBuild(RobotType.LANDSCAPER, dir)) {
                    System.out.println("Created a new landscaper!");
//                    ++numLandScapers;
//                    System.out.println("Number of landscapers:" + numLandScapers);
                }
            }
        }
    }
}
