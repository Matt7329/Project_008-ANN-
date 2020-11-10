package main;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;
import org.jnativehook.mouse.NativeMouseMotionListener;

public class InputHandler implements NativeKeyListener, NativeMouseListener, NativeMouseMotionListener {
	
	private Main main;
	
	public boolean[] key = new boolean[65565];
	
	public int mousex = 0;
	public int mousey = 0;
	public int difx = 0;
	public int dify = 0; 
	
	public boolean mousepressed = false;
	long timepressed = 0;
	
	public float[] output_set;
	
	public String[] label = {"Mouse", "Left", "Right", "Up", "Down"};
	//The output set that will be parsed to the ANN:
	//[0] Mouse pressed left
	//[1] Mouse moved left
	//[2] Mouse moved right
	//[3] Mouse moved up
	//[4] Mouse moved down
	
	public InputHandler(Main main) {
		this.main = main;
		output_set = new float[main.output_width];
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.SEVERE);
		logger.setUseParentHandlers(false);
		GlobalScreen.addNativeKeyListener(this);
		GlobalScreen.addNativeMouseListener(this);
		GlobalScreen.addNativeMouseMotionListener(this);
	}
	public void nativeMouseClicked(NativeMouseEvent e) {

	}
	public void nativeMousePressed(NativeMouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
		
		if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
			mousepressed = true;			
			timepressed = System.currentTimeMillis();
		}
	}
	public void nativeMouseReleased(NativeMouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
		
		if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
			mousepressed = false;
		}
	}
	public void nativeKeyPressed(NativeKeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode > 0 && keyCode < key.length) {
			key[keyCode] = true;
		}
		if(key[NativeKeyEvent.VC_ESCAPE]) {
			System.out.println("MODE: EXIT");
			main.MODE = main.EXIT;
			main.operation = "Current Operation: "+main.getOperation();
		}
		if(key[NativeKeyEvent.VC_F1]) {
			System.out.println("MODE: IDLE");
			main.MODE = main.IDLE;
			main.operation = "Current Operation: "+main.getOperation();
			Container contentPane = main.frame.getContentPane();
	        if (contentPane instanceof JComponent) {
	            JComponent jCmpt = (JComponent) contentPane;
	            jCmpt.setBorder(BorderFactory.createLineBorder(Color.RED, 3, false));
	        }
		}
		if(key[NativeKeyEvent.VC_F2]) {
			System.out.println("MODE: TRAINING");
			main.MODE = main.TRAINING;
			main.operation = "Current Operation: "+main.getOperation();
			Container contentPane = main.frame.getContentPane();
	        if (contentPane instanceof JComponent) {
	            JComponent jCmpt = (JComponent) contentPane;
	            jCmpt.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3, false));
	        }
		}
		if(key[NativeKeyEvent.VC_F3]) {
			System.out.println("MODE: TESTING");
			main.MODE = main.TESTING;
			main.operation = "Current Operation: "+main.getOperation();
			Container contentPane = main.frame.getContentPane();
	        if (contentPane instanceof JComponent) {
	            JComponent jCmpt = (JComponent) contentPane;
	            jCmpt.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3, false));
	        }
		}
		if(key[NativeKeyEvent.VC_F4]) {
			System.out.println("MODE: SAVING");
			main.MODE = main.SAVING;
			main.operation = "Current Operation: "+main.getOperation();
			main.save();
		}
		if(key[NativeKeyEvent.VC_F6]) {
			System.out.println("MODE: RESETTING");
			main.MODE = main.RESET;
			main.operation = "Current Operation: "+main.getOperation();
			main.reset();
		}
		if(key[NativeKeyEvent.VC_F7]) {
			System.out.println("HELP!");
			System.out.println("Esc - Enables/Disables Robot Control");
			System.out.println("F1 - Exits Program");
			System.out.println("F2 - Begins Training");
			System.out.println("F3 - Runs test cycle");
			System.out.println("F4 - Saves/loads a network");
			System.out.println("F5 - Idle");
			System.out.println("F6 - Resets current network");
			System.out.println("<- and -> Can be used to increase/decrease cycle rate");
		}
		if(key[NativeKeyEvent.VC_F8]) {
			main.show_info ^= true;
			main.info.info.setVisible(main.show_info);
			System.out.println(main.show_info? "Showing Info Box" : "Hide Info Box");
		}
		if(key[NativeKeyEvent.VC_LEFT]) {
			if(main.tickrate-1 > 0) main.tickrate--;
		}
		if(key[NativeKeyEvent.VC_RIGHT]) {
			main.tickrate++;
		}
	}
	public void nativeKeyReleased(NativeKeyEvent e) {
		int keyCode = e.getKeyCode();
		
		if (keyCode > 0 && keyCode < key.length) {
			key[keyCode] = false;
		}
	}
	public void nativeKeyTyped(NativeKeyEvent e) {

	}
	public void nativeMouseDragged(NativeMouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
		int dx = mousex - main.ss.width/2;
		int dy = mousey - main.ss.height/2;
		difx += dx;
		dify += dy;
	}
	public void nativeMouseMoved(NativeMouseEvent e) {	
		mousex = e.getX();
		mousey = e.getY();	
		int dx = mousex - main.ss.width/2;
		int dy = mousey - main.ss.height/2;
		difx += dx;
		dify += dy;
	}
	public void tick() {
		output_set[1] = 0;
		output_set[2] = 0;
		output_set[3] = 0;
		output_set[4] = 0;
		
		if(mousepressed) timepressed = System.currentTimeMillis();
		float tp = 1f - (System.currentTimeMillis() - timepressed) / 500f;
		
		float dx = (float)(difx) / 2000f;
		float dy = (float)(dify) / 2000f;	
		difx = 0;
		dify = 0;
		
		if(dx > 1) dx = 1;
		if(dx < -1) dx = -1;
		if(dy > 1) dy = 1;
		if(dy < -1) dy = -1;
		
		if(tp < 0) tp = 0;
		output_set[0] = tp;
		
		if(dx < 0) output_set[1] = Math.abs(dx);
		if(dx > 0) output_set[2] = Math.abs(dx); 
		if(dy < 0) output_set[3] = Math.abs(dy); 
		if(dy > 0) output_set[4] = Math.abs(dy); 	
	}
}
