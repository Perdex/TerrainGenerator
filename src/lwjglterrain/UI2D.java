package lwjglterrain;

import java.awt.Color;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;



public class UI2D{

    
    private int id, width = 1, height = 1;
    
    private final int max_width;
    
    public UI2D(int width, int height){
        this.width = width;
        this.height = height;
        id = glGenTextures();
        
        
        float[] in = new float[2];
        glGetFloatv(GL_ALIASED_LINE_WIDTH_RANGE, in);
        
        max_width = (int)in[1];
    }
    
    
    public void render(Shader s){
        
        s.setUniform("shaderMode", 3);
        
        s.setUniform("projection", LWJGLTerrain.camera.get2DProjection());
        
        drawRect(width - 30, 15, 14, (height - 30d) * Camera.getDrawSpeed(), 7, Color.green);
        //drawLine(0.4825, -0.48, 0.4825, -0.48 + 0.965 * Camera.getDrawSpeed(), 7, Color.black);
        drawRect(width - 31, 15, 16, height - 30, 1, Color.green.darker().darker());
        
        int crossHairSize = 8;
        drawLine(width / 2 - crossHairSize, height / 2, width / 2 + crossHairSize, height / 2, 2, Color.darkGray);
        drawLine(width / 2, height / 2 - crossHairSize, width / 2, height / 2 + crossHairSize, 2, Color.darkGray);
        
        glDisable(GL_LINE_WIDTH);
    }
    
    //Coordinates in pixels, origin left bottom
    private void drawRect(double x0, double y0, double width, double height, int lWidth, Color color){
        
        lWidth = Math.min(lWidth, max_width);
        
        x0 += 0.5 * lWidth;
        y0 += 0.5 * lWidth;
        width -= lWidth;
        height -= lWidth;
        
        x0 /= this.width;
        y0 /= this.height;
        width /= this.width;
        height /= this.height;
        
        x0 -= 0.5;
        y0 -= 0.5;
        
        glLineWidth(lWidth);
        
        glBegin(GL_LINES);
            glEnable(GL_LINE_WIDTH);
            glColor3f(color.getRed(), color.getGreen(), color.getBlue());
        
            //left side
            glVertex3d(x0, y0 + height, 0);
            glVertex3d(x0, y0, 0);
            
            //Right side
            glVertex3d(x0 + width, y0, 0);
            glVertex3d(x0 + width, y0 + height, 0);
            
            x0 -= (double)lWidth / (double)this.width / 2d;
            width += (double)lWidth / (double)this.width;
            
            //bottom
            glVertex3d(x0, y0, 0);
            glVertex3d(x0 + width, y0, 0);
            
            //top
            glVertex3d(x0, y0 + height, 0);
            glVertex3d(x0 + width, y0 + height, 0);
            
        
        glEnd();
    }
    
    private void drawLine(double x0, double y0, double x1, double y1, int lWidth, Color color){
        
        x0 /= this.width;
        y0 /= this.height;
        x1 /= this.width;
        y1 /= this.height;
        
        x0 -= 0.5;
        y0 -= 0.5;
        x1 -= 0.5;
        y1 -= 0.5;
        
        
        glLineWidth(lWidth);
        
        glBegin(GL_LINES);
            glEnable(GL_LINE_WIDTH);
            glColor3f(color.getRed(), color.getGreen(), color.getBlue());
        
            glVertex2d(x0, y0);
            glVertex2d(x1, y1);
            
        glEnd();
    
    }
}
