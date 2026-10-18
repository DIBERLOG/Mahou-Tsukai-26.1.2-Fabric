#version 330

#moj_import <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

layout(std140) uniform MahouFog {
    vec4 SmokeColor;
    vec4 NearFarAndInvertedViewport;
    vec4 Softness;
};

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

float calc_depth(float z) {
    float nearPlane = NearFarAndInvertedViewport.x;
    float farPlane = NearFarAndInvertedViewport.y;
    return (2.0 * nearPlane) / (farPlane + nearPlane - z * (farPlane - nearPlane));
}

void main(){
    vec4 diffuse = texture(Sampler0, texCoord0.xy) * ColorModulator * SmokeColor * vertexColor;
    if (diffuse.a < 0.02) discard;

    vec2 uv = gl_FragCoord.xy * NearFarAndInvertedViewport.zw;
    float sceneDepth = calc_depth(texture(Sampler1, uv).x);
    float particleDepth = calc_depth(gl_FragCoord.z);
    float depthDelta = clamp(sceneDepth - particleDepth, 0.0, 1.0);
    float softAlpha = smoothstep(0.0, Softness.x, depthDelta);
    float edgeFade = max(softAlpha, step(Softness.x, depthDelta));
    fragColor = vec4(diffuse.rgb, diffuse.a * edgeFade);
    if (fragColor.a < 0.02) discard;
}
