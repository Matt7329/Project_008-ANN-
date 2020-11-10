package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class InfoBox {
	
	public JFrame info;
	BufferStrategy bs;
	Main main;
	float scale = 2;
	
	public InfoBox(Main main) {
		this.main = main;
		info = new JFrame();
		info.setUndecorated(true);
		info.setAlwaysOnTop(true);
	    info.setLocationRelativeTo(null);
	    scale = (int)(600f / (float)main.image_width);
	    info.setBounds(0, main.frame.getHeight() - (int)(main.image_height * scale), (int)(main.image_width * scale), (int)(main.image_height * scale));
	    info.setVisible(true);
	    info.createBufferStrategy(3);
	    bs = info.getBufferStrategy();
	    main.operation = "Current Operation: "+main.getOperation();
	}
	public void drawGraphics() {
    	Graphics2D g = (Graphics2D) bs.getDrawGraphics();
    	render(g);	
		if (g != null) {
			g.dispose();
		}
			
		bs.show();
	}
	public void render(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, info.getWidth(), info.getHeight());
    	//Inputs:	
    	for(int x = 0; x < main.image_width; x++) {
    		for(int y = 0; y < main.image_height; y++) {
    			float in = main.input[x + y * main.image_width];
    			g.setColor(main.im.decode(in));
    			g.fillRect((int)(x*scale), (int)(y*scale), (int)scale, (int)scale);
    		}
    	}
    	//Outputs:
    	g.setColor(Color.RED);
		g.drawString(main.operation, 10, 20);
		g.drawString("In: "+main.input.length, 10, 35);
    	g.drawString(main.image_width+"x"+main.image_height, info.getWidth() - 50, info.getHeight() - 10);
    	for(int i = 0; i < main.output.length; i++) {
    		g.drawString("["+i+"]: "+main.output[i]+" ("+main.in.label[i]+")", 10, 50 + i * 10);
    	}
    	g.setColor(Color.CYAN);
    	g.drawString(""+main.rate, info.getWidth() - 80, 20);
	}
}
