attribute vec3 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
attribute vec3 a_normal;

uniform mat4 u_projection;
uniform mat4 u_model;
uniform mat4 u_modelView;
uniform mat3 u_normalMatrix;

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec3 v_fragPosition;
varying vec3 v_fragNormal;

void main() {
    v_color = a_color;
    v_texCoords = a_texCoord0;
    v_fragNormal = a_normal;
    vec4 fragPosition4 = u_projection * u_modelView * vec4(a_position, 1.0);
    v_fragPosition = fragPosition4.xyz / fragPosition4.w;
    gl_Position = u_projection * u_modelView * vec4(a_position, 1.0);
}
