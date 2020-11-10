package main;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;

import image.manipulation.ImageManipulation;
import net.ArtificialNeuralNetwork;

public class Main {
	
	ArtificialNeuralNetwork ANN;
	Robot r;
	ImageManipulation im;
	Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
	JFrame frame;
	InputHandler in;
	InfoBox info;
	Rectangle screenshotbox = new Rectangle(3, 3, ss.width - 3, ss.height - 3);
	
	int previous_mx;
	int previous_my;
	
	//ANN:
	int image_width = 68;
	int image_height = 54;
	int input_width = image_width * image_height;
	
	int output_width = 5;
	int width = 2000;
	int depth = 3;
	float[] input = new float[input_width];
	float[] output = new float[output_width];
	
	//MODE
	String operation = "";
	public int EXIT = 0;
	public int TRAINING = 1;
	public int TESTING = 2;
	public int SAVING = 3;
	public int IDLE = 4;
	public int RESET = 5;
	public int MODE = IDLE;
	
	public int tickrate = 10;
	public int TRAINING_ITERATIONS = 0;
	public int TESTING_ITERATIONS = 0;
	
	boolean show_info = true;
	float rate = 0;
	long lasttime = System.nanoTime();
	
	public Main () {
		init();
		reset();
		info = new InfoBox(this);
		run();
	}
	private void init() {
		frame = new JFrame("");
	    Container contentPane = frame.getContentPane();
        if (contentPane instanceof JComponent) {
            JComponent jCmpt = (JComponent) contentPane;
            jCmpt.setBorder(BorderFactory.createLineBorder(Color.RED, 3, false));
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    frame.setUndecorated(true);
	    frame.setAlwaysOnTop(true);
	    frame.setBackground(new Color(0, 0, 0, 0));
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	    
		try { r = new Robot(); } catch (AWTException e) {
			e.printStackTrace();
		}
		im = new ImageManipulation();
		in = new InputHandler(this);
	}
	public String getOperation() {
		String s;
		if(MODE == EXIT) {
			s = "EXIT";
		}else if(MODE == TRAINING) {
			s = "TRAINING";
		}else if(MODE == TESTING) {
			s = "TESTING";
		}else if(MODE == SAVING) {
			s = "SAVING";
		}else if(MODE == IDLE) {
			s = "IDLE";
		}else if(MODE == RESET) {
			s = "RESETTING";
		}else {
			return "Unknown";
		}
		return s;
	}
	public void reset() {
		//System.out.println("Creating New ANN ...");
		BufferedImage screenshot_init = r.createScreenCapture(screenshotbox);	
		input = im.ImageModifyToPixelColor(screenshot_init);
		image_width = im.width;
		image_height = im.height;
		input_width = input.length;
		//System.out.println("Inputs: "+input_width);
		//System.out.println("Width: "+width+" Depth: "+depth+" (T:"+(width*width*depth + (input_width * width) + (output_width * width))+")");
		ANN = new ArtificialNeuralNetwork(input_width, output_width, width, depth);
	}
	public void tick() {
		in.tick();
		if(MODE == EXIT) {
			System.exit(0);
		}
		if(MODE == TRAINING) {
			BufferedImage screenshot = r.createScreenCapture(screenshotbox);	
			input = im.ImageModifyToPixelColor(screenshot);
			image_width = im.width;
			image_height = im.height;
			output = in.output_set;		
			ANN.train(input, output);	
			TRAINING_ITERATIONS++;
			operation = "Current Operation: "+getOperation()+" "+TRAINING_ITERATIONS;
		}
		if(MODE == TESTING) {
			BufferedImage screenshot = r.createScreenCapture(screenshotbox);	
			input = im.ImageModifyToPixelColor(screenshot);
			image_width = im.width;
			image_height = im.height;
			output = ANN.forwardPass(input);
			passOutputToRobot(output);
			TESTING_ITERATIONS++;
			operation = "Current Operation: "+getOperation()+" "+TESTING_ITERATIONS;
		}
		if(show_info) info.drawGraphics();
		rate = 1e9f/(System.nanoTime() - lasttime);
		lasttime  = System.nanoTime();
	}
	private void passOutputToRobot(float[] output) {
		float threshold = 0.5f;
		
		if(output[0] > threshold) 
			r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		else 
			r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		int dx = (int) (((output[2] > output[1])? output[2] : -output[1]) * 2000f);
		int dy = (int) (((output[4] > output[3])? output[4] : -output[3]) * 2000f);
		
		if(Math.abs(dx) + Math.abs(dy) > 0) {
			r.mouseMove(dx + in.mousex, dy + in.mousey);
		}	
	}
	public void save() {
		ANN.save();
	}
	public void run() {	
		//System.out.println("Starting ... IDLE");
		System.out.println("Press F7 For Help");
		
		double unprocessedSeconds = 0;
		long previousTime = System.nanoTime();
		double secondsPerTick = 1.0 / tickrate;
		
		while (true) {
			long currentTime = System.nanoTime();
			long passedTime = currentTime - previousTime;
			previousTime = currentTime;
			unprocessedSeconds += passedTime / 1000000000.0;
			while (unprocessedSeconds > secondsPerTick) {
				unprocessedSeconds -= secondsPerTick;
				secondsPerTick = 1.0 / tickrate;
				tick();
			}
		}
	}
	public static void main(String args[]) {
		new Main();
	}
}
