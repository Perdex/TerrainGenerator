package lwjglterrain;

import javax.swing.JOptionPane;
import meshes.Mesh;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import org.lwjgl.opengl.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;

public class LWJGLTerrain{

    static final int WIDTH = 1800, HEIGHT = 900;

    private static long win;

    private static Model[] m;
    private static Terrain terrain;

    static Camera camera;
    static Shader shader;
    private static UI2D ui;
    
    static Vector3fc lightPos = new Vector3f(500, 200, 200);

    private static int frames = 0;
    
    public static void main(String[] args){
        
        try{
            init();

            double lastTime = getTime();

            int lastframes = 0;
            float t = 0;

            while(!glfwWindowShouldClose(win)){

                //show FPS every second
                if(getTime() - lastTime >= 1.0){
                    lastTime = getTime();
                    System.out.println("FPS: " + (frames - lastframes));
                    System.out.println("Cam pos: (" + (int)camera.getPosition().x() +
                                                ", " + (int)camera.getPosition().y() +
                                                ", " + (int)camera.getPosition().z() + ")");
                    lastframes = frames;
                }
                frames++;

                updateDisplay(t);
                t += 0.025;
                //sim.updateDrawPositions();
            }
            closeDisplay();
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Something went wrong in main: " + e);
            e.printStackTrace(System.err);
            closeDisplay();
        }
    }

    private static double getTime(){
        return (double) System.nanoTime() / 1e9;
    }

    public static void updateDisplay(float t){

        glClear(GL_COLOR_BUFFER_BIT);//init screen to bg color

        shader.bind();

        Actions.applyEvents(win);

        for(Model m1 : m)
            m1.render(t, shader, camera.getPosition(), lightPos);
        
        terrain.render(t, shader, camera.getPosition(), lightPos);
        
        glDisable(GL_DEPTH_TEST);
        
        ui.render(shader);

        glEnable(GL_DEPTH_TEST);
        
        glfwSwapBuffers(win);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();
        

    }

    private static void init(){

        //set error stream
        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit()){
            System.err.println("Init errored!");
            System.exit(1);
        }

        win = glfwCreateWindow(WIDTH, HEIGHT, "It's (now) 3D!", 0, 0);


        // Make the OpenGL context current
        glfwMakeContextCurrent(win);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(win);

        //init GL environment
        GL.createCapabilities();

        glEnable(GL_TEXTURE_2D);
        
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDepthFunc(GL_LEQUAL);
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        glFrontFace(GL_CW);
        glEnable(GL_CULL_FACE);
        glDisable(GL_DITHER);

        //BG color
        //glClearColor(0.6f, 0.6f, 1, 1);
        glClearColor(0.9f, 0.9f, 0.9f, 1);

        m = new Model[2];
        m[0] = Model.createFromMesh(meshes.Ball.makeBall(20, lightPos, 30, "sun.jpg"));
        m[1] = Model.createFromMesh(meshes.Fxy.plane(260, 50000, 1000, false, 4));
        //m[1] = Model.createFromMesh(meshes.Ball.makeBall(20, new Vector3f(0, 0, -50), 100, null), 1);

        shader = new Shader("shader");

        camera = Camera.makeTerrainCamera();

        terrain = new Terrain();
        camera.setTerrain(terrain);
        
        Actions.addCallbacks(shader, win, camera);
        
        ui = new UI2D(WIDTH, HEIGHT);
        
    }

    public static Mesh getMesh(int i){
        return m[i % m.length].getMesh();
    }
    
    public static void refreshMeshF(){
        refreshTerrain(meshes.Fxy.terrainFractal());
    }
    public static void refreshMeshP(){
        refreshTerrain(meshes.Fxy.terrainPerlin());
    }
    public static void refreshMeshP2(){
        //refreshTerrain(meshes.Fxy.terrainTruePerlin(permutation));
    }
    public static void refreshMeshS(){
        refreshTerrain(meshes.Fxy.terrainSine());
    }
    private static void refreshTerrain(Mesh mesh){
//        camera.setTerrain(mesh);
//        m[0] = Model.createFromMesh(mesh);
    }
    

    private static void closeDisplay(){

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(win);
        glfwDestroyWindow(win);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
