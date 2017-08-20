#version 120

attribute vec3 vertices;
attribute vec2 textures;
attribute vec3 normals;

varying vec3 Position;
varying vec2 Texture;

varying float LightStrength;
varying float ReflectionStrength;
varying float CamDist;

uniform vec3 LightPosition;
uniform vec3 CameraPosition;
uniform float Time;
uniform int shaderMode;

uniform mat4 projection;

void main(){

    Position = vertices;
    Texture = textures;
    
    Texture.x -= int(Texture.x * 0.5) * 2;
    Texture.y -= int(Texture.y * 0.5) * 2;
    
    if(Texture.x > 1)
        Texture.x = 2 - Texture.x;
    if(Texture.y > 1)
        Texture.y = 2 - Texture.y;

    vec3 lightRel = normalize(LightPosition - Position);
    vec3 camRel = normalize(CameraPosition - Position);

    float light = clamp(dot(lightRel, normals), 0, 1) + 0.2;

    vec3 refNorm = normals;

    if(shaderMode == 5){
        //water
        refNorm.x += 0.1 * sin(Time + 50 * Position.x);
        refNorm.y += 0.1 * sin(Time + 200 * Position.y + 20 * Position.x);
        refNorm = normalize(refNorm);
    }

    vec3 reflected = normalize(-lightRel + 1.7 * dot(lightRel, refNorm) * refNorm);
    float reflection = clamp(dot(camRel, reflected) - 0.95, 0, 1) * 20;
    reflection *= reflection;
    ReflectionStrength = 0;

    LightStrength = light;
    if(dot(lightRel, refNorm) > 0)
        ReflectionStrength = reflection;

    CamDist = length(CameraPosition - Position);

    gl_FrontColor = gl_Color;
    gl_Position = projection * vec4(Position, 1);
}

