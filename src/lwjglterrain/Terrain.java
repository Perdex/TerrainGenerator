package lwjglterrain;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import meshes.Mesh;
import meshes.Fxy;
import java.util.ArrayList;
import java.util.Random;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class Terrain{
    
    private ArrayList<terrainMesh> meshes;
    private final Model water;
    private static final int[] permutation = new int[512];
    
    private final int size = 10;
    private final float precision = 0.2f;
    private final int renderRange = 80;
    private final Mesh.f func;
    
    public static TerrainTexture terrainTexture = null;
    
    public Terrain(){
        terrainTexture = new TerrainTexture("ground.png", "beach.png", "green.png", "snow.png");
        
        makePermutation();
        
        meshes = new ArrayList<>();
        
        func = (float x, float y) -> {
            float z = 0.2f;
            
            for(int i = 0; i < 5; i++){
                z += Math.pow(0.4, i) * (Fxy.truePerlin(
                        0.1 * x * (1 << i),
                        0.1 * y * (1 << i),
                        permutation) - 0.5);
            }
            float scatterExtra = (float)Math.max(0.0f, 0.1 * (Fxy.truePerlin(
                    1 * x,
                    1 * y,
                    permutation) - 0.7));
            if(scatterExtra > 0)
                scatterExtra += 0.03;
            //TODO window size!!
            z += scatterExtra;
            
            return z * 5;
        };
        water = Model.createFromMesh(Fxy.plane(0, 60, 0.05f, true, 5));
    }
    
    private terrainMesh addMesh(int x, int y){
        Mesh mesh = Fxy.build(size, precision, new Vector3f(x, y, 0), true, func);
        terrainMesh tm = new terrainMesh(mesh, x, y);
        meshes.add(tm);
        return tm;
    }
    
    public float getZ(float x, float y){
        return func.z(x, y);
    }
    
    public void render(float time, Shader s, Vector3fc camPos, Vector3fc lightPos){
        int camTileX = (int)camPos.x();
        camTileX -= camTileX % size;
        int camTileY = (int)camPos.y();
        camTileY -= camTileY % size;
        
        water.getMesh().setPos(new Vector3f(camTileX, camTileY, 0));
        water.render(time, s, camPos, lightPos);
        
        terrainTexture.bind();
        s.setUniform("shaderMode", 1);
        s.setUniform("textureSampler", 0);
        s.setUniform("beachSampler", 1);
        s.setUniform("greenSampler", 2);
        s.setUniform("snowSampler", 3);
        
        s.setUniform("Time", time);
        
        int n = renderRange / size;
        for(int i = -n; i <= n; i++){
            for(int j = -n; j <= n; j++){
            
                int x = camTileX + i * size;
                int y = camTileY + j * size;

                if(new Vector3f(x, y, 0).sub(camPos).lengthSquared() < renderRange * renderRange){
                    terrainMesh m;
                    int index = meshes.indexOf(new terrainMesh(x, y));

                    if(index != -1){
                        m = meshes.get(index);
                    }else
                        m = addMesh(x, y);

                    m.render(s, camPos, lightPos);
                }
            }
        }
//        System.out.println("list:");
//        meshes.forEach((terrainMesh m) -> {
//            System.out.println(m.x + " " + m.y);
//        });
    }
    
    private static int[] makePermutation(){
        int tablesize = permutation.length;
        
        //Make the permutation table
        for(int i = 0; i < tablesize / 2; i++){
            permutation[i] = i;
        }
        Random r = new Random();
        for(int i = tablesize / 2; i > 0; i--){
            int j = r.nextInt(i);
            int temp = permutation[i];
            permutation[i] = permutation[j];
            permutation[j] = temp;
        }
        
        for(int i = tablesize / 2; i < tablesize; i++){
            permutation[i] = permutation[i % (tablesize / 2)];
        }
        return permutation;
    }
    
    private class terrainMesh{
        private Mesh mesh;
        private int draw_count, v_id, t_id, i_id, n_id;
        private final int x, y;

        public terrainMesh(int x, int y){
            this.x = x;
            this.y = y;
        }
        public terrainMesh(Mesh mesh, int x, int y){
            this.mesh = mesh;
            
            this.x = x;
            this.y = y;
            
            draw_count = mesh.getIndexArray().length;

            v_id = glGenBuffers();
            //bind
            glBindBuffer(GL_ARRAY_BUFFER, v_id);
            glBufferData(GL_ARRAY_BUFFER, createBuffer(mesh.getVertArray()), GL_STATIC_DRAW);

            t_id = glGenBuffers();
            //bind
            glBindBuffer(GL_ARRAY_BUFFER, t_id);
            glBufferData(GL_ARRAY_BUFFER, createBuffer(mesh.getTextArray()), GL_STATIC_DRAW);

            n_id = glGenBuffers();
            //bind
            glBindBuffer(GL_ARRAY_BUFFER, n_id);
            glBufferData(GL_ARRAY_BUFFER, createBuffer(mesh.getNormalArray()), GL_STATIC_DRAW);

            i_id = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, i_id);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, createBuffer(mesh.getIndexArray()), GL_STATIC_DRAW);

            //unbind
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        }
        
        private void render(Shader s, Vector3fc camPos, Vector3fc lightPos){
            Vector3fc pos = mesh.getPos();
            
            Matrix4f proj = LWJGLTerrain.camera.getProjection().mul(mesh.getViewMatrix(), new Matrix4f());
            LWJGLTerrain.shader.setUniform("projection", proj);

            Vector3fc relLightPos = lightPos.sub(pos, new Vector3f());
            Vector3fc relCamPos = camPos.sub(pos, new Vector3f());

            s.setUniform("LightPosition", relLightPos);
            s.setUniform("CameraPosition", relCamPos);

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
        private IntBuffer createBuffer(int[] data){
            IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
            buffer.put(data);
            buffer.flip();
            return buffer;
        }

        @Override
        public boolean equals(Object o){
            if(!(o instanceof terrainMesh))
                return false;
            terrainMesh other = (terrainMesh)o;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode(){
            int hash = 5;
            hash = 89 * hash + this.x;
            hash = 89 * hash + this.y;
            return hash;
        }
    }
    
}
