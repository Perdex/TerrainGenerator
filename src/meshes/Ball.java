package meshes;

import lwjglterrain.Texture;

import java.util.ArrayList;
import org.joml.Vector3f;
import org.joml.Vector3fc;



public class Ball extends Mesh{
    
    
    private Ball(ArrayList<Float> vert, ArrayList<Float> text, ArrayList<Integer> ind){
        super(vert, text, ind, vert);
    }
    
    
    public static Ball makeBall(float r, Vector3fc pos, int precision, String texture){
        return makeEllipsoid(new Vector3f(r), pos, precision, texture);
    }
        
    public static Ball makeEllipsoid(Vector3fc scale, Vector3fc pos, int precision, String texture){
        
        boolean textured = texture != null;
        
        ArrayList<Float> vert = new ArrayList();
        ArrayList<Float> text = new ArrayList();
        ArrayList<Integer> ind = new ArrayList();
        
        
        float step = (float)Math.PI / precision;
        
        int id = 0;
        
        
        for(int i = 1; i < precision; i++){
            
            //step back to the same pos for texture reasons!
            for(int j = 0; j <= precision; j++){
                
                float z = (float)i * step;
                
                vert.add(f(Math.sin((float)j * step * 2f) * Math.sin(z)));
                vert.add(f(Math.cos((float)j * step * 2f) * Math.sin(z)));
                vert.add(f(Math.cos(z)));
                
                if(textured){
                    text.add(1 - (float)j / (precision));
                    text.add((float)i / (precision));
                }else{
                    text.add(f(i % 2));
                    text.add(f(j % 2));
                }
                
                //upper left
                if(i > 1){
                    
                    if(j != 0){
                        ind.add(id);
                        ind.add(id - 1);
                        ind.add(id - precision - 2);

                        ind.add(id);
                        ind.add(id - precision - 2);
                        ind.add(id - precision - 1);
                    }
                }
                id++;
            }
            
        }
        
        //low midpoint
        vert.add(0f);
        vert.add(0f);
        vert.add(1f);
        
        //upper midpoint
        vert.add(0f);
        vert.add(0f);
        vert.add(-1f);
        
        //textcoords
        if(textured){
            text.add(0.5f);
            text.add(0f);
            text.add(0.5f);
            text.add(1f);
        }else{
            text.add(precision % 2f);
            text.add(1f);
            text.add(precision % 2f);
            text.add(1f);
        }
        
        for(int i = 0; i < precision; i++){
            //lower
            ind.add(id);
            ind.add((i + 1) % precision);
            ind.add(i);
            
            int idd = id - precision;
            
            //upper
            ind.add(id + 1);
            ind.add(idd + i);
            ind.add((i + 1) % precision + idd);
        }
        
        Ball b = new Ball(vert, text, ind);
        if(textured)
            b.texture = new Texture(texture);
        b.scale(scale);
        b.setPos(pos);
        return b;
    }
    
    private static float f(double d){
        return (float)d;
    }
}
