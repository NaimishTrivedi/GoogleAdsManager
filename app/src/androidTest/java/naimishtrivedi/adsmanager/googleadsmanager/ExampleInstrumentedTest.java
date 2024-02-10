/*
 * Created by Naimish Trivedi on 06/02/24, 11:13 am
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 01/02/24, 1:01 pm
 */

package naimishtrivedi.adsmanager.googleadsmanager;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.oceanmtech.googleadsmanager", appContext.getPackageName());
    }
}