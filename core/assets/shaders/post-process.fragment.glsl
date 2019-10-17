
uniform sampler2D u_texture;
uniform float u_time;

varying vec2 v_texCoords;
varying vec3 v_fragPosition;

// retorna cor invertida
vec3 invert(vec3 color) {
    return color;
}

// retorna cor em escala de cinza
vec3 toGrayscale(vec3 color) {
    return color;
}

vec3 blur(sampler2D tex, vec2 texCoords) {
    // cria um vetor 3x3 contendo o deslocamento de cada pixel adjacente a este
    // (do kernel)
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

    
    // kernel de blur
    float constantWeight = 1.0 / 16.0;
    float kernelWeights[9] = float[](
        constantWeight,   constantWeight*2, constantWeight,
        constantWeight*2, constantWeight,   constantWeight*2,
        constantWeight,   constantWeight*2, constantWeight
    );

    // kernel de aguçar imagem (sharpen)
    //float kernelWeights[9] = float[](
    //    -1, -1, -1,
    //    -1,  9, -1,
    //    -1, -1, -1
    //);

    // kernel de detectar bordas
    //float kernelWeight[9] = float[](
    //     1,  1,  1,
    //     1, -9,  1,
    //     1,  1,  1
    //);

    // olha na textura quais são as cores dos vizinhos deste pixel
    vec3 neighborsColors[9];
    for (int i = 0; i < 9; i++) {
        neighborsColors[i] = texture(tex, texCoords + kernelOffsets[i]);
    }

    // aplica a convolução, fazendo com que a cor resultante deste pixel
    // seja uma combinação das cores dos pixels adjacentes (3x3) multiplicadas
    // pelos pesos (do kernel)
    vec3 resultingColor = vec3(0.0);
    for (int i = 0; i < 9; i++) {
        resultingColor += neighborsColors[i] * kernelWeights[i];
    }

    return resultingColor;
}

void main() {
    vec3 colorFromTexture = texture(u_texture, v_texCoords);
    gl_FragColor = vec4(invert(colorFromTexture), 1.0);
}