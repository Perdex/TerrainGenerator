#version 120

uniform sampler2D sampler;

varying vec3 Position;
varying vec2 Texture;

varying float LightStrength;

uniform int shaderMode;


void main(){
    if(shaderMode == 1){
            gl_FragColor = texture2D(sampler, Texture);
            return;
    }
    if(shaderMode == 3){
            gl_FragColor = gl_Color;
            return;
    }

    float r;
    float g;
    float b;

    float wireframeScale = 0.02;
    float k = 1;
	
    if(Position.z <= 0){
        //water
        r = 0;
        g = 0.1 / (1 - Position.z * 2);
        b = 1 / (1 - Position.z * 2);
        wireframeScale = 0;
    }else if(Position.z < 0.15){
        //beaches
        r = 0.8;
        g = 0.8;
        b = 0.3;
    }else{
        //green areas
        g = 0.5;

        //add brown areas
        r = sin(Position.x / 4 + 0.8 * Position.z + atan(Position.x) + 5) * Position.z +
            sin(Position.y / 4 - 1.1 * Position.z - 0.2 * atan(Position.y) + 5) + 0.1 * (Position.z - 0.15);

        r = clamp(r, -0.5, 0.5);
        b = 0.1;

        if(r > 0)
            g = max(0.5 - r * 0.5, r * 0.6);
        else
            r = 0.1;

        if(Position.z > 1.5){
            //add stone
            float w = sin(Position.x / 3 + 0.7 * Position.z + atan(Position.x) + 2) +
                sin(Position.y / 2.2 - 1.1 * Position.z - 0.25 * atan(Position.y) + 3) + (Position.z - 2);

            
            if(w > 0)
                r = g = b = 0.3; 

        }

        if(Position.z > 2.5){
            //white tops
            float w = sin(Position.x / 2 + 0.7 * Position.z + atan(Position.x) + 1) +
                sin(Position.y / 2 - 1.3 * Position.z - 0.2 * atan(Position.y) + 1) + (Position.z - 3);


            if(w > 0)
                r = g = b = 0.9;
            
        }else if(Position.z < 0.18){
            //gradient by the beach

            float w = (Position.z - 0.15) / 0.03;

            //beach color is (0.8, 0.8, 0.3)

            r = r * w + (1 - w) * 0.8;
            g = g * w + (1 - w) * 0.8;
            b = b * w + (1 - w) * 0.3;            

        }
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

    k *= LightStrength;

    r = clamp(r * k, 0, 1);
    g = clamp(g * k, 0, 1);
    b = clamp(b * k, 0, 1);

    gl_FragColor = vec4(r, g, b, 1);
}
