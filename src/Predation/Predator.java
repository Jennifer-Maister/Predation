package Predation;
import sim.engine.*;
import sim.util.*;

public class Predator implements Steppable{
	int x,y;
	int nextx, nexty;
	int breedCounter = 0;
	int starvationCounter = 0;
	boolean isHungry;
	
	public Stoppable stopper = null;
	
	/* A predator first tries to eat by inspecting the horizontally or vertically adjacent cells for prey and if it finds 
	 * at least one, it randomly chooses a prey, eats it and moves to the vacated location.
	 * After the scheduled eat method, if the starvation limit is reached, the predator dies.
	 * If it is still alive and no prey was eaten, it moves randomly to any empty horizontally or vertically adjacent cell.
	 * If the breed limit is reached and there is an empty location around the current predator, a new predator is created.
	 */
	public void step (SimState state){
		Environment envi = (Environment) state;
		eat(state, envi);
		if (starvationCounter == envi.predStarveTime){ 
			Predator pred = this;
			if (stopper != null) stopper.stop(); //stops individual from moving
			envi.grid.remove(pred); //erases individual from grid
			envi.predPop--;
			return;
		}
		if (isHungry == true) {
			move(state, envi);
		}
		if (breedCounter >= envi.predBreedTime) { 
			reproduce(state, envi); 
		}
		isHungry = true;
	}
	
	public void move (SimState state, Environment envi) {
		Bag neighbors = envi.grid.getNeighborsHamiltonianDistance(x, y, 1, true, null, null, null);
		neighbors.remove(this);
		if (neighbors.numObjs >= 4){ 
			envi.grid.setObjectLocation(this, x, y); 
			breedCounter++;
			starvationCounter++;
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
				starvationCounter++;
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
				Predator pred = new Predator();
				envi.grid.setObjectLocation(pred, nextx, nexty);
				pred.stopper = envi.schedule.scheduleRepeating(pred);
				pred.setXY(nextx, nexty);
				envi.predPop++;
				breedCounter = 0;
			}
			else { reproduce(state, envi); }
		}
	}
	
	public void eat (SimState state, Environment envi) {
		Bag indvs = envi.grid.getNeighborsHamiltonianDistance(x, y, 1, true, null , null, null);
		indvs.remove(this);
		if (indvs != null && indvs.numObjs > 0) {
			for (int i=0; i<indvs.numObjs; i++) {
				int x = envi.random.nextInt(indvs.numObjs);
				if (indvs.objs[x] instanceof Prey) {
					Prey p = (Prey) indvs.objs[x];
					nextx = p.x; nexty = p.y;
					envi.grid.remove(p);
					if (stopper != null) p.stopper.stop();
					starvationCounter = 0;
					envi.preyPop--;
					isHungry = false;
					envi.grid.setObjectLocation(this, nextx, nexty);
					this.setXY(nextx, nexty);
					breedCounter++; 
					break;
				}
			}
		}
	}
	
	public void setXY (int x, int y){
		this.x = x;
		this.y = y;
	}
}
