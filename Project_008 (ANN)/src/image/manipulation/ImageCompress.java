package image.manipulation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class ImageCompress {

	public static BufferedImage ImageToImageCompression(BufferedImage image, float compression_x, float compression_y) {	
		int width = (int) Math.ceil(image.getWidth() * compression_x);
		int height = (int) Math.ceil(image.getHeight() * compression_y);
		float offset_x = 0.5f / compression_x;
		float offset_y = 0.5f / compression_y;
		BufferedImage newimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int w = 0; w < width; w++) {
			for(int h = 0; h < height; h++) {
				int image_x = (int) (((float)w / compression_x) + offset_x);
				int image_y = (int) (((float)h / compression_y) + offset_y);	
				int redMask = 0xFF0000, greenMask = 0xFF00, blueMask = 0xFF;
				int r = 0, g = 0, b = 0, count = 0;			
				for(int w2 = (int)-offset_x; w2 < offset_x; w2++) {
					for(int h2 = (int)-offset_y; h2 < offset_y; h2++) {
						int x = image_x + w2;
						int y = image_y + h2;
						if(x < image.getWidth() && y < image.getHeight()) {
							int bgr = image.getRGB(x, y);
							r += ((bgr & redMask) >> 16);
							g += ((bgr & greenMask) >> 8);
							b += (bgr & blueMask);
							count++;
						}
					}
				}
				newimg.setRGB(w, h, ((r/count) << 16) + ((g/count) << 8) + (b/count));
			}
		}
		return newimg;
	}
	public static int[] ImageToPixelCompression(BufferedImage image, float compression_x, float compression_y) {	
		int width = (int) Math.ceil(image.getWidth() * compression_x);
		int height = (int) Math.ceil(image.getHeight() * compression_y);
		float offset_x = 0.5f / compression_x;
		float offset_y = 0.5f / compression_y;
		int[] output = new int[(int) (width * height)];	
		for(int w = 0; w < width; w++) {
			for(int h = 0; h < height; h++) {
				int image_x = (int) (((float)w / compression_x) + offset_x);
				int image_y = (int) (((float)h / compression_y) + offset_y);	
				int redMask = 0xFF0000, greenMask = 0xFF00, blueMask = 0xFF;
				int r = 0, g = 0, b = 0, count = 0;			
				for(int w2 = (int)-offset_x; w2 < offset_x; w2++) {
					for(int h2 = (int)-offset_y; h2 < offset_y; h2++) {
						int x = image_x + w2;
						int y = image_y + h2;
						if(x < image.getWidth() && y < image.getHeight()) {
							int bgr = image.getRGB(x, y);
							r += ((bgr & redMask) >> 16);
							g += ((bgr & greenMask) >> 8);
							b += (bgr & blueMask);
							count++;
						}
					}
				}
				output[w + h * width] = ((r/count) << 16) + ((g/count) << 8) + (b/count);
			}
		}
		return output;
	}
	private static BufferedImage getImage(String filename) {
		ImageIcon in = new ImageIcon(filename);
		Image img = in.getImage();
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();
	    return bimage;
    }
	public static void main(String args[]) {
		BufferedImage bi = getImage("res/img/fohabpmsz2u21.jpg");
		
		long start = System.currentTimeMillis();
		float x = 0.333f;
		float y = 0.333f;
		bi = ImageToImageCompression(bi, x, y);
		System.out.println("Time: "+(System.currentTimeMillis()-start));
		System.out.println("W:"+bi.getWidth() + " H:"+bi.getHeight()+" (T:"+(bi.getWidth()*bi.getHeight())+")");
		
		JFrame f = new JFrame();
		f.setTitle("Matt");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize((int)(bi.getWidth()/x), (int)(bi.getHeight()/y));
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
		g.scale(1f/x, 1f/y);
		g.drawImage(bi, null, 0, 0);	
		g.dispose();
		bs.show();
	}
}
