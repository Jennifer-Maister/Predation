package Predation;
import sim.engine.*;
import sim.display.*;
import sim.portrayal.grid.*;
import javax.swing.*;
import sim.portrayal.simple.*;
import java.awt.Color;

public class EnvironmentWithUI extends GUIState{
	public Display2D display;
	public JFrame displayFrame;
	SparseGridPortrayal2D enviPortrayal = new SparseGridPortrayal2D();
	
	/* The environment is displayed as a black field.
	 * The prey is displayed as blue circles and the predator is displayed as red squares.
	 * 
	 * To display the line graph with population versus time step data, open the model tab of the console. Click the magnifying glass
	 * beside the labels of the parameter of interest and click "Chart". Provide the chart title and axis labels.
	 */
	
	public EnvironmentWithUI () {
		super(new Environment (System.currentTimeMillis()));
	}
	
	public static void main(String[] args) {
		EnvironmentWithUI ex = new EnvironmentWithUI();
		Console c = new Console(ex);
		c.setVisible(true);
		System.out.println("Start Simulation");
	}
	
	public void quit() {
		super.quit();
		if (displayFrame!=null) displayFrame.dispose();
		displayFrame = null;
		display = null;
	}

	public void start() {
		super.start();
		setupPortrayals();
	}

	public void load(SimState state) {
		super.load(state);
		setupPortrayals();
	}

	public void setupPortrayals() {
        Environment se = (Environment)state;
		enviPortrayal.setField(se.grid);
		enviPortrayal.setPortrayalForClass(Prey.class, new OvalPortrayal2D(Color.blue));
		enviPortrayal.setPortrayalForClass(Predator.class, new RectanglePortrayal2D(Color.red));
		display.reset();
		display.setBackdrop(Color.black);
		display.repaint();
	}

	public void init(Controller c){
		super.init(c);
		display = new Display2D(600,600,this, 0); //added 0 para mawala ang error
		displayFrame = display.createFrame();
		displayFrame.setTitle("Predation");
		c.registerFrame(displayFrame);
		displayFrame.setVisible(true);
		display.attach(enviPortrayal,"Predation");
	}

	public Object getSimulationInspectedObject() { //method for accessing global inspectors under the "Model" tab of the console
		return state;
	}
}
