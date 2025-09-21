#version 330 core

in vec2 vPosition;

layout(location = 0) out vec4 Color;

uniform vec4 uColor = vec4(1.0);

void main() {
    Color = uColor;

    ivec2 pixel = ivec2(vPosition);

}
