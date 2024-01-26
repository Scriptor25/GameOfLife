#version 330 core

layout(location = 0) in vec4 Position;
layout(location = 1) in vec4 Color;

out vec4 vColor;

uniform mat4 uProjection = mat4(1.0);
uniform mat4 uView = mat4(1.0);
uniform mat4 uModel = mat4(1.0);

void main() {
    gl_Position = uProjection * uView * uModel * Position;
    vColor = Color;
}
