package com.mygdx.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
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

@Deprecated("unused")
class MyGdxGame : ApplicationAdapter() {
    private lateinit var dropImage: Texture
    private lateinit var bucketImage: Texture
    private lateinit var dropSound: Sound
    private lateinit var rainMusic: Music
    private lateinit var camera: OrthographicCamera
    private lateinit var batch: SpriteBatch
    private lateinit var bucket: Rectangle
    private lateinit var touchPos: Vector3

    private lateinit var rainDropArray: ArrayList<Rectangle>
    private var lastDropTime: Long = 0

    override fun create() {
        dropImage = Texture(Gdx.files.internal("droplet.png"))
        bucketImage = Texture(Gdx.files.internal("bucket.png"))

        dropSound = Gdx.audio.newSound(Gdx.files.internal("waterdrop.wav"))
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("undertreeinrain.mp3"))

        rainMusic.isLooping = true
        rainMusic.play()

        bucket = Rectangle()
        bucket.x = (800 / 2 - 64 / 2).toFloat() // what
        bucket.y = 20F
        bucket.width = 64f
        bucket.height = 64f

        touchPos = Vector3()
        rainDropArray = ArrayList()
        spawnRainDrop()
        camera = OrthographicCamera()
        camera.setToOrtho(false, 800f, 480f)

        batch = SpriteBatch()
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()

        batch.projectionMatrix = camera.combined
        batch.begin()
        batch.draw(bucketImage, bucket.x, bucket.y)
        for (rainDrop in rainDropArray) {
            batch.draw(dropImage, rainDrop.x, rainDrop.y)
        }
        batch.end()

        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRainDrop()

        var iterator = rainDropArray.iterator()
        while (iterator.hasNext()) {
            var rainDrop = iterator.next()
            rainDrop.y -= 200 * Gdx.graphics.deltaTime
            if (rainDrop.y + 64 < 0) iterator.remove()
            if (rainDrop.overlaps(bucket)) {
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

    private fun spawnRainDrop() {
        var newRainDrop = Rectangle()
        newRainDrop.x = MathUtils.random(0, 800 - 64).toFloat()
        newRainDrop.y = 480f
        newRainDrop.width = 64f
        newRainDrop.height = 64f
        rainDropArray.add(newRainDrop)
        lastDropTime = TimeUtils.nanoTime()
    }

    override fun dispose() {
        super.dispose()
        dropSound.dispose()
        bucketImage.dispose()
        dropImage.dispose()
        rainMusic.dispose()
        batch.dispose()
    }
}
