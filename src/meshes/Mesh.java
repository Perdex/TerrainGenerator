package meshes;

import java.util.ArrayList;
import lwjglterrain.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;



public class Mesh {
    private final float[] vertArray, texArray, normalArray;
    private final int[] indexArray;
    
    protected Texture texture;
    
    public int shaderMode = 0;
    
    private Vector3fc scale = new Vector3f(1, 1, 1), pos = new Vector3f();
    
    float minx, miny, maxx, maxy;
    
    public interface f{float z(float x, float y);}
    
    public Mesh(ArrayList<Float> vert,
                ArrayList<Float> text,
                ArrayList<Integer> ind,
                ArrayList<Float> nor){
        vertArray = new float[vert.size()];
        texArray = new float[text.size()];
        indexArray = new int[ind.size()];
        normalArray = new float[nor.size()];
        
        minx = maxx = vert.get(0);
        miny = maxy = vert.get(1);
        
        //convert to native arrays
        //(using .toArray would return Float objects, instead of float natives)
        for(int i = 0; i < vert.size(); i++){
            vertArray[i] = vert.get(i);
            if(i % 3 == 0){
                minx = Math.min(minx, vertArray[i]);
                maxx = Math.max(maxx, vertArray[i]);
            }else if(i % 3 == 1){
                miny = Math.min(miny, vertArray[i]);
                maxy = Math.max(maxy, vertArray[i]);
            }
        }
        for(int i = 0; i < text.size(); i++)
            texArray[i] = text.get(i);
        for(int i = 0; i < ind.size(); i++)
            indexArray[i] = ind.get(i);
        for(int i = 0; i < nor.size(); i++)
            normalArray[i] = nor.get(i);
    }
    
    public Mesh(float[] vert, float[] text, int[] ind, float[] nor){
        vertArray = vert;
        texArray = text;
        indexArray = ind;
        normalArray = nor;
    }
    
    public float[] getNormalArray(){
        return normalArray;
    }
    
    public float[] getTextArray(){
        return texArray;
    }
    public float[] getVertArray(){
        return vertArray;
    }
    public int[] getIndexArray(){
        return indexArray;
    }
    
    public Texture getTexture(){
        return texture;
    }
    public boolean isTextured(){
        return texture != null;
    }
    public void setTexture(String texture){
        this.texture = new Texture(texture);
    }
    
    public Vector3fc getPos(){
        return pos;
    }
    public void setPos(Vector3fc v){
        pos = new Vector3f(v);
    }
    public void translate(Vector3fc v){
        pos = pos.add(v, new Vector3f());
    }
    
    public void scale(float s){
        scale = scale.mul(s, new Vector3f());
    }
    public void scale(Vector3fc s){
        scale = scale.mul(s, new Vector3f());
    }
    public void setScale(float s){
        scale = new Vector3f(s);
    }
    public void setScale(Vector3fc s){
        scale = s;
    }
    public Vector3fc getScale(){
        return scale;
    }
    
    
    public Matrix4f getViewMatrix(){
        return new Matrix4f().translate(pos).scale(scale);
    }
}
