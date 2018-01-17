package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.TimeUtils


class GameScreen(val game: Drop) : Screen {

    val dropImg: Texture
    val bucketImg: Texture
    val dropSound: Sound
    val rainMusic: Music
    val camera: OrthographicCamera
    val bucket: Rectangle
    val raindrops: ArrayList<Rectangle>
    var lastDropTime: Long = 0
    var dropsGathered: Int = 0
    val touchPos: Vector3

    init {
        dropImg = Texture(Gdx.files.internal("droplet.png"))
        bucketImg = Texture(Gdx.files.internal("bucket.png"))

        dropSound = Gdx.audio.newSound(Gdx.files.internal("waterdrop.wav"))
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("undertreeinrain.mp3"))

        rainMusic.isLooping = true
        //rainMusic.play()

        bucket = Rectangle()
        bucket.x = (800 / 2 - 64 / 2).toFloat() // what
        bucket.y = 20F
        bucket.width = 64f
        bucket.height = 64f

        touchPos = Vector3()

        raindrops = ArrayList()
        spawnRainDrop()

        camera = OrthographicCamera()
        camera.setToOrtho(false, 800f, 480f)
    }

    override fun hide() {
    }

    override fun show() {
        rainMusic.play()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()

        game.batch.projectionMatrix = camera.combined
        game.batch.begin()
        game.batch.draw(bucketImg, bucket.x, bucket.y)
        game.font.draw(game.batch, "Drops: " + dropsGathered, 0f, 480f)
        for (rainDrop in raindrops) {
            game.batch.draw(dropImg, rainDrop.x, rainDrop.y)
        }
        game.batch.end()

        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRainDrop()

        var iterator = raindrops.iterator()
        while (iterator.hasNext()) {
            var rainDrop = iterator.next()
            rainDrop.y -= 200 * Gdx.graphics.deltaTime
            if (rainDrop.y + 64 < 0) iterator.remove()
            if (rainDrop.overlaps(bucket)) {
                dropsGathered++
                dropSound.play()
                iterator.remove()
            }
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX().toFloat(), Gdx.input.getY().toFloat(), 0f)
            camera.unproject(touchPos)
            bucket.x = touchPos.x - 64 / 2
        }

        // keyboard support
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.deltaTime
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.deltaTime

        if (bucket.x < 0) bucket.x = 0f
        if (bucket.x > 800 - 64) bucket.x = (800 - 64).toFloat()
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun dispose() {
        dropSound.dispose()
        bucketImg.dispose()
        dropImg.dispose()
        rainMusic.dispose()
        //game.batch.dispose()
    }

    private fun spawnRainDrop() {
        var newRainDrop = Rectangle()
        newRainDrop.x = MathUtils.random(0, 800 - 64).toFloat()
        newRainDrop.y = 480f
        newRainDrop.width = 64f
        newRainDrop.height = 64f
        raindrops.add(newRainDrop)
        lastDropTime = TimeUtils.nanoTime()
    }
}