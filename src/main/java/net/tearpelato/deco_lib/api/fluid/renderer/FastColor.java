package net.tearpelato.deco_lib.api.fluid.renderer;

import net.minecraft.util.Mth;

public class FastColor {

    private FastColor() {}

    public static int toChannel8Bit(float value) {
        return Mth.floor(value * 255.0F);
    }

    public static final class ABGR32 {

        private ABGR32() {

        }

        public static int getA(int color) {
            return color >>> 24;
        }

        public static int getR(int color) {
            return color & 0xFF;
        }

        public static int getG(int color) {
            return (color >>> 8) & 0xFF;
        }

        public static int getB(int color) {
            return (color >>> 16) & 0xFF;
        }

        public static int makeTransparent(int color) {
            return color & 0xFFFFFF;
        }

        public static int makeOpaque(int color) {
            return color | 0xFF000000;
        }

        public static int fromComponents(int a, int b, int g, int r) {
            return (a << 24) | (b << 16) | (g << 8) | r;
        }

        public static int applyAlpha(int alpha, int rgb) {
            return (alpha << 24) | (rgb & 0xFFFFFF);
        }

        public static int convertFromArgb(int color) {
            int a = (color >>> 24) & 0xFF;
            int r = (color >>> 16) & 0xFF;
            int g = (color >>> 8) & 0xFF;
            int b = color & 0xFF;
            return (a << 24) | (b << 16) | (g << 8) | r;
        }
    }

    public static final class ARGB32 {

        private ARGB32() {}

        public static int alpha(int color) {
            return (color >>> 24) & 0xFF;
        }

        public static int red(int color) {
            return (color >>> 16) & 0xFF;
        }

        public static int green(int color) {
            return (color >>> 8) & 0xFF;
        }

        public static int blue(int color) {
            return color & 0xFF;
        }

        public static int fromRGBA(int a, int r, int g, int b) {
            return (a << 24) | (r << 16) | (g << 8) | b;
        }

        public static int fromRGB(int r, int g, int b) {
            return fromRGBA(255, r, g, b);
        }

        public static int multiply(int c1, int c2) {
            int a = alpha(c1) * alpha(c2) / 255;
            int r = red(c1) * red(c2) / 255;
            int g = green(c1) * green(c2) / 255;
            int b = blue(c1) * blue(c2) / 255;
            return fromRGBA(a, r, g, b);
        }

        public static int lerp(float delta, int c1, int c2) {
            int a = Mth.lerpInt(delta, alpha(c1), alpha(c2));
            int r = Mth.lerpInt(delta, red(c1), red(c2));
            int g = Mth.lerpInt(delta, green(c1), green(c2));
            int b = Mth.lerpInt(delta, blue(c1), blue(c2));
            return fromRGBA(a, r, g, b);
        }

        public static int withFullAlpha(int color) {
            return color | 0xFF000000;
        }

        public static int withAlpha(int alpha, int rgb) {
            return (alpha << 24) | (rgb & 0xFFFFFF);
        }

        public static int fromFloats(float a, float r, float g, float b) {
            return fromRGBA(
                    FastColor.toChannel8Bit(a),
                    FastColor.toChannel8Bit(r),
                    FastColor.toChannel8Bit(g),
                    FastColor.toChannel8Bit(b)
            );
        }

        public static int average(int c1, int c2) {
            int a = (alpha(c1) + alpha(c2)) / 2;
            int r = (red(c1) + red(c2)) / 2;
            int g = (green(c1) + green(c2)) / 2;
            int b = (blue(c1) + blue(c2)) / 2;
            return fromRGBA(a, r, g, b);
        }
    }
}