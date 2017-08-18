package lwjglterrain;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix4f;
import org.joml.Vector3fc;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class Model {
    
    private final int draw_count, v_id, t_id, i_id, n_id;
    private meshes.Mesh mesh = null;
    
    public Model(float[] vertices, float[] tex_coords, int[] indices, float[] normals){
        draw_count = indices.length;
        
        v_id = glGenBuffers();
        //bind
        glBindBuffer(GL_ARRAY_BUFFER, v_id);
        glBufferData(GL_ARRAY_BUFFER, createBuffer(vertices), GL_STATIC_DRAW);
        
        t_id = glGenBuffers();
        //bind
        glBindBuffer(GL_ARRAY_BUFFER, t_id);
        glBufferData(GL_ARRAY_BUFFER, createBuffer(tex_coords), GL_STATIC_DRAW);
        
        
        n_id = glGenBuffers();
        //bind
        glBindBuffer(GL_ARRAY_BUFFER, n_id);
        glBufferData(GL_ARRAY_BUFFER, createBuffer(normals), GL_STATIC_DRAW);
        
        i_id = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, i_id);
        IntBuffer buffer = BufferUtils.createIntBuffer(indices.length);
        
        buffer.put(indices);
        buffer.flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        
        //unbind
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
    
    public static Model createFromMesh(meshes.Mesh m, int i){
        Model mod = new Model(m.getVertArray(), m.getTextArray(), m.getIndexArray(), m.getNormalArray());
        mod.mesh = m;
        if(m.isTextured()){
            m.getTexture().bind(i);
        }
        return mod;
    }
    
    public meshes.Mesh getMesh(){
        return mesh;
    }

    public void render(Shader s, Vector3fc camPos, Vector3fc lightPos){
        
        Matrix4f proj = LWJGLTerrain.camera.getProjection();
        
        if(mesh != null){
            proj = proj.mul(mesh.getViewMatrix());
            if(mesh.isTextured()){
                mesh.getTexture().bind();
                s.setUniform("shaderMode", 1);
            }else
                s.setUniform("shaderMode", 0);
            
            Vector3fc pos = mesh.getPos();
            //0.2 is the weirdest bug I've ever encountered: for some reason the meshes are scale by 5!
            s.setUniform("LightPosition", (lightPos.x() - pos.x()), -(lightPos.y() - pos.y()), (lightPos.z() - pos.z()));
            s.setUniform("CameraPosition", (camPos.x() - pos.x()), -(camPos.y() - pos.y()), (camPos.z() - pos.z()));
        }
        
        LWJGLTerrain.shader.setUniform("projection", proj);
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        
        
        //bind vertices
        glBindBuffer(GL_ARRAY_BUFFER, v_id);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        
        //bind textures
        glBindBuffer(GL_ARRAY_BUFFER, t_id);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        
        //bind normals
        glBindBuffer(GL_ARRAY_BUFFER, n_id);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
        
        //bind indices
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, i_id);
        glDrawElements(GL_TRIANGLES, draw_count, GL_UNSIGNED_INT, 0);
        
        
        //unbind
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
    }
    
    private FloatBuffer createBuffer(float[] data){
        FloatBuffer buff = BufferUtils.createFloatBuffer(data.length);
        buff.put(data);
        buff.flip();
        return buff;
    }
}
