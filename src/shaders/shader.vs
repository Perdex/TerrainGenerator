#version 120

attribute vec3 vertices;
attribute vec2 textures;
attribute vec3 normals;

varying vec3 Position;
varying vec2 Texture;

varying float LightStrength;

uniform vec3 LightPosition;
uniform vec3 CameraPosition;

uniform mat4 projection;

void main(){

    Position = vertices;
    Texture = textures;

    vec3 lightRel = normalize(LightPosition - Position);
    vec3 camRel = normalize(CameraPosition - Position);

    float light = clamp(dot(lightRel, normals), 0, 1) + 0.2;

    vec3 reflected = normalize(-lightRel + 2 * dot(lightRel, normals) * normals);
    float reflection = clamp(dot(camRel, reflected) - 0.9, 0, 1) * 15;
    reflection *= reflection;

    LightStrength = 0;//light;
    //if(dot(camRel, normals) > 0)
    //    LightStrength += reflection;

    float camDist = length(CameraPosition - Position);
    LightStrength += clamp(1 - 0.02 * camDist, -0.5, 1);

    gl_FrontColor = gl_Color;
    gl_Position = projection * vec4(Position, 1);
}

