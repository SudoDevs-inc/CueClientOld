package cn.origin.cube.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;
import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {
    public static RenderItem itemRender;
    public static ICamera camera;
    public static Minecraft mc = Minecraft.getMinecraft();
    public static Tessellator tessellator;
    public static BufferBuilder builder;
    public static int deltaTime;
    private final static Matrix4f modelMatrix = new Matrix4f();
    private final static Matrix4f projectionMatrix = new Matrix4f();
    static Vec3d camPos = new Vec3d(0.0, 0.0, 0.0);

    static {
        Minecraft mc = Minecraft.getMinecraft();
        itemRender = mc.getRenderItem();
        camera = new Frustum();
        tessellator = Tessellator.getInstance();
        builder = RenderUtil.tessellator.getBuffer();
    }

    public static void prepare() {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GL11.glDisable(3553);
        glEnable(2848);
        GL11.glBlendFunc(770, 771);
    }

    public static void release() {
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        glEnable(3553);
        GL11.glPolygonMode(1032, 6914);
    }

    public static void glBillboard(float x, float y, float z) {
        float scale = 0.02666667f;
        GlStateManager.translate((double) x - mc.getRenderManager().renderPosX, (double) y - mc.getRenderManager().renderPosY, (double) z - mc.getRenderManager().renderPosZ);
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-mc.getRenderViewEntity().rotationYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.getRenderViewEntity().rotationPitch, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
    }


    public static Vec3d updateToCamera(final Vec3d vec) {
        return new Vec3d(vec.x - RenderUtil.mc.getRenderManager().viewerPosX, vec.y - RenderUtil.mc.getRenderManager().viewerPosY, vec.z - RenderUtil.mc.getRenderManager().viewerPosZ);
    }

    public static void glBillboardDistanceScaled(float x, float y, float z, Entity entity, float scale) {
        glBillboard(x, y, z);
        int distance = (int) entity.getDistance(x, y, z);
        float scaleDistance = (float) distance / 2.0f / (2.0f + (2.0f - scale));
        if (scaleDistance < 1.0f) {
            scaleDistance = 1.0f;
        }
        GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
    }


    public static void addBuilderVertex(final BufferBuilder bufferBuilder, final double x, final double y, final double z, final Color color) {
        bufferBuilder.pos(x, y, z).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f).endVertex();
    }

    public static Vec3d toScaledScreenPos(Vec3d posIn) {
        final Vector4f vector4f = getTransformedMatrix(posIn);

        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int width = scaledResolution.getScaledWidth();
        final int height = scaledResolution.getScaledHeight();

        vector4f.x = width / 2f + (0.5f * vector4f.x * width + 0.5f);
        vector4f.y = height / 2f - (0.5f * vector4f.y * height + 0.5f);
        final double posZ = isVisible(vector4f, width, height) ? 0.0 : -1.0;

        return new Vec3d(vector4f.x, vector4f.y, posZ);
    }

    private static Vector4f getTransformedMatrix(Vec3d posIn) {
        final Vec3d relativePos = camPos.subtract(posIn);
        final Vector4f vector4f = new Vector4f((float) relativePos.x, (float) relativePos.y, (float) relativePos.z, 1f);

        transform(vector4f, modelMatrix);
        transform(vector4f, projectionMatrix);

        if (vector4f.w > 0.0f) {
            vector4f.x *= -100000;
            vector4f.y *= -100000;
        } else {
            final float invert = 1f / vector4f.w;
            vector4f.x *= invert;
            vector4f.y *= invert;
        }

        return vector4f;
    }

    private static void transform(Vector4f vec, Matrix4f matrix) {
        final float x = vec.x;
        final float y = vec.y;
        final float z = vec.z;
        vec.x = x * matrix.m00 + y * matrix.m10 + z * matrix.m20 + matrix.m30;
        vec.y = x * matrix.m01 + y * matrix.m11 + z * matrix.m21 + matrix.m31;
        vec.z = x * matrix.m02 + y * matrix.m12 + z * matrix.m22 + matrix.m32;
        vec.w = x * matrix.m03 + y * matrix.m13 + z * matrix.m23 + matrix.m33;
    }

    public static void renderBorder(int x, int y, int width, int height, int lineWidth, Color color) {
        //top lines
        Gui.drawRect(x, y, width, y + lineWidth, color.getRGB());
        //left line
        Gui.drawRect(x, y, x + lineWidth, height, color.getRGB());
        //right line
        Gui.drawRect(width, y, width - lineWidth, height, color.getRGB());
        //bottom line
        Gui.drawRect(x, height, width, height - lineWidth, color.getRGB());
    }


    private static boolean isVisible(Vector4f pos, int width, int height) {
        double right = width;
        double left = pos.x;
        if (left >= 0.0D && left <= right) {
            right = height;
            left = pos.y;
            return left >= 0.0D && left <= right;
        }
        return false;
    }

    public static void drawPolygonOutline(double startDegree, double endDegree, int corners, int x, int y, int radius, float width, int color) {
        double increment = 360 / (double) corners;
        x += radius;
        y += radius;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GL11.glLineWidth(width);

        float a = (float)(color >> 24 & 255) / 255.0F;
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for(double i = startDegree; i <= endDegree; i+=increment) {
            bufferbuilder.pos(x-Math.cos(Math.toRadians(i))*radius, y-Math.sin(Math.toRadians(i))*radius, 0.0D).color(r, g, b, a).endVertex();
        }
        bufferbuilder.pos(x-Math.cos(Math.toRadians(endDegree))*radius, y-Math.sin(Math.toRadians(endDegree))*radius, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();
        GL11.glDisable(GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
