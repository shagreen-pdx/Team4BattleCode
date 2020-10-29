package team4player;
import battlecode.common.*;

import java.util.ArrayList;
import java.util.Map;

import static java.lang.Math.min;

public strictfp class RobotPlayer {
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        Robot me = null;

        // Here, we've separated the controls into a different method for each RobotType.
        // You can add the missing ones or rewrite this into your own control structure.
        switch (rc.getType()) {
            case HQ:                 me = new HQ(rc);                break;
            case MINER:              me = new Miner(rc);             break;
            case REFINERY:           me = new Refinery(rc);          break;
            case VAPORATOR:          me = new Vaporator(rc);         break;
            case DESIGN_SCHOOL:      me = new DesignSchool(rc);      break;
            case FULFILLMENT_CENTER: me = new FulfillmentCenter(rc); break;
            case LANDSCAPER:         me = new Landscaper(rc);        break;
            case DELIVERY_DRONE:     me = new DeliveryDrone(rc);     break;
            case NET_GUN:            me = new NetGun(rc);            break;
        }

        while (true) {

            try {
                me.takeTurn();

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();
            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }

}


