package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PlayScreen implements Screen {
    private MyGame game;

    public TextureAtlas atlas;

    private OrthogonalTiledMapRenderer renderer;
    private TmxMapLoader mapLoader;
    private TiledMap map;

    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Box2DDebugRenderer b2dr;
    private Character player;
    private box[] boxo;
    private locationToPut[] loce;
    private World world;
    public boolean onGround=true;
private Hud hud;
    public PlayScreen(MyGame game){
        atlas = new TextureAtlas("boxi.pack");
        this.game = game;

        gamecam = new OrthographicCamera();

        gamePort = new FitViewport(400/MyGame.PPM, 208/MyGame.PPM, gamecam);

        hud = new Hud(game.batch);


        mapLoader = new TmxMapLoader();
        if(Hud.level == 1)
            map = mapLoader.load("untitled.tmx");
        else if(Hud.level == 2)
            map = mapLoader.load("untitled2.tmx");
        else if(Hud.level == 3)
            map = mapLoader.load("untitled3.tmx");

        renderer = new OrthogonalTiledMapRenderer(map, 1/MyGame.PPM);
        gamecam.position.set(gamePort.getWorldWidth()/2, gamePort.getWorldHeight() / 2, 0);

        b2dr = new Box2DDebugRenderer();
        world = new World(new Vector2(0, 0), true);
        new B2WorldCreator(world, map, this);
        int i=0;

        boxo = new box[map.getLayers().get(2).getObjects().getCount()];
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){


            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            boxo[i]= new box(world, map, this, rect, i);
            i++;}
            i=0;
            loce = new locationToPut[map.getLayers().get(3).getObjects().getCount()];

            for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
                 Rectangle rect = ((RectangleMapObject) object).getRectangle();
                 loce[i]= new locationToPut(world, map, this, rect, i);
                 i++;
            }
        player = new Character(world, this, map);
        world.setContactListener(new WorldContactListener(this, player, boxo));
    }

    @Override
    public void show() {

    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    public boolean moving = false;
    private int x, y;
    public void handleInput(float dt){



        if(!moving) {


            if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                x = 0;
                y = 1;
                player.move(0, 1, dt, this);

            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)){
                x = 0;
                y = -1;
                player.move(0, -1, dt, this);

             }
            if (Gdx.input.isKeyJustPressed(Input.Keys.D)){
                x=1; y=0;
                player.move(1, 0, dt, this);

            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                x = -1;
                y = 0;
                player.move(-1, 0, dt, this);
            }

            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                MyGame.correct=0;
                hud.addDeath();
                game.setScreen(new PlayScreen(game));

            }


        }else{
            player.move(x,y,dt,this);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            MyGame.correct=0;
            Hud.resetRestart();
            game.setScreen(new Menu(game));

        }

        if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
            gamecam.zoom=2.5f;
        }else{
            gamecam.zoom=1;
        }





    }
    public void update(float dt){
        handleInput(dt);

        world.step(1/60f, 6, 2);

        player.update(dt);
        for(box dez : boxo) {
             dez.update(dt, loce);
        }

        gamecam.position.x = player.b2body.getPosition().x;
        gamecam.position.y = player.b2body.getPosition().y;


        gamecam.update();
        renderer.setView(gamecam);


        if(MyGame.correct == map.getLayers().get(3).getObjects().getCount()){
            if(Hud.level !=3) {
                MyGame.correct = 0;
                game.setScreen(new endGame(game));
            }else{
                MyGame.correct = 0;
                Hud.level = 1;
                game.setScreen(new finish(game));
            }
        }

    }

    @Override
    public void render(float delta) {
        update(delta);



        Gdx.gl.glClearColor(27f/255f,94f/255f,31f/255f,1);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();
        //b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);

        for(locationToPut loc : loce) {
            loc.draw(game.batch);
        }

        for(box dez : boxo) {
             dez.draw(game.batch);
        }



        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);

    }



    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
    }
}
