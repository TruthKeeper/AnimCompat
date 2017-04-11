package com.tk.animcompat;

/**
 * <pre>
 *     author : TK
 *     time   : 2017/03/28
 *     desc   : 揭示动画配置
 * </pre>
 */
public class RevealOptions {
    private int centerX;
    private int centerY;
    private float startRadius;
    private float endRadius;
    private int during;

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public float getStartRadius() {
        return startRadius;
    }

    public float getEndRadius() {
        return endRadius;
    }

    public int getDuring() {
        return during;
    }

    private RevealOptions(Builder builder) {
        centerX = builder.centerX;
        centerY = builder.centerY;
        startRadius = builder.startRadius;
        endRadius = builder.endRadius;
        during = builder.during;
    }

    public static final class Builder {
        private int centerX;
        private int centerY;
        private float startRadius;
        private float endRadius;
        private int during;

        public Builder() {
        }

        public Builder centerX(int centerX) {
            this.centerX = centerX;
            return this;
        }

        public Builder centerY(int centerY) {
            this.centerY = centerY;
            return this;
        }

        public Builder startRadius(float startRadius) {
            this.startRadius = startRadius;
            return this;
        }

        public Builder endRadius(float endRadius) {
            this.endRadius = endRadius;
            return this;
        }

        public Builder during(int during) {
            this.during = during;
            return this;
        }

        public RevealOptions build() {
            return new RevealOptions(this);
        }
    }
}
