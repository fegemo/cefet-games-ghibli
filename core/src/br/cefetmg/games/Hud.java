package br.cefetmg.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author fegemo
 */
public class Hud implements Disposable {

    private final Stage stage;
    private final Skin skin;
    private final Label rotuloCreditosMeiLink, rotuloCreditosTotoroLink, rotuloCreditosFonteLink;
    private final Window janelaCreditos;//, janelaInstrucoes;
    private final Table layout;
    private final BitmapFont font15;
    private final BitmapFont font32;
    private final Label rotuloVertices;
    private final Label rotuloTitulo;
    private final Label rotuloPosProcessamento;

    public Hud() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("hud/ghibli.ttf"));
        FreeTypeFontParameter fontParams = new FreeTypeFontParameter();
        fontParams.size = 15;
        font15 = generator.generateFont(fontParams);
        font15.getData().markupEnabled = true;
        fontParams.size = 32;
        font32 = generator.generateFont(fontParams);
        generator.dispose();

        stage = new Stage(new ScreenViewport());
        skin = new Skin();
        skin.add("ghibli-font", font15, BitmapFont.class);
        skin.add("ghibli-font-32", font32, BitmapFont.class);
        skin.addRegions(new TextureAtlas(Gdx.files.internal("hud/uiskin.atlas")));
        skin.load(Gdx.files.internal("hud/uiskin.json"));
        BitmapFont defaultFont = skin.get("default-font", BitmapFont.class);
        defaultFont.getData().markupEnabled = true;

        rotuloCreditosMeiLink = new Label("https://sketchfab.com/3d-models/mei-5478ddd14bf044e59e02bda57ec46edb", skin);
        rotuloCreditosTotoroLink = new Label("https://sketchfab.com/3d-models/totoro-f1fdde319ed64170b13bf044b3739ead", skin);
        rotuloCreditosFonteLink = new Label("https://www.myfonts.com/fonts/alsamman/ghibli/", skin);
        for (Label link : new Label[]{rotuloCreditosMeiLink, rotuloCreditosTotoroLink, rotuloCreditosFonteLink}) {
            link.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.net.openURI(((Label) event.getTarget()).getText().toString());
                }
            });
            link.addListener(new InputListener() {
                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    ((Label) event.getTarget()).setColor(Color.WHITE);
                }

                @Override

                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    ((Label) event.getTarget()).setColor(new Color(0.5f, 0.5f, 1, 1));
                }

            });
        }

        // configura a janelinha dos créditos
        janelaCreditos = new Window("Creditos", skin);
        janelaCreditos.setMovable(true);

        janelaCreditos.add("Modelo da [#4ed36dff]Mei[]:").padRight(10).right();
        janelaCreditos.add("cgart.com no Sketchfab").expandX().left();
        janelaCreditos.row();
        janelaCreditos.add();
        janelaCreditos.add(rotuloCreditosMeiLink).expandX().left();
        janelaCreditos.row();

        janelaCreditos.add("Modelo do [#4ed36dff]Totoro[]:").padRight(10).right();
        janelaCreditos.add("Nico Caraballo - theniloart no Sketchfab").expandX().left();
        janelaCreditos.row();
        janelaCreditos.add();
        janelaCreditos.add(rotuloCreditosTotoroLink).expandX().left();
        janelaCreditos.row();

        janelaCreditos.add("Fonte [#4ed36dff]Ghibli[]:").padRight(10).right();
        janelaCreditos.add("Eyad Al-Samman").expandX().left();
        janelaCreditos.row();
        janelaCreditos.add();
        janelaCreditos.add(rotuloCreditosFonteLink).expandX().left();

        janelaCreditos.pack();
        final float alturaOriginalJanelaCreditos = janelaCreditos.getHeight();
        janelaCreditos.setPosition(0, 0, Align.bottom | Align.center);
        janelaCreditos.getTitleTable().getCells().get(0).expandX();
        janelaCreditos.getTitleTable().addListener(new ClickListener() {
            private boolean isCollapsed = false;

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (this.getTapCount() > 1) {
                    isCollapsed = !isCollapsed;
                    if (isCollapsed) {
                        ((Window) event.getTarget().getParent().getParent()).setHeight(25);
                        ((Window) event.getTarget().getParent().getParent()).validate();
                    } else {
                        ((Window) event.getTarget().getParent().getParent()).setHeight(alturaOriginalJanelaCreditos);
                        ((Window) event.getTarget().getParent().getParent()).validate();
                    }
                }
            }

        });


        // configura título e número de vértices
        rotuloTitulo = new Label("Ghibli Shader Studio", skin, "ghibli-font-32", Color.BLACK);
        rotuloVertices = new Label("Vertices: ", skin, "ghibli-font", Color.WHITE);
        rotuloPosProcessamento = new Label("Pos-processamento: ", skin, "ghibli-font", Color.WHITE);
        
        // configura o layout (tabela) raiz
        layout = new Table(skin);
        layout.setFillParent(true);
        layout.add(rotuloTitulo).padBottom(5).center();
        layout.row();
        layout.add(rotuloVertices).padRight(5).right();
        layout.row();
        layout.add(rotuloPosProcessamento).padRight(5).right();
        layout.row();
        layout.add().expandY();
        layout.row();
        layout.add(janelaCreditos);

        stage.addActor(layout);
    }

    public InputProcessor getInputProcessor() {
        return stage;
    }
    
    public void setVertices(int vertices) {
        rotuloVertices.setText("[#000000ff]Vertices: [#4ed36dff]" + NumberFormat.getNumberInstance(Locale.getDefault()).format(vertices));
    }
    
    public void setPosProcessamento(boolean ativado) {
        rotuloPosProcessamento.setText("[#000000ff]Pos-processamento: [#4ed36dff]" + (ativado ? "ativado" : "desativado") + " [#000000ff][P]");
    }

    public void update(float dt) {
        stage.act(dt);
    }

    public void render() {
        stage.draw();
    }

    public void resize(int w, int h) {
        stage.getViewport().update(w, h);
    }

    @Override
    public void dispose() {
        stage.dispose();
        font15.dispose();
    }
}
