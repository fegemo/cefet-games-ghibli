
uniform sampler2D u_texture;
uniform float u_time;

varying vec2 v_texCoords;
varying vec3 v_fragPosition;

vec3 invert(vec3 color) {
    return vec3(1.0 - color);
}

vec3 toGrayscale(vec3 color) {
    float grayShade = dot(vec3(0.2989, 0.5870, 0.1140), color);
    return vec3(grayShade);
}

vec3 blur(sampler2D tex, vec2 texCoords) {
    float offset = 1.0 / 300.0;
    vec2 kernelOffsets[9] = vec2[](
        vec2(-offset,  offset),      // cima-esquerda
        vec2(      0,  offset),      // cima-meio
        vec2( offset,  offset),      // cima-direita
        vec2(-offset,       0),      // meio-esquerda
        vec2(      0,       0),      // meio-meio
        vec2( offset,       0),      // meio-direita
        vec2(-offset, -offset),      // baixo-esquerda
        vec2(      0, -offset),      // baixo-meio
        vec2( offset, -offset)       // baixo-direita
    );

    float constantWeight = 1.0 / 16.0;
    
    float kernelWeights3[9] = float[](
        constantWeight,   constantWeight*2, constantWeight,
        constantWeight*2, constantWeight,   constantWeight*2,
        constantWeight,   constantWeight*2, constantWeight
    );
    float kernelWeights2[9] = float[](
        -1, -1, -1,
        -1,  9, -1,
        -1, -1, -1
    );

    float kernelWeights[9] = float[](
         1,  1,  1,
         1, -9,  1,
         1,  1,  1
    );

    vec3 neighborsColors[9];
    for (int i = 0; i < 9; i++) {
        neighborsColors[i] = texture(tex, texCoords + kernelOffsets[i]);
    }

    vec3 resultingColor = vec3(0.0);
    for (int i = 0; i < 9; i++) {
        resultingColor += neighborsColors[i] * kernelWeights[i];
    }

    return resultingColor;
}

void main() {
    vec3 colorFromTexture = texture(u_texture, v_texCoords);
    //gl_FragColor = vec4(invert(colorFromTexture), 1.0);
    //gl_FragColor = vec4(toGrayscale(colorFromTexture), 1.0);
    vec3 outlineColor = invert(toGrayscale(blur(u_texture, v_texCoords)));
    gl_FragColor = vec4(outlineColor, 1.0);
    if (length(outlineColor) < 1.5) {
        // é um contorno
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
    } else {
        //gl_FragColor = vec4(1.0);
        gl_FragColor = vec4(colorFromTexture, 1.0);
    }
    //gl_FragColor = vec4(mix(outlineColor, colorFromTexture, length(outlineColor) < 1.5 ? 0.0 : 1.0), 1.0);
}