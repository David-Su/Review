/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wx.base.util.tools.math;

import java.util.Random;

/**
 * A class that contains utility methods related to numbers.
 *
 * @hide Pending API council approval
 */
public final class MathTool {
    private static final Random sRandom = new Random();
    private static final float DEG_TO_RAD = 3.1415926f / 180.0f;
    private static final float RAD_TO_DEG = 180.0f / 3.1415926f;
    private static final Object lock = new Object();
    private static volatile MathTool instance;

    private MathTool() {
    }

    public static MathTool instance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new MathTool();
                }
            }
        }
        return instance;
    }

    public float abs(float v) {
        return v > 0 ? v : -v;
    }

    public int constrain(int amount, int low, int high) {
        return amount < low ? low : (amount > high ? high : amount);
    }

    public long constrain(long amount, long low, long high) {
        return amount < low ? low : (amount > high ? high : amount);
    }

    public float constrain(float amount, float low, float high) {
        return amount < low ? low : (amount > high ? high : amount);
    }

    public float log(float a) {
        return (float) Math.log(a);
    }

    public float exp(float a) {
        return (float) Math.exp(a);
    }

    public float pow(float a, float b) {
        return (float) Math.pow(a, b);
    }

    public float max(float a, float b) {
        return a > b ? a : b;
    }

    public float max(int a, int b) {
        return a > b ? a : b;
    }

    public float max(float a, float b, float c) {
        return a > b ? (a > c ? a : c) : (b > c ? b : c);
    }

    public float max(int a, int b, int c) {
        return a > b ? (a > c ? a : c) : (b > c ? b : c);
    }

    public float min(float a, float b) {
        return a < b ? a : b;
    }

    public float min(int a, int b) {
        return a < b ? a : b;
    }

    public float min(float a, float b, float c) {
        return a < b ? (a < c ? a : c) : (b < c ? b : c);
    }

    public float min(int a, int b, int c) {
        return a < b ? (a < c ? a : c) : (b < c ? b : c);
    }

    public float dist(float x1, float y1, float x2, float y2) {
        final float x = (x2 - x1);
        final float y = (y2 - y1);
        return (float) Math.hypot(x, y);
    }

    public float dist(float x1, float y1, float z1, float x2, float y2, float z2) {
        final float x = (x2 - x1);
        final float y = (y2 - y1);
        final float z = (z2 - z1);
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public float mag(float a, float b) {
        return (float) Math.hypot(a, b);
    }

    public float mag(float a, float b, float c) {
        return (float) Math.sqrt(a * a + b * b + c * c);
    }

    public float sq(float v) {
        return v * v;
    }

    public float dot(float v1x, float v1y, float v2x, float v2y) {
        return v1x * v2x + v1y * v2y;
    }

    public float cross(float v1x, float v1y, float v2x, float v2y) {
        return v1x * v2y - v1y * v2x;
    }

    public float radians(float degrees) {
        return degrees * DEG_TO_RAD;
    }

    public float degrees(float radians) {
        return radians * RAD_TO_DEG;
    }

    public float acos(float value) {
        return (float) Math.acos(value);
    }

    public float asin(float value) {
        return (float) Math.asin(value);
    }

    public float atan(float value) {
        return (float) Math.atan(value);
    }

    public float atan2(float a, float b) {
        return (float) Math.atan2(a, b);
    }

    public float tan(float angle) {
        return (float) Math.tan(angle);
    }

    public float lerp(float start, float stop, float amount) {
        return start + (stop - start) * amount;
    }

    public float norm(float start, float stop, float value) {
        return (value - start) / (stop - start);
    }

    public float map(float minStart, float minStop, float maxStart, float maxStop, float value) {
        return maxStart + (maxStart - maxStop) * ((value - minStart) / (minStop - minStart));
    }

    public int random(int howbig) {
        return (int) (sRandom.nextFloat() * howbig);
    }

    public int random(int howsmall, int howbig) {
        if (howsmall >= howbig)
            return howsmall;
        return (int) (sRandom.nextFloat() * (howbig - howsmall) + howsmall);
    }

    public float random(float howbig) {
        return sRandom.nextFloat() * howbig;
    }

    public float random(float howsmall, float howbig) {
        if (howsmall >= howbig)
            return howsmall;
        return sRandom.nextFloat() * (howbig - howsmall) + howsmall;
    }

    public void randomSeed(long seed) {
        sRandom.setSeed(seed);
    }
}
