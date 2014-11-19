package Predation;
import sim.engine.*;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;

//It's Egita again

public class Environment extends SimState {
	public SparseGrid2D grid; //the grid wraps around
	public int gridWidth = 100;
	public int gridHeight = 100;
	public int numPrey = 800;
	public int numPred = 200;
	public int preyBreedTime = 5;
	public int predBreedTime = 40;
	public int predStarveTime = 12;
	public int preyPop;
	public int predPop;
	private static boolean isCheckpointing = true; //checkpointing can be turned off by changing the value of isCheckpointing to false
	
	/* The model first creates a sparse grid 2D environment and then seeds the prey first then the predators.
	 * Both prey and predators are scheduled to update every time step and ordering is not specified.
	 */
	
	public Environment (long seed) { 
		super(seed);
	}	
	
	public void start(){
		super.start();
		preyPop = 0;
		predPop = 0;
		grid = new SparseGrid2D(gridWidth, gridHeight);
		for (int i=0;i<numPrey;i++){
			seedPrey();
		}
		for (int i=0;i<numPred;i++){
			seedPred();
		}
	}
	
	public static void main(String[] args){ 
		if(isCheckpointing){
			Environment envi = null;
			for(int x=0;x<args.length-1;x++)
		        if (args[x].equals("-fromcheckpoint"))
		            {
		            SimState state = SimState.readFromCheckpoint(new java.io.File(args[x+1]));
		            if (state == null)   //if there was an error -- quit (error will be displayed)
		                System.exit(1);
		            else if (!(state instanceof Environment))  //if wrong simulation stored in the file
		                {
		                System.out.println("Checkpoint contains some other simulation: " + state);
		                System.exit(1);
		                }
		            else //when all else ok, run simulation
		                envi = (Environment)state;
		            }
			//if a new run
			if (envi == null) { //no checkpoint requested
				envi = new Environment(System.currentTimeMillis());
				envi.start(); 
			}
			long steps;
			do {
				if (!envi.schedule.step(envi)) break;
				steps = envi.schedule.getSteps();
				if (steps%500 == 0) { //checkpoints every 500 time steps and prints the prey and predator population
					System.out.println ("Steps: " + steps + "  Time:" + envi.schedule.time() );
					System.out.println ("Prey: " + envi.preyPop + " Pred: " + envi.predPop);
					String s = steps + ".Environment.checkpoint";
					System.out.println("Checkpointing to file: " + s);
					envi.writeToCheckpoint(new java.io.File(s));
				} 
			} while (steps < 4000);
			envi.finish(); //ends simulation
			System.exit(0); //finishes threads
		}
		else {
			Environment envi = new Environment(System.currentTimeMillis());
			envi.start();
			long steps;
			do {
				envi.schedule.step(envi);
				steps = envi.schedule.getSteps();
				if (steps%50 == 0) { //during every 50 time steps, print the prey and predator population
					System.out.println ("Steps: " + steps + "  Time:" + envi.schedule.time() );
					System.out.println ("Prey: " + envi.preyPop + " Pred: " + envi.predPop);
					System.out.println();
				}
			} while (steps <2000);
			envi.finish(); //ends simulation
			System.exit(0); //finishes threads
		}
	}
		
	public Prey seedPrey (){
		int x = random.nextInt(gridWidth);
		int y = random.nextInt(gridHeight);
		Prey prey = new Prey();
		if (grid.numObjectsAtLocation(x, y) == 0) {
			grid.setObjectLocation(prey, x, y);
			prey.stopper = schedule.scheduleRepeating(prey); 
		  	prey.setXY(x, y);
		  	preyPop++;
		}
		else {
			seedPrey();
		}
		return prey;
	}
	
	public Predator seedPred (){
		int x = random.nextInt(gridWidth);
		int y = random.nextInt(gridHeight);
		Predator pred = new Predator();
		if (grid.numObjectsAtLocation(x, y) == 0) {
			grid.setObjectLocation(pred, x, y);
			pred.stopper = schedule.scheduleRepeating(pred);
		  	pred.setXY(x, y);
		  	predPop++;
		}
		else {
			seedPred();
		}
		return pred;
	}
	
	public int getpreyPop() { return preyPop; }
	public void setpreyPop(int i) { if (i>0) preyPop = i; }
	
	public int getpredPop() { return predPop; }
	public void setpredPop(int i) { if (i>0) predPop = i; }
	
	public int getgridWidth(){ return gridWidth; }
	public void setgridWidth(int i){ if(i>0) gridWidth = i;	}
	
	public int getgridHeight(){ return gridHeight; }
	public void setgridHeight(int i){ if(i>0) gridHeight = i; }
	
	public int getnumPrey(){ return numPrey; }
	public void setnumPrey(int i){ if(i>=0) numPrey = i; }
	
	public int getpreyBreedTime(){ return preyBreedTime; }
	public void setpreyBreedTime(int i){ if (i>0) preyBreedTime = i; }
	
	public int getnumPred(){ return numPred; }
	public void setnumPred(int i){ if(i>=0) numPred = i; }
	
	public int getpredBreedTime(){ return predBreedTime; }
	public void setpredBreedTime(int i){ if (i>0) predBreedTime = i; }
		
	public int getpredStarveTime() { return predStarveTime; }
	public void setpredStarveTime (int i) { if (i>=0) predStarveTime = i; }
}
