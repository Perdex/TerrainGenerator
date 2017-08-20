#version 120

uniform sampler2D textureSampler;
uniform sampler2D beachSampler;
uniform sampler2D greenSampler;
uniform sampler2D snowSampler;

varying vec3 Position;
varying vec2 Texture;

varying float LightStrength;
varying float ReflectionStrength;
varying float CamDist;

uniform float Time;

uniform int shaderMode;

void main(){
    if(shaderMode == 3){
        //2d ui
        gl_FragColor = gl_Color;
        return;
    }
    
    float r;
    float g;
    float b;
    float a = 1;
    
    float reflection = ReflectionStrength;
    
    float fog = clamp(CamDist * 0.02 - 0.5, 0, 1);
    
    float k = 1;
    
    if(shaderMode == 0){
        //plain texture
        vec4 color = texture2D(textureSampler, Texture);
        r = color.x;
        g = color.y;
        b = color.z;
        a = color.w;
        k = 4;
        fog = 0;
    }
    
    if(shaderMode == 1){
        //textures
        float greenweight = 0;
        float beachweight = 0;
        float snowweight = 0;
        
        if(Position.z > 2.5){
            snowweight = (Position.z - 2.5) * 4;
            snowweight = min(1, snowweight);
        }
        
        if(Position.z < 0.15){
            beachweight = 1 - 20 * max(0, Position.z - 0.1);
        }
        //greens
        r = sin(Position.x / 4 + 0.8 * Position.z + atan(Position.x) + 5) * Position.z +
            sin(Position.y / 4 - 1.1 * Position.z - 0.2 * atan(Position.y) + 5) + 0.1 * (Position.z - 0.15);

        r = clamp(r, -0.2, 0.2);

        if(r > 0){
            greenweight = r * 5;
            greenweight = min(1 - snowweight - beachweight, greenweight);
        }
        
        float groundweight = 1 - greenweight - beachweight - snowweight;
        vec4 color = texture2D(textureSampler, Texture) * groundweight;
        
        color += texture2D(greenSampler, Texture) * greenweight;
        color += texture2D(beachSampler, Texture) * beachweight;
        color += texture2D(snowSampler, Texture) * snowweight;
        
        reflection *= 0.8 * snowweight + 0.6 * beachweight + 0.2 * greenweight + 0.2 * groundweight;
        
        r = color.x;
        g = color.y;
        b = color.z;
        a = color.w;
    }


    //float wireframeScale = 0.02;
    
    if(shaderMode == 5){
        //water
        r = 0.3;
        g = 0.5;
        b = 0.8;
        a = 0.4 + reflection;
        k += reflection * 10;
    }

    if(shaderMode == 4){
        //sky
        r = 0.3;
        g = 0.3;
        b = 0.8;
        fog = min(1, sqrt(CamDist) * 0.02);
    }
    if(shaderMode < 4 && shaderMode != 0){
        if(Position.z < 0)
            k *= LightStrength;
        else
            k *= LightStrength + reflection;
    }
    /*if(Texture.x < wireframeScale)
        k *= Texture.x / wireframeScale;
    if(Texture.y < wireframeScale)
        k *= Texture.y / wireframeScale;

    if(Texture.x > 1 - wireframeScale)
        k *= (1 - Texture.x) / wireframeScale;
    if(Texture.y > 1 - wireframeScale)
        k *= (1 - Texture.y) / wireframeScale;
*/
    /*if(abs(Texture.x - Texture.y - 0.25) < wireframeScale)
        k *= 0.25;
    */

    
    r = clamp(r * k, 0, 1);
    g = clamp(g * k, 0, 1);
    b = clamp(b * k, 0, 1);
    
    a = clamp(a, 0, 1);
    
    //add fog
    r = r * (1 - fog) + fog * 0.9;
    g = g * (1 - fog) + fog * 0.9;
    b = b * (1 - fog) + fog * 0.9;

    gl_FragColor = vec4(r, g, b, a);
}
