package com.plussy.game.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;

public class Drop implements ApplicationListener {

    OrthographicCamera camera;
    SpriteBatch batch;

    Texture appleImage;
    Texture basketImage;
    Sound dropSound;

    Rectangle basket;

    Array<Rectangle> appledrops;

    long lastDropTime;

    @Override
    public void create() {
        // загрузка изображений для капли и ведра, 64x64 пикселей каждый
        appleImage = new Texture(Gdx.files.internal("apple.png"));
        basketImage = new Texture(Gdx.files.internal("basket.png"));

        // загрузка звукового эффекта падающей капли и фоновой "музыки" дождя
        dropSound = Gdx.audio.newSound(Gdx.files.internal("waterdrop.wav"));

        // создается камера и SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 800);
        batch = new SpriteBatch();

        // создается Rectangle для представления ведра
        basket = new Rectangle();
        // центрируем ведро по горизонтали
        basket.x = 480 / 2 - 88 / 2;
        // размещаем на 20 пикселей выше нижней границы экрана.
        basket.y = 20;
        basket.width = 88;
        basket.height = 50;

        // создает массив капель и возрождает первую
        appledrops = new Array<Rectangle>();
        spawnRaindrop();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // сообщает камере, что нужно обновить матрицы
        camera.update();

        // сообщаем SpriteBatch о системе координат
        // визуализации указанной для камеры.
        batch.setProjectionMatrix(camera.combined);

        // начинаем новую серию, рисуем ведро и
        // все капли
        batch.begin();
        batch.draw(basketImage, basket.x, basket.y);
        for(Rectangle raindrop: appledrops) {
            batch.draw(appleImage, raindrop.x, raindrop.y);
        }
        batch.end();

        // обработка пользовательского ввода
        if(Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            basket.x = touchPos.x - 88 / 2;
        }

        // убедитесь что ведро остается в пределах экрана
        if(basket.x < 0) basket.x = 0;
        if(basket.x > 480 - 88) basket.x = 480 - 88;

        // проверка, нужно ли создавать новую каплю
        if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

        // движение капли, удаляем все капли выходящие за границы экрана
        // или те, что попали в ведро. Воспроизведение звукового эффекта
        // при попадании.
        Iterator<Rectangle> iter = appledrops.iterator();
        while(iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if(raindrop.y + 62 < 0) iter.remove();
            if(raindrop.overlaps(basket)) {
                dropSound.play();
                iter.remove();
            }
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        appleImage.dispose();
        basketImage.dispose();
        dropSound.dispose();
        batch.dispose();
    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 480-58);
        raindrop.y = 800;
        raindrop.width = 58;
        raindrop.height = 64;
        appledrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }
}