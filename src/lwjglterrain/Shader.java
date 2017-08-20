package lwjglterrain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private final int program, vs, fs;
    
    public Shader(String filename){
        program = glCreateProgram();
        
        //read shader codes and compile them
        vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs, readFile(filename + ".vs"));
        glCompileShader(vs);
        
        if(glGetShaderi(vs, GL_COMPILE_STATUS) != 1){
            System.err.println(glGetShaderInfoLog(vs));
            System.exit(1);
        }
        
        fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs, readFile(filename + ".fs"));
        glCompileShader(fs);
        
        if(glGetShaderi(fs, GL_COMPILE_STATUS) != 1){
            System.err.println(glGetShaderInfoLog(fs));
            System.exit(1);
        }
        
        glAttachShader(program, vs);
        glAttachShader(program, fs);
        
        glBindAttribLocation(program, 0, "vertices");
        glBindAttribLocation(program, 1, "textures");
        glBindAttribLocation(program, 2, "normals");
        
        
        glLinkProgram(program);
        if(glGetProgrami(program, GL_LINK_STATUS) != 1){
            System.err.println(glGetProgramInfoLog(program));
            System.exit(1);
        }
        glValidateProgram(program);
        if(glGetProgrami(program, GL_VALIDATE_STATUS) != 1){
            System.err.println(glGetProgramInfoLog(program));
            System.exit(1);
        }
    }
    
    public void bind(){
        glUseProgram(program);
    }
    
    public void setUniform(String name, int value){
        int location = glGetUniformLocation(program, name);
        
        if(location != -1)
            glUniform1i(location, value);
    }
    public void setUniform(String name, float value){
        int location = glGetUniformLocation(program, name);
        
        if(location != -1)
            glUniform1f(location, value);
    }
    public void setUniform(String name, float i, float j, float k){
        int location = glGetUniformLocation(program, name);
        
        if(location != -1)
            glUniform3f(location, i, j, k);
    }
    public void setUniform(String name, Vector3fc value){
        setUniform(name, value.x(), value.y(), value.z());
    }
    public void setUniform(String name, Matrix4f value){
        int location = glGetUniformLocation(program, name);
        
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        value.get(buffer);
        
        if(location != -1)
            glUniformMatrix4fv(location, false, buffer);
    }
    
    private String readFile(String filename){
        StringBuilder s = new StringBuilder();
        BufferedReader br;
        
        try{
            br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/shaders/" + filename)));
            String line;
            while((line = br.readLine()) != null){
                s.append(line);
                s.append("\n");
            }
            br.close();
        }catch(IOException e){
            e.printStackTrace(System.err);
        }
        return s.toString();
    }
}
