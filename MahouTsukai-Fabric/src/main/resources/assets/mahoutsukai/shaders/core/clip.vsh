#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;

layout(std140) uniform MahouClip {
    mat4 ModelMat;
    vec4 ClipPlane;
};

out vec4 vertexColor;
out vec2 texCoord0;
out float mahouClipDistance0;

void main(){
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    texCoord0 = UV0;
    vertexColor = Color;
    mahouClipDistance0 = dot(vec4(Position, 1.0), ClipPlane);
    gl_ClipDistance[0] = mahouClipDistance0;
}
