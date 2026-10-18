#version 330

#moj_import <minecraft:dynamictransforms.glsl>


uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;
in float mahouClipDistance0;

out vec4 fragColor;

void main(){
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    fragColor = color;
    if (mahouClipDistance0 < 0.0) discard;
    if (fragColor.a < 0.02) discard;
}
