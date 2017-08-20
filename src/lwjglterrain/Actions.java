package lwjglterrain;

import static org.lwjgl.glfw.GLFW.*;

public class Actions{
//    private static float zoom = 1.2f, x = 0.5f, y = 0;
    public static void applyEvents(long win){
        
        final int[] transKeys = new int[]{
            GLFW_KEY_A, GLFW_KEY_D, GLFW_KEY_LEFT_CONTROL, GLFW_KEY_LEFT_SHIFT, GLFW_KEY_W, GLFW_KEY_S
        };
        final int[] rotKeys = new int[]{
            GLFW_KEY_UP, GLFW_KEY_DOWN, GLFW_KEY_LEFT, GLFW_KEY_RIGHT, GLFW_KEY_Q, GLFW_KEY_E
        };
        
        
        for(int i = 0; i < 6; i++){
            //go through translating keys
            if(glfwGetKey(win, transKeys[i]) == GLFW_PRESS)
                LWJGLTerrain.camera.translate(2*(i%2)-1, i / 2);
            
            //go through rotating keys
            if(glfwGetKey(win, rotKeys[i]) == GLFW_PRESS)
                LWJGLTerrain.camera.rotate((2*(i%2)-1) * 0.05f, i / 2);
            
        }
        if(glfwGetKey(win, GLFW_KEY_SPACE) == GLFW_PRESS)
            LWJGLTerrain.camera.translateFree(1f, 2);
    }
    
    private static double lastx = 0, lasty = 0;
    private static boolean rot = false;

    public static void addCallbacks(Shader s, long win, Camera camera){
        
        
        glfwSetCursorPosCallback(win, (window, mousex, mousey) -> {
            if(rot && lastx != 0 && lasty != 0){
                LWJGLTerrain.camera.rotate((float)(mousex - lastx) / 100f, 1);
                LWJGLTerrain.camera.rotate((float)(mousey - lasty) / 100f, 0);
            }
            lastx = mousex;
            lasty = mousey;
            
            
//            Vector3f pos = LWJGLTerrain.camera.getPosition();
//            Vector3f dir = LWJGLTerrain.camera.getNormal();
//            
//            Model.lightPos = new Vector3f(pos.x - dir.x * pos.z / dir.z, pos.y - dir.y * pos.z / dir.z, 50);
        });
        
        glfwSetMouseButtonCallback(win, (window, button, action, mods) -> {
            if(button == GLFW_MOUSE_BUTTON_1)
                rot = !rot;
            if(button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS){
                //Camera.targetMesh++;
            }
        });
        
        glfwSetScrollCallback(win, (window, dx, dy) -> {
            //LWJGLtest.camera.translateFree((float)dy * 2f, 2);
            camera.addSpeed(0.01f * (float)dy);
        });

        
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(win, (window, key, scancode, action, mods) -> {
                if(action == GLFW_RELEASE && key == GLFW_KEY_ESCAPE)
                    glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
                if(action == GLFW_RELEASE && key == GLFW_KEY_4)
                    LWJGLTerrain.refreshMeshF();
                if(action == GLFW_RELEASE && key == GLFW_KEY_1)
                    LWJGLTerrain.refreshMeshP();
                if(action == GLFW_RELEASE && key == GLFW_KEY_2)
                    LWJGLTerrain.refreshMeshP2();
                if(action == GLFW_RELEASE && key == GLFW_KEY_3)
                    LWJGLTerrain.refreshMeshS();
        });
    }
}
