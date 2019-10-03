package br.cefetmg.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 *
 * @author fegemo
 */
public class MeuPrimeiroShaderS2 implements Shader {

    private ShaderProgram program;
    private Camera camera;
    private RenderContext context;
    private int u_projection;
    private int u_modelView;
    private int u_model;
    private int u_diffuseTexture;
    private int u_dirLights0color;
    private int u_dirLights0direction;
    private int u_dirLights1color;
    private int u_dirLights1direction;
    private int u_normalMatrix;

    @Override
    public void init() {
        String vertexSource = Gdx.files.internal("shaders/vertex.glsl").readString(); 
        String fragmentSource = Gdx.files.internal("shaders/fragment.glsl").readString(); 
        program = new ShaderProgram(vertexSource, fragmentSource);
        
        if (!program.isCompiled()) {
            throw new GdxRuntimeException(program.getLog());
        }
        u_projection = program.getUniformLocation("u_projection");
        u_model = program.getUniformLocation("u_model");
        u_modelView = program.getUniformLocation("u_modelView");
        u_diffuseTexture = program.getUniformLocation("u_diffuseTexture");
        u_dirLights0color = program.getUniformLocation("u_dirLights0color");
        u_dirLights0direction = program.getUniformLocation("u_dirLights0direction");
        u_dirLights1color = program.getUniformLocation("u_dirLights1color");
        u_dirLights1direction = program.getUniformLocation("u_dirLights1direction");
        u_normalMatrix = program.getUniformLocation("u_normalMatrix");
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        this.context = context;
        program.begin();
        program.setUniformMatrix(u_projection, camera.projection);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);
    }

    @Override
    public void render(Renderable renderable) {
        Matrix4 modelView = new Matrix4(camera.view).mul(renderable.worldTransform);
        program.setUniformMatrix(u_model, renderable.worldTransform);
        program.setUniformMatrix(u_modelView, modelView);
        renderable.meshPart.render(program);
        TextureAttribute textureAttribute = (TextureAttribute)renderable.material.get(TextureAttribute.Diffuse);
        program.setUniformi(u_diffuseTexture, context.textureBinder.bind(textureAttribute.textureDescription));
        program.setUniformMatrix(u_normalMatrix, new Matrix3().set(modelView).inv().transpose());
        Environment environment = renderable.environment;
        if (environment != null) {
            final DirectionalLightsAttribute dla = environment.get(DirectionalLightsAttribute.class, DirectionalLightsAttribute.Type);
            final Array<DirectionalLight> dirs = dla == null ? null : dla.lights;
            if (dirs.size > 0) {
                program.setUniformf(u_dirLights0color, dirs.get(0).color);
                program.setUniformf(u_dirLights0direction, dirs.get(0).direction);
            }
            if (dirs.size > 1) {
                program.setUniformf(u_dirLights1color, dirs.get(1).color);
                program.setUniformf(u_dirLights1direction, dirs.get(1).direction);
            }
        }
    }

    @Override
    public void end() {
        program.end();
    }

    @Override
    public void dispose() {
        program.dispose();
    }
}
