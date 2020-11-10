package image.manipulation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class ImageManipulation {
	
	int depth = 50;
	float divisor = 2;
	int min = 20;
	
	public int[] sequence = new int[depth];
	public int width = 0;
	public int height = 0;
	
	public  void generateSequence() {
		for(float n = 0; n < depth; n++) {
			sequence[(int) n] = (int)((n/divisor) * (n/divisor)) + min;
		}
	}
	public BufferedImage ImageModify(BufferedImage image) {	
		int wi = 0;
		int he = 0;
		width = 0;
		height = 0;
		
		int xdif_min = 0;
		int ydif_min = 0;
		for(int i = 0; i < depth; i++) {
			if(wi < image.getWidth()/2) {
				xdif_min = image.getWidth()/2 - wi;
				wi += sequence[Math.abs(i)];
				width+=2;
			}
			if(he < image.getHeight()/2) {
				ydif_min = image.getHeight()/2 - he;
				he += sequence[Math.abs(i)];
				height+=2;
			}
		}
		int xdif = image.getWidth() - (2 * wi) - xdif_min;
		int ydif = image.getHeight() - (2 * he) - ydif_min;	
		int x_off = width/2;
		int y_off = height/2;
		
		BufferedImage newimg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);	
		
		int x_coord = xdif;	
		for(int x = 0; x < width; x++) {
			int w_scale = sequence[Math.abs(x - x_off)];
			int y_coord = ydif;
			for(int y = 0; y < height; y++) {		
				int h_scale = sequence[Math.abs(y - y_off)];
				int redMask = 0xFF0000, greenMask = 0xFF00, blueMask = 0xFF;
				int r = 0, g = 0, b = 0, count = 1;	
				for(int w = 0; w < w_scale; w++) {
					for(int h = 0; h < h_scale; h++) {
						int pos_x = x_coord + w;
						int pos_y = y_coord + h;
						if(pos_x >= 0 && pos_x < image.getWidth() && pos_y >= 0 && pos_y < image.getHeight()) {
							int bgr = image.getRGB(pos_x, pos_y);
							r += ((bgr & redMask) >> 16);
							g += ((bgr & greenMask) >> 8);
							b += (bgr & blueMask);
							count++;
						}
					}
				}	
				for(int w = 0; w < w_scale; w++) {
					for(int h = 0; h < h_scale; h++) {
						int pos_x = x_coord + w;
						int pos_y = y_coord + h;
						if(pos_x > 0 && pos_x < image.getWidth() && pos_y > 0 && pos_y < image.getHeight()) {
							newimg.setRGB(pos_x, pos_y, ((r/count) << 16) + ((g/count) << 8) + (b/count));
						}
					}
				}
				y_coord+=h_scale;
			}
			x_coord+=w_scale;
		}
		return newimg;
	}
	public float[] ImageModifyToPixelColor(BufferedImage image) {	
		int wi = 0;
		int he = 0;
		
		width = 0;
		height = 0;
		
		int sw = 0;
		int sh = 0;
		
		for(int i = 0; i < depth; i++) {
			int s = sequence[i];
			if(wi < image.getWidth()/2) {
				wi += s;
				width += 2;
				sw = s/2;
			}
			if(he < image.getHeight()/2) {
				he += s;
				height += 2;
				sh = s/2;
			}
		}	
		if(wi > image.getWidth()/2) {
			if(wi - sw >= image.getWidth()/2) {
				width--;
				wi -= sw;
			}
		}
		if(he > image.getHeight()/2) {
			if(he - sh >= image.getHeight()/2) {
				height--;
				he -= sh;
			}
		}
		
		int xdif = image.getWidth()/2 - wi;
		int ydif = image.getHeight()/2 - he;	

		float[] output = new float[(width * height)];	
		
		int x_coord = -xdif;	
		for(int x = 0; x < width; x++) {	
			int w_scale = sequence[Math.abs(x - width/2)];
			int y_coord = -ydif;		
			for(int y = 0; y < height; y++) {			
				int h_scale = sequence[Math.abs(y - height/2)];
				int redMask = 0xFF0000, greenMask = 0xFF00, blueMask = 0xFF;
				int r = 0, g = 0, b = 0, count = 0;	
				
				for(int w = -w_scale/2; w <= w_scale/2; w++) {
					for(int h = -h_scale/2; h <= h_scale/2; h++) {
						
						int pos_x = x_coord + w;
						int pos_y = y_coord + h;
						int off = 3;
						
						if(pos_x >= off && pos_x < image.getWidth() - off && pos_y >= off && pos_y < image.getHeight() - off) {
							int bgr = image.getRGB(pos_x, pos_y);
							r += ((bgr & redMask) >> 16);
							g += ((bgr & greenMask) >> 8);
							b += (bgr & blueMask);
							count++;
						}
					}
				}	
				if(count == 0) count = 1;
				output[x + y * width] = encode(r/count, g/count, b/count);
				y_coord += h_scale;
			}
			x_coord += w_scale;
		}
		return output;
	}
	public float encode(int r, int g, int b) {
	    return (float) (r + g * 256f + b * 256f * 256f) / (256f * 256f * 256f);
	}

	public Color decode(float f) {
		int i = (int) (f * 256f * 256f * 256f);
		
	    int b = i / (256 * 256);
	    int g = (i - b * 256 * 256) / 256;
	    int r = i - (b * 256 * 256) - (g * 256);
	    
	    return new Color(r, g, b);
	}
	private BufferedImage getImage(String filename) {
		ImageIcon in = new ImageIcon(filename);
		Image img = in.getImage();
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();
	    return bimage;
    }
	/*public static void main(String args[]) {
		new ImageManipulation().test();
	}*/
	public ImageManipulation() {
		generateSequence();
	}
	private void test() {
		BufferedImage bi = getImage("res/img/fohabpmsz2u21.jpg");
		
		long start = System.currentTimeMillis();
		bi = ImageModify(bi);
		System.out.println("Time: "+(System.currentTimeMillis()-start));
		System.out.println("W:"+width + " H:"+height+" (T:"+(width*height)+")");
		
		JFrame f = new JFrame();
		f.setTitle("Matt");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(bi.getWidth(), bi.getHeight());
		f.setLocationRelativeTo(null);
		f.setUndecorated(true);
		f.setVisible(true);
		f.createBufferStrategy(3);
		BufferStrategy bs = f.getBufferStrategy();
		
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		g.drawImage(bi, null, 0, 0);	
		g.dispose();
		bs.show();
	}
}
