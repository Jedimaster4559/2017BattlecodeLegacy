package legacybot.units;

import battlecode.common.*;
import static legacybot.tools.UnsortedTools.*;

public class Gardener extends Unit{
    GardenerUnitState state;
    boolean hasBuiltInitialLumberjack;
    int roundsSinceLastBuild;
    int totalUnitsBuilt;
    TreeHandler trees;

    public Gardener(RobotController rc){
        super(rc);
        state = GardenerUnitState.INITIAL;
        hasBuiltInitialLumberjack = false;
        roundsSinceLastBuild = Integer.MAX_VALUE;
        totalUnitsBuilt = 0;
        trees = null;
    }

    public void run() throws GameActionException {
        System.out.println("I'm a gardener!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                //Start the round
                roundStart();

                checkState();

                switch(state){
                    case FARMING:
                        farming();
                        break;
                    case SEARCHING:
                        searching();
                        break;
                    case INITIAL:
                        initial();
                        break;
                }

                // Increment rounds since last build
                roundsSinceLastBuild++;

                // End the round
                roundEnd();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }

    public void checkState() {
        if(state == GardenerUnitState.FARMING){
            return;
        }

        if(age > 10 && hasBuiltInitialLumberjack){
            state = GardenerUnitState.SEARCHING;
        }

        if(age > 100){
            state = GardenerUnitState.SEARCHING;
        }
    }

    public void initial() throws GameActionException{
        if(!hasBuiltInitialLumberjack){
            if(tryBuild(RobotType.LUMBERJACK)){
                hasBuiltInitialLumberjack = true;
            }
        }

        if(rc.getLocation().distanceTo(spawn) < 0.01) {
            tryMove(randomDirection(), rc);
        } else if(age > 10){
            //Check if this is a farmable location
            RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy.opponent());

            MapLocation plantObstacle = null; // Basicially, anything we don't want to plant near
            for(RobotInfo robot : robots){
                if(robot.type == RobotType.ARCHON){
                    plantObstacle = robot.getLocation();
                    break;
                }

                if(robot.type == RobotType.GARDENER && rc.getLocation().distanceTo(robot.getLocation()) < 2 * (RobotType.GARDENER.bodyRadius + GameConstants.BULLET_TREE_RADIUS * 2)){
                    plantObstacle = robot.getLocation();
                }
            }

            if(plantObstacle != null){
                tryMove(plantObstacle.directionTo(rc.getLocation()), rc);
            }
        } else {
            // Continue moving away from our spawn along previous vector
            Direction direction = spawn.directionTo(rc.getLocation());
            tryMove(direction, rc);
        }
    }

    public void searching() throws GameActionException {
        // attempt to build
        if(roundsSinceLastBuild > 250 || rand.nextFloat() < .05){
            if(rand.nextBoolean()){
                tryBuild(RobotType.LUMBERJACK);
            } else {
                tryBuild(RobotType.SOLDIER);
            }
        }

        //Check if this is a farmable location
        RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy.opponent());

        MapLocation plantObstacle = null; // Basicially, anything we don't want to plant near
        for(RobotInfo robot : robots){
            if(robot.type == RobotType.ARCHON){
                plantObstacle = robot.getLocation();
                break;
            }

            if(robot.type == RobotType.GARDENER && rc.getLocation().distanceTo(robot.getLocation()) < 2 * (RobotType.GARDENER.bodyRadius + GameConstants.BULLET_TREE_RADIUS * 2)){
                plantObstacle = robot.getLocation();
            }
        }

        if(plantObstacle == null){
            state = GardenerUnitState.FARMING;
            trees = new TreeHandler(rc);
            System.out.println("Entering Farmer Mode");
            return;
        } else {
            tryMove(plantObstacle.directionTo(rc.getLocation()), rc);
        }
    }

    public void farming() throws GameActionException{
        trees.process();

        if(roundsSinceLastBuild > 200){
            tryBuild(getBuildType());
        }
    }

    public RobotType getBuildType() throws GameActionException{
        int broadcastType = rc.readBroadcast(21);

        if(broadcastType == 1){
            return null;
        } else if (broadcastType == 2){
            return RobotType.SCOUT;
        } else if (broadcastType == 3){
            return RobotType.SOLDIER;
        } else if (broadcastType == 4){
            return RobotType.LUMBERJACK;
        } else if (broadcastType == 5){
            return RobotType.TANK;
        } else {
            if(rand.nextBoolean()){
                return RobotType.LUMBERJACK;
            } else {
                return RobotType.SOLDIER;
            }
        }
    }

    public void checkIfAnnounceBuild(RobotType type) throws GameActionException{
        int broadcastType = rc.readBroadcast(21);

        if (broadcastType == 2){
            rc.broadcast(22, rc.readBroadcast(22) + 1);
            return;
        } else if (broadcastType == 3){
            rc.broadcast(22, rc.readBroadcast(22) + 1);
            return;
        } else if (broadcastType == 4){
            rc.broadcast(22, rc.readBroadcast(22) + 1);
            return;
        } else if (broadcastType == 5){
            rc.broadcast(22, rc.readBroadcast(22) + 1);
            return;
        }
    }

    /**
     * Helper to assist with the production of units
     * @param type The type of robot to create
     * @return If a unit was actually created or not
     * @throws GameActionException
     */
    private boolean tryBuild(RobotType type) throws GameActionException{
        if(type == null){
            return false;
        }

        Direction buildDirection = randomDirection();

        if(trees != null){
            buildDirection = trees.getBuildDirection();
        }

        if(buildDirection == null){
            buildDirection = randomDirection();
        }

        // TODO: This can be optimized a lot
        // Try rotating instead of random
        // Allow some type of escape cause this sometimes might break
        if(rc.getTeamBullets() >= type.bulletCost) {
            while (!rc.canBuildRobot(type, buildDirection)) {
                buildDirection = randomDirection();
                if(Clock.getBytecodesLeft() < 5000){
                    break;
                }
            }
        }


        if(rc.canBuildRobot(type, buildDirection)){
            rc.buildRobot(type, buildDirection);
            checkIfAnnounceBuild(type);
            roundsSinceLastBuild = 0;
            totalUnitsBuilt++;
            return true;
        }

        return false;
    }
}
