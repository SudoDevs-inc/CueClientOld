package cn.origin.cube.utils.render.particle;

import javax.vecmath.Tuple2f;
import javax.vecmath.Vector2f;

import cn.origin.cube.module.modules.client.ClickGui;
import cn.origin.cube.utils.render.ColorUtil;
import cn.origin.cube.utils.render.Render2DUtil;
import net.minecraft.client.gui.ScaledResolution;

public final class ParticleSystem
{
    private final int PARTS = 200;
    private final Particle[] particles;
    private ScaledResolution scaledResolution;

    public ParticleSystem(final ScaledResolution scaledResolution) {
        this.particles = new Particle[200];
        this.scaledResolution = scaledResolution;
        for (int i = 0; i < 200; ++i) {
            this.particles[i] = new Particle(new Vector2f((float)(Math.random() * scaledResolution.getScaledWidth()), (float)(Math.random() * scaledResolution.getScaledHeight())));
        }
    }

    public void update() {
        for (int i = 0; i < 200; ++i) {
            final Particle particle = this.particles[i];
            if (this.scaledResolution != null) {
                final boolean isOffScreenX = particle.getPos().x > this.scaledResolution.getScaledWidth() || particle.getPos().x < 0.0f;
                final boolean isOffScreenY = particle.getPos().y > this.scaledResolution.getScaledHeight() || particle.getPos().y < 0.0f;
                if (isOffScreenX || isOffScreenY) {
                    particle.respawn(this.scaledResolution);
                }
            }
            particle.update();
        }
    }

    public void render(final int mouseX, final int mouseY) {
        if (!ClickGui.INSTANCE.particles.getValue()) {
            return;
        }
        for (int i = 0; i < 200; ++i) {
            final Particle particle = this.particles[i];
            for (int j = 1; j < 200; ++j) {
                if (i != j) {
                    final Particle otherParticle = this.particles[j];
                    final Vector2f diffPos = new Vector2f(particle.getPos());
                    diffPos.sub((Tuple2f)otherParticle.getPos());
                    final float diff = diffPos.length();
                    final int distance = ClickGui.INSTANCE.partLength.getValue() / ((this.scaledResolution.getScaleFactor() <= 1) ? 3 : this.scaledResolution.getScaleFactor());
                    if (diff < distance) {
                        final int lineAlpha = (int)map(diff, distance, 0.0, 0.0, 127.0);
                        if (lineAlpha > 8) {
                            Render2DUtil.drawLine(particle.getPos().x + particle.getSize() / 2.0f, particle.getPos().y + particle.getSize() / 2.0f, otherParticle.getPos().x + otherParticle.getSize() / 2.0f, otherParticle.getPos().y + otherParticle.getSize() / 2.0f, 1.0f, Particle.changeAlpha(ColorUtil.toRGBA(ClickGui.getCurrentColor().getRed(),ClickGui.getCurrentColor().getGreen(),ClickGui.getCurrentColor().getBlue()), lineAlpha));
                        }
                    }
                }
            }
            particle.render(mouseX, mouseY);
        }
    }

    public static double map(double value, final double a, final double b, final double c, final double d) {
        value = (value - a) / (b - a);
        return c + value * (d - c);
    }

    public ScaledResolution getScaledResolution() {
        return this.scaledResolution;
    }

    public void setScaledResolution(final ScaledResolution scaledResolution) {
        this.scaledResolution = scaledResolution;
    }
}
