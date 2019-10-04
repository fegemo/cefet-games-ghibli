package br.cefetmg.games;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader.ObjLoaderParameters;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GhibliGame extends ApplicationAdapter {

    // referentes à renderização
    private ModelBatch modelBatch;
    private Environment environment;
    private Shader meuPrimeiroShader;
    private DirectionalLight light1;
    private DirectionalLight light2;
    private float light1Angle = 0;
    
    // referentes à câmera
    private Viewport viewport;
    private Camera camera;
    private CameraInputController cameraController;
    private InputMultiplexer worldAndHudInputProcessor;
    private ModelInstance cameraFocus;
    private float cameraAnimationTime = 0;
    private static final float CAMERA_MOVEMENT_DURATION = 2f;
    
    // modelos
    private Model totoroModel, meiModel;
    private ModelInstance totoro, mei;
    private AnimationController meiWalking;
    
    // hud
    private Hud hud;
    

    @Override
    public void create() {
        // instancia elementos de renderização
        meuPrimeiroShader = new MeuPrimeiroShaderS2();
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1f));
        light1 = new DirectionalLight().set(new Color(0.5f, 0.5f, 0.5f, 1.0f), new Vector3(-5f, -0.8f, -5.2f));
        light2 = new DirectionalLight().set(new Color(0.5f, 0.5f, 0.5f, 1.0f), new Vector3(5f, 0.8f, 5.2f));
        environment.add(light1);
        environment.add(light2);
        meuPrimeiroShader.init();

        // iniciliza câmera
        camera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 15f, 30f);
        camera.lookAt(Vector3.Zero);
        camera.near = 0.1f;
        camera.far = 3000f;
        camera.update();
        viewport = new ScreenViewport(camera);
        cameraController = new CameraInputController(camera);

        // inicializa modelos e instâncias de modelos
        ModelLoader objLoader = new ObjLoader();
        ModelLoader fbxLoader = new G3dModelLoader(new JsonReader());
        totoroModel = objLoader.loadModel(Gdx.files.internal("models/totoronico.obj"), new ObjLoaderParameters(true));
        totoro = new ModelInstance(totoroModel);
        totoro.transform.setToTranslation(10, 0, 0);
        totoro.transform.mul(new Matrix4().setToScaling(20, 20, 20));
        totoro.transform.mul(new Matrix4().setToRotation(Vector3.Y, -30));

        meiModel = fbxLoader.loadModel(Gdx.files.internal("models/mei.g3dj"));
        mei = new ModelInstance(meiModel);
        mei.transform.setToTranslation(-10, -8, 0);
        mei.transform.mul(new Matrix4().setToScaling(8, 8, 8));
        mei.transform.mul(new Matrix4().setToRotation(Vector3.Y, 30));
        meiWalking = new AnimationController(mei);
        meiWalking.setAnimation("Bind_Joint_GRP|running", -1);

        // elementos da HUD
        hud = new Hud();
        int quantidadeVertices = 0;
        for (Mesh m : meiModel.meshes) {
            quantidadeVertices += m.getNumVertices();
        }
        for (Mesh m : totoroModel.meshes) {
            quantidadeVertices += m.getNumVertices();
        }
        hud.setVertices(quantidadeVertices);
        worldAndHudInputProcessor = new InputMultiplexer(hud.getInputProcessor(), cameraController);
        Gdx.input.setInputProcessor(worldAndHudInputProcessor);
    }

    @Override
    public void resize(int width, int height) {
        // atualiza a viewport para manter a razão de aspecto
        viewport.update(width, height);
        hud.resize(width, height);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        
        // atualiza a cena
        atualizaFontesDeLuz(dt);
        atualizaCamera(dt);
        atualizaAnimacao(dt);
        hud.update(dt);
        verificaInput();
        
        // limpa e desenha a cena
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(camera);
        modelBatch.render(totoro, environment, meuPrimeiroShader);
        modelBatch.render(mei, environment);
        modelBatch.end();
        
        hud.render();
    }
        
    @Override
    public void dispose() {
        modelBatch.dispose();
        totoroModel.dispose();
        meiModel.dispose();
        hud.dispose();
    }

        
    /**
     * Atualiza a posição da fonte de luz1, que fica "girando"
     * @param dt tempo desde o último quadro em segundos.
     */
    private void atualizaFontesDeLuz(float dt) {
        light1Angle += dt;
        if (light1Angle > Math.PI * 2) {
            light1Angle -= Math.PI * 2;
        }
        light1.direction.set((float)Math.cos(light1Angle) * 5, -1, (float) Math.sin(light1Angle) * 5);
    }
    
    /**
     * Atualiza a câmera de acordo com o teclado e para o caso de ela estar
     * executando a animação de focar um personagem.
     * @param dt tempo desde o último quadro em segundos.
     */
    private void atualizaCamera(float dt) {
        // atualiza o controlador (input)
        cameraController.update();
        
        // atualiza para onde a câmera está olhando (se apertar TAB)
        if (cameraAnimationTime < CAMERA_MOVEMENT_DURATION && cameraFocus != null) {
            cameraAnimationTime += dt;
            float alpha = cameraAnimationTime / CAMERA_MOVEMENT_DURATION;
            Vector3 cameraFocusingTarget = new Vector3();
            cameraFocus.transform.getTranslation(cameraFocusingTarget);
            camera.direction.lerp(new Vector3(cameraFocusingTarget).sub(camera.position).nor(), alpha);
            camera.update();
        }

    }
    
    /**
     * Atualiza a animação da personagem Mei
     * @param dt tempo desde o último quadro em segundos.
     */
    private void atualizaAnimacao(float dt) {
        meiWalking.update(dt);
    }
    
    /**
     * Verifica se teclas foram pressionadas
     */
    private void verificaInput() {
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            // interrompe ou resume a animação
            meiWalking.paused = !meiWalking.paused;
        }
        if (Gdx.input.isKeyJustPressed(Keys.TAB)) {
            // define para qual modelo a câmera está olhando
            if (cameraFocus != totoro) {
                cameraFocus = totoro;
            } else {
                cameraFocus = mei;
            }
            cameraAnimationTime = 0;
        }
    }
}
