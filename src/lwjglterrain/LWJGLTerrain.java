package lwjglterrain;

import javax.swing.JOptionPane;
import meshes.Mesh;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;

import org.lwjgl.glfw.GLFWErrorCallback;

public class LWJGLTerrain{

    static final int WIDTH = 1800, HEIGHT = 1000;

    private static long win;

    private static Model m[];

    static Camera camera;
    static Shader shader;
    private static UI2D ui;
    
    static Vector3fc lightPos = new Vector3f(10, 20, 5);

    private static int frames = 0;

    public static void main(String[] args){
        
        try{
            init();

            double lastTime = getTime();

            int lastframes = 0;

            while(!glfwWindowShouldClose(win)){

                //show FPS every second
                if(getTime() - lastTime >= 1.0){
                    lastTime = getTime();
                    System.out.println("FPS: " + (frames - lastframes));
                    System.out.println("Cam pos: (" + (int)camera.getPosition().x +
                                                ", " + (int)camera.getPosition().y +
                                                ", " + (int)camera.getPosition().z + ")");
                    lastframes = frames;
                }
                frames++;

                updateDisplay();
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

    public static void updateDisplay(){

        glClear(GL_COLOR_BUFFER_BIT);//init screen to bg color

        shader.bind();

        Actions.applyEvents(win);

        for(Model m1 : m)
            m1.render(shader, camera.getPosition(), lightPos);
        

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
//        glFrontFace(GL_CW);
//        glEnable(GL_CULL_FACE);
        glDisable(GL_DITHER);

        //BG color
        glClearColor(0.4f, 0.4f, 1, 1);

        m = new Model[4];
        m[0] = Model.createFromMesh(meshes.Fxy.terrainTruePerlin(), 0);
        m[1] = Model.createFromMesh(meshes.Ball.makeBall(20, new Vector3f(0, 0, -50), 100, null), 1);
        m[2] = Model.createFromMesh(meshes.Ball.makeBall(2, lightPos, 20, null), 2);
        m[3] = Model.createFromMesh(meshes.Ball.makeBall(2, lightPos.add(new Vector3f(0, 0, -50), new Vector3f()), 20, null), 2);

        shader = new Shader("shader");

        camera = Camera.makeConstrainedCamera();
        
        Actions.addCallbacks(shader, win);
        
        ui = new UI2D(WIDTH, HEIGHT);

    }

    public static Mesh getMesh(int i){
        return m[i % m.length].getMesh();
    }
    
    public static void refreshMeshF(){
        m[0] = Model.createFromMesh(meshes.Fxy.terrainFractal(), 0);
    }
    public static void refreshMeshP(){
        m[0] = Model.createFromMesh(meshes.Fxy.terrainPerlin(), 0);
    }
    public static void refreshMeshP2(){
        m[0] = Model.createFromMesh(meshes.Fxy.terrainTruePerlin(), 0);
    }
    public static void refreshMeshS(){
        m[0] = Model.createFromMesh(meshes.Fxy.terrainSine(), 0);
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
