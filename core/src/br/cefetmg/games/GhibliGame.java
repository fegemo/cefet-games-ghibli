package br.cefetmg.games;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader.ObjLoaderParameters;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultRenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GhibliGame extends ApplicationAdapter {

    private ModelBatch modelBatch;
    private Environment environment;
    private Shader meuPrimeiroShader;
    
    private Viewport viewport;
    private Camera camera;
    private CameraInputController cameraController;
    private ModelInstance cameraFocus;
    private float cameraAnimationTime = 0;
    private static final float CAMERA_MOVEMENT_DURATION = 2f;
    
    private Model totoroModel, meiModel;
    private ModelInstance totoro, mei;
    private AnimationController meiWalking;
    private float light1Angle = 0;
    private DirectionalLight light1;
    private DirectionalLight light2;

    @Override
    public void create() {
//        spriteBatch = new SpriteBatch();
//        String vertexShaderSource = Gdx.files.internal("shaders/vertex.glsl").readString();
//        String fragmentShaderSource = Gdx.files.internal("shaders/fragment.glsl").readString();
//        ShaderProgram phong = new ShaderProgram(vertexShaderSource, fragmentShaderSource);
        meuPrimeiroShader = new MeuPrimeiroShaderS2();
//        modelBatch = new ModelBatch(new DefaultShaderProvider(vertexShaderSource, fragmentShaderSource), new DefaultRenderableSorter());
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1f));
        light1 = new DirectionalLight().set(new Color(0.5f, 0.5f, 0.5f, 1.0f), new Vector3(-5f, -0.8f, -5.2f));
        light2 = new DirectionalLight().set(new Color(0.5f, 0.5f, 0.5f, 1.0f), new Vector3(5f, 0.8f, 5.2f));
        environment.add(light1);
        environment.add(light2);

        camera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 15f, 30f);
        camera.lookAt(Vector3.Zero);
        camera.near = 0.1f;
        camera.far = 3000f;
        camera.update();
        viewport = new ScreenViewport(camera);

        ModelLoader objLoader = new ObjLoader();
        ModelLoader fbxLoader = new G3dModelLoader(new JsonReader());
        totoroModel = objLoader.loadModel(Gdx.files.internal("models/totoronico.obj"), new ObjLoaderParameters(true));
        totoro = new ModelInstance(totoroModel);
        totoro.transform.setToTranslation(10, 0, 0);
        totoro.transform.mul(new Matrix4().setToScaling(20, 20, 20));
        totoro.transform.mul(new Matrix4().setToRotation(Vector3.Y, -30));
        
//        Renderable totoroRenderable = new Renderable();
//        totoro.getRenderable(totoroRenderable);
//        totoroRenderable.environment = environment;
        
        
        meiModel = fbxLoader.loadModel(Gdx.files.internal("models/mei.g3dj"));
        mei = new ModelInstance(meiModel);
        mei.transform.setToTranslation(-10, -8, 0);
        mei.transform.mul(new Matrix4().setToScaling(8, 8, 8));
        mei.transform.mul(new Matrix4().setToRotation(Vector3.Y, 30));
        meiWalking = new AnimationController(mei);
        meiWalking.setAnimation("Bind_Joint_GRP|running", -1);

        cameraController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(cameraController);
        
        meuPrimeiroShader.init();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        
        atualizaFontesDeLuz(dt);
        atualizaCamera(dt);
        atualizaAnimacao(dt);
        verificaInput();
        
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(camera);
        modelBatch.render(totoro, environment, meuPrimeiroShader);
        modelBatch.render(mei, environment);
        modelBatch.end();
    }

        
    private void atualizaFontesDeLuz(float dt) {
        light1Angle += dt;
        if (light1Angle > Math.PI * 2) {
            light1Angle -= Math.PI * 2;
        }
        light1.direction.set((float)Math.cos(light1Angle) * 5, -1, (float) Math.sin(light1Angle) * 5);
    }
    
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
    
    private void atualizaAnimacao(float dt) {
        meiWalking.update(dt);
    }
    
    private void verificaInput() {
        // verifica se teclas foram pressionadas
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
    
    @Override
    public void dispose() {
        modelBatch.dispose();
        totoroModel.dispose();
    }
}
