package br.cefetmg.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 *
 * @author fegemo
 */
public class PostProcessingShader {
    private ShaderProgram program;

    public PostProcessingShader() {
        // lê shaders do arquivo e compila o programa shader
        String vertexSource = Gdx.files.internal("shaders/post-process.vertex.glsl").readString(); 
        String fragmentSource = Gdx.files.internal("shaders/post-process.fragment.glsl").readString(); 
        program = new ShaderProgram(vertexSource, fragmentSource);
        
        if (!program.isCompiled()) {
            throw new GdxRuntimeException(program.getLog());
        }
        
        ShaderProgram.pedantic = false;
    }

    public ShaderProgram getShaderProgram() {
        return program;
    }
}
