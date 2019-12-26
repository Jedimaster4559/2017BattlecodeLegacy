package legacybot.units;

import battlecode.common.*;

public abstract class Unit {

    // This is the RobotController object. You use it to perform actions from this robot,
    // and to get information on its current status.
    RobotController rc;

    public Unit(RobotController rc){
        this.rc = rc;
    }

    public abstract void run() throws GameActionException;
}
