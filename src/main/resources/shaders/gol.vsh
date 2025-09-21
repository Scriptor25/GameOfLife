#version 330 core

layout(location = 0) in vec4 Position;

out vec2 vPosition;

uniform mat4 uProjection = mat4(1.0);
uniform mat4 uView = mat4(1.0);
uniform mat4 uModel = mat4(1.0);

void main() {
    vPosition = (uProjection * uView * uModel * Position).xy;
    gl_Position = Position;
}
