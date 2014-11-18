package Predation;

import sim.engine.*;
import sim.util.*;

public  class Prey implements Steppable{ 
	int x,y;
	int nextx, nexty;
	int breedCounter = 0;
	
	public Stoppable stopper = null;
	
	/* A prey moves randomly to any empty horizontally or vertically adjacent cell.
	 * If the breed limit is reached and there is an empty location around the current prey, a new prey is created.
	 */
	public void step (SimState state){
		Environment envi = (Environment) state;
		move(state, envi);
		if (breedCounter >= envi.preyBreedTime) { 
			reproduce(state, envi); 
		}
	}
	
	public void move (SimState state, Environment envi) {
		Bag neighbors = envi.grid.getNeighborsHamiltonianDistance(x, y, 1, true, null, null, null);
		neighbors.remove(this);
		if (neighbors.numObjs >= 4){ //if there are no empty spaces
			envi.grid.setObjectLocation(this, x, y); //stay on same location
			breedCounter++;
		}
		else {
			int k = envi.random.nextInt(4);
			if (k == 0) { nextx = envi.grid.stx(this.x-1); nexty = this.y;}
			if (k == 1) { nextx = envi.grid.stx(this.x+1); nexty = this.y;}
			if (k == 2) { nexty = envi.grid.sty(this.y-1); nextx = this.x;}
			if (k == 3) { nexty = envi.grid.sty(this.y+1); nextx = this.x;}
			if (envi.grid.numObjectsAtLocation(nextx, nexty) == 0) {
				envi.grid.setObjectLocation(this, nextx, nexty);
				this.setXY(nextx, nexty);
				breedCounter++;
			}
			else {
				move(state, envi); 
			}
		}
	}
	
	public void reproduce(SimState state, Environment envi) {
		Bag neighbors = envi.grid.getNeighborsHamiltonianDistance(x, y, 1, true, null, null, null);
		neighbors.remove(this);
		if (neighbors.numObjs < 4) {
			int k = envi.random.nextInt(4);
			if (k == 0) { nextx = envi.grid.stx(this.x-1); nexty = this.y;}
			if (k == 1) { nextx = envi.grid.stx(this.x+1); nexty = this.y;}
			if (k == 2) { nexty = envi.grid.sty(this.y-1); nextx = this.x;}
			if (k == 3) { nexty = envi.grid.sty(this.y+1); nextx = this.x;}
			if (envi.grid.numObjectsAtLocation(nextx, nexty) == 0) {
				Prey prey = new Prey();
				envi.grid.setObjectLocation(prey, nextx, nexty);
				prey.stopper = envi.schedule.scheduleRepeating(prey);
				prey.setXY(nextx, nexty);
				envi.preyPop++;
				breedCounter = 0;
			}
			else { reproduce(state, envi); }
		}
	}
		
	public void setXY (int x, int y){
		this.x = x;
		this.y = y;
	}
}
