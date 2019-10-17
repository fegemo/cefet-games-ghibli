attribute vec3 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
varying vec2 v_texCoords;
varying vec3 v_fragPosition;

void main() {
    v_texCoords = a_texCoord0;
    vec4 fragPosition4 = u_projTrans * vec4(a_position, 1.0);
    v_fragPosition = fragPosition4.xyz / fragPosition4.w;

    gl_Position = fragPosition4;
}

