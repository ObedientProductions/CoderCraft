#version 150
#moj_import <minecraft:matrix.glsl>

uniform sampler2D Sampler0;
uniform float GameTime;
uniform int currentFrame;
uniform int frameCount;
uniform float ZoomScale; // default to 1.0

uniform float CameraYaw;
uniform float CameraPitch;



in vec4 texProj;
out vec4 fragColor;

float hash(float x) {
    return fract(sin(x * 17.0) * 43758.5453);
}

mat2 rotate(float a) {
    float s = sin(a), c = cos(a);
    return mat2(c, -s, s, c);
}

mat3 yawRotation(float yaw) {
    float rad = radians(yaw);
    float c = cos(rad), s = sin(rad);
    return mat3(
    c, 0, -s,
    0, 1,  0,
    s, 0,  c
    );
}

mat3 pitchRotation(float pitch) {
    float rad = radians(pitch);
    float c = cos(rad), s = sin(rad);
    return mat3(
    1, 0,  0,
    0, c, -s,
    0, s,  c
    );
}



void main() {
    float yawScale = 0.25;   // smaller = slower parallax
    float pitchScale = 0.25;

    vec3 viewDir = normalize(texProj.xyz / texProj.w);
    viewDir = yawRotation(CameraYaw * yawScale) * pitchRotation(-CameraPitch * pitchScale) * viewDir;

    vec3 background = vec3(7 / 255, 13 / 255, 20 / 255); // #070d14
    vec3 result = background;
    vec3 bloom = vec3(0.0);

    float frameHeight = 1.0 / frameCount;
    float vOffset = currentFrame * frameHeight;
    float frameAlpha = (currentFrame == frameCount - 1) ? 1.0 : 0.7;

    int layers = 30;
    float minScale = 5.0;
    float maxScale = 30.0;

    bool drewStar = false;

    for (int i = 0; i < layers; i++) {
        float t = float(i) / float(layers - 1);
        float scale = mix(maxScale, minScale, t) * ZoomScale;
        float depth = float(i) + 1.0;

        float baseAlpha = clamp(1.0 / depth, 0.05, 0.75);
        float alpha = baseAlpha * frameAlpha;

        vec2 offset = vec2(
        hash(depth * 12.1 + GameTime * 0.01),
        hash(depth * 45.7 + GameTime * 0.01)
        );

        float angle = hash(depth * 5.0) * 6.2831;
        vec2 uv = rotate(angle) * (viewDir.xy * scale + offset);
        uv = mod(uv, 4.0); // or 8.0 if you want more stars repeating

        uv.y = uv.y * frameHeight + vOffset;

        vec3 tex = texture(Sampler0, uv).rgb;
        vec3 starTint = vec3(0.6, 0.85, 0.95); // brighter, cooler blue

        if (tex.r > 0.01 || tex.g > 0.01 || tex.b > 0.01) {
            result = mix(result, starTint, alpha * 0.9);
            bloom += starTint * (alpha * 0.2);
            drewStar = true;
        }
    }

    result += bloom * 0.5;

    float finalAlpha = drewStar ? 1f : 0.2f;
    fragColor = vec4(result, finalAlpha);

}
