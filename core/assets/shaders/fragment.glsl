uniform sampler2D u_diffuseTexture;
uniform float u_time;
uniform vec4 u_dirLights0color;
uniform vec3 u_dirLights0direction;
uniform vec4 u_dirLights1color;
uniform vec3 u_dirLights1direction;

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec3 v_fragPosition;
varying vec3 v_fragNormal;

void main() 
{
    // DEBUG: esta cor pode ser usada para "depurar". Pinte o fragmento com ela,
    // que está azulzinha
    vec4 debugColor = vec4(0.5, 0.5, 1.0, 1.0);

    // DEBUG: use este if para mudar a cor para vermelho caso 
    // "algo esteja errado". No final, defina o gl_FragColor como esta cor...
    // se estiver vermelho quer dizer que entrou neste IF (sua condição está
    // verdadeira)
    if (length(u_dirLights0color) > 1.0) {
        debugColor.r = 1.0;
        debugColor.b = 0.5;
    }

    // para calcular a componente difusa, precisamos:
    vec3 normal = normalize(v_fragNormal);
    vec3 incidenciaLuz0 = -normalize(u_dirLights0direction);
    vec3 incidenciaLuz1 = -normalize(u_dirLights1direction);

    // para calcular a componente especular, precisamos:
    vec3 visualizacao = -normalize(v_fragPosition);
    vec3 reflexao0 = reflect(incidenciaLuz0, normal);
    vec3 reflexao1 = reflect(incidenciaLuz1, normal);

    // calcula as 3 componentes de Phong: ambiente, difusa, especular
    vec4 ambiente = vec4(0.15, 0.15, 0.15, 1.0) * texture2D(u_diffuseTexture, v_texCoords);

    // componente difusa, considerando cada fonte luminosa
    float cossenoDifusa0 = dot(normal, incidenciaLuz0);
    float cossenoDifusa1 = dot(normal, incidenciaLuz1);
    if (cossenoDifusa0 > 0.6667) {
        cossenoDifusa0 = 1;
    } else if (cossenoDifusa0 > 0.3333) {
        cossenoDifusa0 = 0.75;
    } else {
        cossenoDifusa0 = 0.3333;
    }
    if (cossenoDifusa1 > 0.6667) {
        cossenoDifusa1 = 1;
    } else if (cossenoDifusa1 > 0.3333) {
        cossenoDifusa1 = 0.75;
    } else {
        cossenoDifusa1 = 0.3333;
    }

    vec4 difusa = max(0.0, cossenoDifusa0) * texture2D(u_diffuseTexture, v_texCoords) * u_dirLights0color; 
    difusa += max(0.0, cossenoDifusa1) * texture2D(u_diffuseTexture, v_texCoords) * u_dirLights1color; 


    vec4 especular = vec4(0.0);
    if (dot(normal, incidenciaLuz0) < 0.0) {
        // luz está do lado de trás... não há contribuição
    } else {
        float cossenoEspecular0 = dot(visualizacao, reflexao0);
        if (cossenoEspecular0 > 0.6667) {
            cossenoEspecular0 = 1;
        } else if (cossenoEspecular0 > 0.3333) {
            cossenoEspecular0 = 0.75;
        } else {
            cossenoEspecular0 = 0.3333;
        }
        especular = pow(max(0.0, cossenoEspecular0), 100.0) * u_dirLights0color;
    }
    if (dot(normal, incidenciaLuz1) < 0.0) {
        // luz está do lado de trás... não há contribuição
    } else {
        float cossenoEspecular1 = dot(visualizacao, reflexao1);
        if (cossenoEspecular1 > 0.6667) {
            cossenoEspecular1 = 1;
        } else if (cossenoEspecular1 > 0.3333) {
            cossenoEspecular1 = 0.75;
        } else {
            cossenoEspecular1 = 0.3333;
        }
        especular += pow(max(0.0, cossenoEspecular1), 30.0) * u_dirLights1color;
    }



    // Dá o resultado
    gl_FragColor = ambiente + difusa + especular * 0.25;

    // DEBUG: para depurar, use a linha a seguir para definir a cor do fragmento
    //gl_FragColor = debugColor;
}
