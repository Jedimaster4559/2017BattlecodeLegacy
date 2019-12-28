package legacybot.units;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Random;

public class TreeHandler {
    RobotController rc;
    Direction production;
    ArrayList<Direction> desiredLocations;
    int stepCounter;
    boolean hasPlanted;

    public TreeHandler(RobotController rc){
        this.rc = rc;
        desiredLocations = new ArrayList<Direction>();
        initializeLocations();
        stepCounter = 0;
        hasPlanted = false;
    }

    private void initializeLocations(){
        // Decide which Direction production will happen in
        Random rand = new Random();
        int productionSpot = rand.nextInt(6);

        //Set locations
        Direction currentDirection = Direction.WEST;
        float offset = 2 * (float)(Math.PI / 6);
        for(int i = 0; i < 6; i++){
            if(i == productionSpot){
                production = currentDirection;
            } else {
                desiredLocations.add(currentDirection);
            }
            currentDirection.rotateRightRads(offset);
        }
    }

    public void process() throws GameActionException{
        tryPlant();
        tryWater();

        stepCounter++;

        if(stepCounter >= desiredLocations.size()){
            stepCounter = 0;
        }

    }

    public Direction getBuildDirection(){
        return production;
    }

    private void tryPlant() throws GameActionException {
        Random rand = new Random();
        int productionSpot = rand.nextInt(5);

        Direction initial = Direction.WEST;
        float offset = 2 * ((float)Math.PI / 6);
        for(int i = 0; i < 6; i++){
            //String degreeDebug = "Attempting to Plant in this Direction:" + Math.round(initial.getAngleDegrees()) + "\nStep: " + i;
            //System.out.println(degreeDebug);
            if(i != productionSpot){
                if(rc.canPlantTree(initial)){
                    rc.plantTree(initial);
                    System.out.println("Planting a Tree!");
                }
            } else {
                production = initial;
            }
            initial = initial.rotateRightRads(offset);
        }
    }

    private void tryWater() throws GameActionException {
        TreeInfo[] trees = rc.senseNearbyTrees(RobotType.GARDENER.bodyRadius + (2 * GameConstants.BULLET_TREE_RADIUS), rc.getTeam());

        if(trees.length <= 0) return;

        int waterIndex = rc.getRoundNum() % trees.length;

        if(rc.canWater(trees[waterIndex].location)){
            rc.water(trees[waterIndex].location);
        }
    }
}
