#version 150

uniform sampler2D Sampler0;
uniform float time;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a < 0.1) discard;

    vec2 center = vec2(0.5, 0.5);
    float dist = distance(texCoord0, center);
    float maxDist = 1.0;

    if (dist > maxDist) {
        fragColor = color;
        return;
    }

    // Base circular pulse
    float pulse = 0.95 + 0.05 * sin(time * 2.0 + dist * 25.0);

    // Global on/off wave
    float onOff = 0.8 + 0.2 * sin(time * 1.5); // slower wave, dims everything globally

    // Boost near-target colors
    vec3 target = vec3(0.847, 0.945, 0.89);
    float similarity = clamp(1.0 - distance(color.rgb, target) * 2.0, 0.0, 1.0);
    float boostedSimilarity = pow(similarity, 3.0);
    float boost = mix(1.0, 2.5, boostedSimilarity);

    // Distance-based dimming
    float dim = pow(1.0 - (dist / maxDist), 0.7);
    dim = clamp(dim, 0.0, 1.0);

    fragColor = vec4(color.rgb * pulse * boost * dim * onOff, color.a);

}







