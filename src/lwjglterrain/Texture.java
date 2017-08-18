package lwjglterrain;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;



public class Texture {
    
    private int id, width = 1, height = 1, sampler;
    
    public Texture(String filename){
        BufferedImage bi;
        
        try{
            bi = ImageIO.read(new BufferedInputStream(getClass().getResourceAsStream("/" + filename)));
            width = bi.getWidth();
            height = bi.getHeight();
            
            
            int[] pixels_raw = bi.getRGB(0, 0, width, height, null, 0, width);
            
            ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
            
            for(int i = 0; i < width; i++){
                for(int j = 0; j < height; j++){
                    int pixel = pixels_raw[i * height + j];
                    pixels.put((byte)((pixel >> 16) & 0xFF));   //RED
                    pixels.put((byte)((pixel >> 8) & 0xFF));    //GREEN
                    pixels.put((byte)(pixel & 0xFF));           //BLUE
                    pixels.put((byte)((pixel >> 24) & 0xFF));   //ALPHA
                }
            }
            
            pixels.flip();
            
            id = glGenTextures();
            
            glBindTexture(GL_TEXTURE_2D, id);
            
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        }catch(IOException e){
            if(!debugShown){
                JOptionPane.showMessageDialog(null, e.getMessage() + "\ncurrent folder: " + new File("").getAbsolutePath() +
                        "\n" + getClass().getResource("/" + filename).getPath());
                debugShown = true;
            }
                e.printStackTrace(System.err);
        }
    }
    
    private static boolean debugShown = false;
    
    public void bind(int sampler){
        this.sampler = 0;
        bind();
    }
    public void bind(){
        if(sampler >= 0 && sampler < 31){
            glActiveTexture(GL_TEXTURE0 + sampler);
            glBindTexture(GL_TEXTURE_2D, id);
        }
    }
    
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    
}
