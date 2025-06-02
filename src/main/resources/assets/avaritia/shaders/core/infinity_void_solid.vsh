#version 150

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;

out vec4 texProj;
out vec4 vertexColor;
out vec2 texCoord0;
out float vertexDistance;

void main() {
    vec4 worldPos = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * worldPos;

    texProj = projection_from_position(gl_Position);
    vertexDistance = fog_distance(Position, FogShape);
    vertexColor = Color;
    texCoord0 = UV0;
}

