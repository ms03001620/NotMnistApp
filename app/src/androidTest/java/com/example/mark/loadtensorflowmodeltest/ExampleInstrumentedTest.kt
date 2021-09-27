package com.example.mark.loadtensorflowmodeltest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.ByteBuffer

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun useBitmap2() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        //                                          10                                      20                          27
        //[  0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   2   0 185 192   0   2   0   0]
        val bitmap: Bitmap = BitmapFactory.decodeResource(appContext.resources, R.drawable.a3)
        val pixels1 = IntArray(bitmap.getHeight() * bitmap.getWidth())
        bitmap.getPixels(pixels1, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight())
        for (i in pixels1.indices) {
            val aa1 = Color.alpha(pixels1[i])
            val rr1 = Color.red(pixels1[i])
            val gg1 = Color.green(pixels1[i])
            val bb1 = Color.blue(pixels1[i])
            if (aa1 != 0 || rr1 != 0 || gg1 != 0 || bb1 != 0) {
                Log.v("useBitmap2", "index:$i")
            }
        }
        Assert.assertTrue(Color.red(pixels1[15]) == 0)
        Assert.assertTrue(Color.green(pixels1[15]) == 0)
        Assert.assertTrue(Color.blue(pixels1[15]) == 0)
        Assert.assertTrue(Color.alpha(pixels1[15]) == 159)
    }
}