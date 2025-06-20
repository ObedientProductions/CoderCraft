#version 150
#moj_import <minecraft:matrix.glsl>

uniform sampler2D Sampler0;
uniform float GameTime;
uniform int currentFrame;
uniform int frameCount;
uniform float ZoomScale;
uniform float CameraYaw;
uniform float CameraPitch;
uniform float BackgroundAlpha;
uniform float PixelAlpha;

uniform float StarColorR;
uniform float StarColorG;
uniform float StarColorB;

uniform float BackgroundColorR;
uniform float BackgroundColorG;
uniform float BackgroundColorB;

in vec4 texProj;
in vec4 vertexColor;
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
    return mat3(c, 0, -s, 0, 1, 0, s, 0, c);
}

mat3 pitchRotation(float pitch) {
    float rad = radians(pitch);
    float c = cos(rad), s = sin(rad);
    return mat3(1, 0, 0, 0, c, -s, 0, s, c);
}

float smoothHash(float x) {
    return sin(x);
}



void main() {
    float yawScale = 0.25;
    float pitchScale = 0.25;

    vec3 viewDir = normalize(texProj.xyz / texProj.w);
    vec2 screenUV = viewDir.xy / max(0.01, 1.0 - abs(viewDir.z));
    screenUV *= 0.5;

    mat2 yawRot = rotate(radians(CameraYaw) * yawScale);
    mat2 pitchRot = rotate(radians(CameraPitch) * pitchScale);
    vec2 parallax = yawRot * pitchRot * screenUV * ZoomScale;

    float pulse = mod(GameTime * 20.0, 400.0) / 400.0;
    vec3 background = vec3(
    BackgroundColorR,
    sin(pulse * 6.2831) * 0.075 + BackgroundColorG,
    cos(pulse * 6.2831) * 0.05 + BackgroundColorB
    );



    vec3 result = background;
    vec3 bloom = vec3(0.0);

    float frameHeight = 1.0 / frameCount;
    float vOffset = currentFrame * frameHeight;
    float frameAlpha = (currentFrame == frameCount - 1) ? 1.0 : 0.7;

    int layers = 16;
    for (int i = 0; i < layers; i++) {
        int mult = 16 - i;

        float base = float(i);
        float randR = hash(base * 12.123);
        float randG = hash(base * 45.456);
        float randB = hash(base * 78.789);

        float angle = mod(randB, 6.2831);

        vec2 offset = vec2(
        smoothHash(float(i) * 12.1 + GameTime * 4.0),
        smoothHash(float(i) * 45.7 + GameTime * 4.0)
        ) * (1.0 / float(mult)) * 1000.0;


        vec2 uv = rotate(angle) * (parallax * (mult * 0.5 + 2.75) * ZoomScale + offset);

        uv = fract(uv / 4.0);
        uv.y = uv.y * frameHeight + vOffset;

        vec3 tex = texture(Sampler0, uv).rgb;

        float a = tex.r * (0.5 + (1.0 / mult));

        vec3 randomColor = vec3(
        randR * 0.1 + 0.9,
        randG * 0.1 + 0.9,
        randB * 0.1 + 0.9
        );


        vec3 StarColor = vec3(StarColorR, StarColorG, StarColorB);
        vec3 color = randomColor * StarColor;


        result += color * a;
        bloom += color * (a * 0.2);

    }

    result = clamp(result, 0.0, 1.0);
    result += bloom * 0.5;
    fragColor = vec4(result, (vertexColor.a * PixelAlpha));
}


