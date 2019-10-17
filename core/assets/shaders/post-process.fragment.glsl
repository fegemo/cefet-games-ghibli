#version 120

uniform sampler2D u_texture;

varying vec2 v_texCoords;
varying vec3 v_fragPosition;

// retorna cor invertida
vec3 inverter(vec3 cor) {
    // não é preciso verificar se está negativo porque o GLSL faz "clamp" 
    // do valor no intervalo [0, 1]
    return vec3(1.0 - cor);
}

// retorna cor em escala de cinza
vec3 paraCinza(vec3 cor) {
    // float tomDeCinza = dot(vec3(0.33), color);                // versão "naive"
    float tomDeCinza = dot(vec3(0.2989, 0.5879, 0.1140), cor); // versão mais correta
    return vec3(tomDeCinza);
}

// retorna cor em modo sépia
vec3 paraSepia(vec3 cor) {
    mat3 transformacaoSepia = mat3(
        0.393, 0.769, 0.189,
        0.349, 0.686, 0.168,
        0.272, 0.534, 0.131
    );

    return transpose(transformacaoSepia) * cor;
    //return vec3[]
}

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

vec3 borrar(sampler2D tex, vec2 texCoords) {

    
    // kernel de blur
    // |  1, 2, 1  |
    // |  2, 4, 2  | /16.0
    // |  1, 2, 1  |
    float peso = 1.0 / 16.0;
    float kernel[9];
    kernel[0] = peso*1.0;
    kernel[1] = peso*2.0;
    kernel[2] = peso*1.0;

    kernel[3] = peso*2.0;
    kernel[4] = peso*4.0;
    kernel[5] = peso*2.0;

    kernel[6] = peso*1.0;
    kernel[7] = peso*2.0;
    kernel[8] = peso*1.0;

    return convolucao(tex, texCoords, kernel);
}

vec3 agucar(sampler2D tex, vec2 texCoords) {
    // kernel de aguçar imagem (sharpen)
    //
    //    -1, -1, -1,
    //    -1,  9, -1,
    //    -1, -1, -1
    //
    float kernel[9];
    kernel[0] = -1.0;
    kernel[1] = -1.0;
    kernel[2] = -1.0;

    kernel[3] = -1.0;
    kernel[4] =  9.0;
    kernel[5] = -1.0;

    kernel[6] = -1.0;
    kernel[7] = -1.0;
    kernel[8] = -1.0;

    return convolucao(tex, texCoords, kernel);
}

vec3 bordas(sampler2D tex, vec2 texCoords) {
    // kernel de detectar bordas
    //
    //     1,  1,  1,
    //     1, -9,  1,
    //     1,  1,  1
    //
    float kernel[9];
    kernel[0] =  1.0;
    kernel[1] =  1.0;
    kernel[2] =  1.0;

    kernel[3] =  1.0;
    kernel[4] = -9.0;
    kernel[5] =  1.0;

    kernel[6] =  1.0;
    kernel[7] =  1.0;
    kernel[8] =  1.0;

    return convolucao(tex, texCoords, kernel);
}

vec3 toonShading(sampler2D tex, vec2 texCoords) {
    vec3 contorno = inverter(paraCinza(bordas(u_texture, v_texCoords)));
    vec3 cor;
    if (length(contorno) < 0.7) {
        cor = vec3(0.0);
    } else {
        cor = texture2D(tex, texCoords).rgb;
    }
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