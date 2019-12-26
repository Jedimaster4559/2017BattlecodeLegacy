package legacybot.units;

import battlecode.common.*;

import java.util.Map;

import static legacybot.tools.UnsortedTools.*;

public class Lumberjack extends Unit {

    UnitState state;
    RobotInfo target;
    int turnsWithoutVisibleTarget;
    int primaryArchonTarget;
    MapLocation primaryArchonTargetLocation;
    int turnsInCurrentState;

    public Lumberjack(RobotController rc){
        super(rc);
        this.state = UnitState.PASSIVE;
        target = null;
        turnsWithoutVisibleTarget = 0;
        primaryArchonTarget = rand.nextInt(rc.getInitialArchonLocations(enemy).length);
        primaryArchonTargetLocation = rc.getInitialArchonLocations(enemy)[primaryArchonTarget];
        turnsInCurrentState = 0;
    }

    public void run() throws GameActionException {
        System.out.println("I'm a lumberjack!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                //Start the round
                roundStart();

                checkState();

                switch (state){
                    case PASSIVE:
                        passive();
                        break;
                    case AGGRESSIVE:
                        aggressive();
                        break;
                }

                // End the round
                roundEnd();

            } catch (Exception e) {
                System.out.println("Lumberjack Exception");
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
            return;
        } else {
            if (turnsInCurrentState > 200 && turnsWithoutVisibleTarget > 200){
                state = UnitState.PASSIVE;
                turnsInCurrentState = 0;
                return;
            }
        }
    }

    public void passive() throws GameActionException{
        // Try to shake any trees that we can
        tryShake(rc);

        // Try to strike if we can
        if(shouldStrike()){
            rc.strike();
        }

        // Try to chop if we can
        tryChop();

        // No close robots, so search for robots within sight radius
        RobotInfo[] robots = rc.senseNearbyRobots(-1,enemy);
        TreeInfo[] neutralTrees = rc.senseNearbyTrees(-1, Team.NEUTRAL);
        TreeInfo[] enemyTrees = rc.senseNearbyTrees(-1, enemy);

        MapLocation myLocation = rc.getLocation();

        // If there is a robot, move towards it
        if(robots.length > 0) {
            MapLocation enemyLocation = robots[0].getLocation();
            Direction toEnemy = myLocation.directionTo(enemyLocation);

            tryMove(toEnemy, rc);
        } else if(enemyTrees.length > 0){
            MapLocation treeLocation = enemyTrees[0].getLocation();
            Direction toTree = myLocation.directionTo(treeLocation);

            tryMove(toTree, rc);
        } else if(neutralTrees.length > 0){
            MapLocation treeLocation = neutralTrees[0].getLocation();
            Direction toTree = myLocation.directionTo(treeLocation);

            tryMove(toTree, rc);
        } else {
            tryMove(randomDirection(), rc);
        }

        // Try to shake any trees that we can
        tryShake(rc);

        // Try to strike if we can
        if(shouldStrike()){
            rc.strike();
        }

        // Try to chop if we can
        tryChop();

    }

    public void aggressive() throws GameActionException {


        // Try to strike if we can
        if(shouldStrike()){
            rc.strike();
        }

        // Try to chop if we can
        tryChop();

        // No close robots, so search for robots within sight radius
        RobotInfo[] robots = rc.senseNearbyRobots(-1,enemy);

        MapLocation myLocation = rc.getLocation();

        // If there is a robot, move towards it
        if(robots.length > 0) {
            MapLocation enemyLocation = robots[0].getLocation();
            Direction toEnemy = myLocation.directionTo(enemyLocation);

            tryMove(toEnemy, rc);
        } else {
            tryMove(myLocation.directionTo(primaryArchonTargetLocation), rc);
        }

        // Try to shake any trees that we can
        tryShake(rc);

        // Try to strike if we can
        if(shouldStrike()){
            rc.strike();
        }

        // Try to chop if we can
        tryChop();
    }

    public boolean shouldStrike(){
        // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius+ GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy);
        RobotInfo[] friendlyRobots = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius + GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy.opponent());

        if(!rc.hasAttacked() && enemyRobots.length > 0 && enemyRobots.length > friendlyRobots.length) {
            // Use strike() to hit all nearby robots!
            return true;
        } else {
            return false;
        }
    }

    public void tryChop() throws GameActionException {
        TreeInfo[] neutralTrees = rc.senseNearbyTrees(RobotType.LUMBERJACK.bodyRadius + 1, Team.NEUTRAL);
        TreeInfo[] enemyTrees = rc.senseNearbyTrees(RobotType.LUMBERJACK.bodyRadius + 1, enemy);

        if(enemyTrees.length > 0 && rc.canChop(enemyTrees[0].getLocation())){
            rc.chop(enemyTrees[0].getLocation());
        }

        if(neutralTrees.length > 0 && rc.canChop(neutralTrees[0].getLocation())){
            rc.chop(neutralTrees[0].getLocation());
        }
    }
}
