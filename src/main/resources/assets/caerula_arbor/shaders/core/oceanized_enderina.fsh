#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float DissolveProgress;
uniform float NoiseEnabled;
uniform float GameTime;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in vec4 vertexColor;
in float vertexDistance;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec4 normal;
in vec2 texCoord0;

out vec4 fragColor;

float hash21(vec2 point) {
    point = fract(point * vec2(123.34, 456.21));
    point += dot(point, point + 45.32);
    return fract(point.x * point.y);
}

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    vec2 pixel = floor(texCoord0 * vec2(128.0, 64.0));
    float noise = hash21(pixel);
    float dissolve = smoothstep(DissolveProgress, DissolveProgress + 0.08, noise);
    color.a *= dissolve;
    if (color.a < 0.1) {
        discard;
    }
    float pulse = hash21(pixel + floor(GameTime * 720.0)) * NoiseEnabled;
    vec3 noiseColor = mix(vec3(0.0, 0.85, 1.0), vec3(0.85, 0.0, 1.0), hash21(pixel + 31.0));
    color.rgb = mix(color.rgb, color.rgb * 0.35 + noiseColor * 0.65, pulse * 0.85);
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
