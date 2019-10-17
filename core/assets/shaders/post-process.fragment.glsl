#version 120

uniform sampler2D u_texture;

varying vec2 v_texCoords;
varying vec3 v_fragPosition;


// aplica uma convolução no pixel usando o kernel especificado
vec3 convolucao(sampler2D tex, vec2 texCoords, float[9] kernel) {
    // cria um vetor 3x3 contendo o deslocamento de cada pixel adjacente a este
    // (do kernel)
    float offset = 1.0 / 300.0;
    // deslocamentos dos pixels vizinhos:
    // |  (-1,  1)  ( 0,  1)  ( 1,  1)  |
    // |  (-1,  0)  ( 0,  0)  ( 1,  0)  |
    // |  (-1, -1)  ( 0, -1)  ( 1, -1)  |
    vec2 deslocamentos[9];
    deslocamentos[0] = vec2(-offset,  offset);      // cima-esquerda
    deslocamentos[1] = vec2(      0,  offset);      // cima-meio
    deslocamentos[2] = vec2( offset,  offset);      // cima-direita
    deslocamentos[3] = vec2(-offset,       0);      // meio-esquerda
    deslocamentos[4] = vec2(      0,       0);      // meio-meio
    deslocamentos[5] = vec2( offset,       0);      // meio-direita
    deslocamentos[6] = vec2(-offset, -offset);      // baixo-esquerda
    deslocamentos[7] = vec2(      0, -offset);      // baixo-meio
    deslocamentos[8] = vec2( offset, -offset);      // baixo-direita

    // olha na textura quais são as cores dos vizinhos deste pixel
    vec3 corVizinhos[9];
    for (int i = 0; i < 9; i++) {
        corVizinhos[i] = texture2D(tex, texCoords + deslocamentos[i]).xyz;
    }

    // aplica a convolução, fazendo com que a cor resultante deste pixel
    // seja uma combinação das cores dos pixels adjacentes (3x3) multiplicadas
    // pelos pesos (do kernel)
    vec3 corResultante = vec3(0.0);
    for (int i = 0; i < 9; i++) {
        corResultante += corVizinhos[i] * kernel[i];
    }

    return corResultante;
}


// retorna cor invertida
vec3 inverter(vec3 cor) {
    // não é preciso verificar se está negativo porque o GLSL faz "clamp" 
    // do valor no intervalo [0, 1]
    return cor;
}


void main() {
    vec3 corDoPixel = texture2D(u_texture, v_texCoords).rgb;
    gl_FragColor = vec4(corDoPixel, 1.0);
    // gl_FragColor = vec4(inverter(corDoPixel), 1.0);
    // gl_FragColor = vec4(paraCinza(corDoPixel), 1.0);
    // gl_FragColor = vec4(paraSepia(corDoPixel), 1.0);
    // gl_FragColor = vec4(borrar(u_texture, v_texCoords), 1.0);
    // gl_FragColor = vec4(agucar(u_texture, v_texCoords), 1.0);
    // gl_FragColor = vec4(bordas(u_texture, v_texCoords), 1.0);
    // gl_FragColor = vec4(toonShading(u_texture, v_texCoords), 1.0);
}