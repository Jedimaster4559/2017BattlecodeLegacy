package legacybot.units;

import battlecode.common.*;

import static legacybot.tools.UnsortedTools.*;

public class Soldier extends Unit {
    UnitState state;
    RobotInfo target;
    int turnsWithoutVisibleTarget;
    int primaryArchonTarget;
    MapLocation primaryArchonTargetLocation;
    int turnsInCurrentState;


    public Soldier(RobotController rc){
        super(rc);
        state = UnitState.PASSIVE;
        target = null;
        turnsWithoutVisibleTarget = 0;
        primaryArchonTarget = rand.nextInt(rc.getInitialArchonLocations(enemy).length);
        primaryArchonTargetLocation = rc.getInitialArchonLocations(enemy)[primaryArchonTarget];
        turnsInCurrentState = 0;
    }

    public void run() throws GameActionException {
        System.out.println("I'm an soldier!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Try to shake any trees that we can
                tryShake(rc);

                checkState();

                switch (state){
                    case PASSIVE:
                        passive();
                        break;
                    case AGGRESSIVE:
                        aggressive();
                        break;
                }

                // Try to shake any trees that we can
                tryShake(rc);

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
        }
    }

    public void checkState() throws GameActionException{
        if(rc.getRoundNum() - rc.readBroadcast(11) < 5){
            state = UnitState.AGGRESSIVE;
            turnsInCurrentState = 0;
            rc.broadcast(11, rc.getRoundNum());
            return;
        }

        if(state == UnitState.PASSIVE){
            if(rc.getRoundNum() > 500 && turnsInCurrentState > 300){
                state = UnitState.AGGRESSIVE;
                turnsInCurrentState = 0;
                rc.broadcast(11, rc.getRoundNum());
                return;
            }
        } else {
            if (turnsInCurrentState > 200 && turnsWithoutVisibleTarget > 200){
                state = UnitState.PASSIVE;
                turnsInCurrentState = 0;
                return;
            }
        }
    }

    public void passive() throws GameActionException{
        MapLocation myLocation = rc.getLocation();

        // See if there are any nearby enemy robots
        RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);

        // If there are some...
        if (robots.length > 0) {
            // And we have enough bullets, and haven't attacked yet this turn...
            if (rc.canFireSingleShot()) {
                // ...Then fire a bullet in the direction of the enemy.
                rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
            }
        }

        // Move randomly
        tryMove(randomDirection(), rc);

        turnsInCurrentState++;
    }

    public void aggressive() throws GameActionException {
        MapLocation myLocation = rc.getLocation();

        if(selectTarget()){
            Direction targetDirection = rc.getLocation().directionTo(target.location);
            rc.fireSingleShot(targetDirection);
        }

        if(target == null){
            tryMove(myLocation.directionTo(primaryArchonTargetLocation), rc);
        } else {
            tryMove(myLocation.directionTo(target.location), rc);
        }



        turnsInCurrentState++;
    }

    /**
     * Finds the best current Target
     * @return
     */
    public boolean selectTarget(){
        // See if there are any nearby enemy robots
        RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);

        // If there are some...
        if (robots.length > 0) {
            // And we have enough bullets, and haven't attacked yet this turn...
           target = robots[0];
           turnsWithoutVisibleTarget = 0;
           return rc.canFireSingleShot();
        } else {
            turnsWithoutVisibleTarget++;
            target = null;
            return false;
        }
    }
}
