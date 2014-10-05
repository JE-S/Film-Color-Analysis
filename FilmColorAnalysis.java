import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGCodec;
import org.jcodec.api.JCodecException;
import java.awt.image.BufferedImage;
import org.jcodec.api.FrameGrab;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.Color;
import java.io.File;


public class FilmColorAnalysis {
	
	// adds up red, green, blue values for each pixel on frame, divides by total num of pixels per frame
	// returns average pixel value given a frame
	public static int[] getAveRGB(BufferedImage frame) {
		int red, green, blue, num_pixels, frameHeight, frameWidth;
		red = green = blue = num_pixels = 0;
		frameHeight = frame.getHeight();
		frameWidth = frame.getWidth();
		int[] pixel = new int[3]; 
		
		for (int h = 0; h < frameHeight; h++) {
			for (int w = 0; w < frameWidth; w++) { 
				pixel = frame.getRaster().getPixel(w, h, pixel);
				red += pixel[0]; // to get total red value
				green += pixel[1]; // to get total green value
				blue += pixel[2]; // to get total blue value
				num_pixels++;
			}
		}
		
		pixel[0] = (red / num_pixels); // average red
		pixel[1] = (green / num_pixels); // average green
		pixel[2] = (blue / num_pixels); // average blue
		
		return pixel;
	}
	
	// sets up JPEG
	public static BufferedImage setupJPEG(int width, int height, int[] RGBarray) { 
		int currentIndex = 0;
		
		BufferedImage new_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		for (int h = 0; h < height; h++) { 
			for (int w = 0; w < width; w++) {
				new_image.setRGB(w, h, RGBarray[currentIndex]);
				currentIndex++;
			}
		}
		
		return new_image;
	}
	
	// encodes JPEG
	public static void encodeJPEG(BufferedImage newIm, String newImPath) throws ImageFormatException, IOException { 
		FileOutputStream fos = new FileOutputStream(newImPath);	        
	    JPEGImageEncoder jencoder = JPEGCodec.createJPEGEncoder(fos);
	    JPEGEncodeParam enParam = jencoder.getDefaultJPEGEncodeParam(newIm);	      
	    enParam.setQuality(1.0F, true);
	    jencoder.setJPEGEncodeParam(enParam);
	    jencoder.encode(newIm);
	    fos.close();
	}
	
	// main
	public static void main(String[] args) throws IOException, JCodecException {
		
		long time = System.currentTimeMillis();	
		
		String FilePath = "/Users/Example/Desktop/example_film.mp4"; // enter file path of film to analyze
		String SaveImagePath = "/Users/Example/Desktop/example_image.jpg"; // enter where final image will be save
		int start_frame = 0; // change start frame as needed
		int end_frame = 3; // change end frame as needed
		int image_height = 1; // change final image height as needed
		int image_width = 3; // change final image width as needed
		
		int[] aveRGBvalARRAY = new int[(end_frame - start_frame)];
		int array_index = 0;		
				
		for (int i = start_frame; i < end_frame; i++) { 
			System.out.println("Frame: " + (i + 1));
			BufferedImage frame = FrameGrab.getFrame(new File(FilePath), i);		
			int[] RGBave = getAveRGB(frame);
			Color myColor = new Color(RGBave[0], RGBave[1], RGBave[2]);
			int aveRGBval = myColor.getRGB();
			aveRGBvalARRAY[array_index] = aveRGBval;
			array_index++;
		}
		
		BufferedImage image = setupJPEG(image_width, image_height, aveRGBvalARRAY);	
		encodeJPEG(image, SaveImagePath);		
		
		System.out.println("Time Used:" + (System.currentTimeMillis() - time) + " Milliseconds");		
	}
}
