package net.bozkurt.dof.client.render;

import net.bozkurt.dof.DiceOfFate;
import net.bozkurt.dof.entity.ThrownDiceEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class ThrownDiceEntityRenderer extends EntityRenderer<ThrownDiceEntity> {
    private static final Identifier WHITE_TEXTURE = new Identifier(DiceOfFate.MOD_ID, "textures/entity/dice_white.png");
    private static final Identifier BLACK_TEXTURE = new Identifier(DiceOfFate.MOD_ID, "textures/entity/dice_black.png");
    private static final Identifier RED_TEXTURE = new Identifier(DiceOfFate.MOD_ID, "textures/entity/dice_red.png");
    private static final float SIZE = 0.15f;

    public ThrownDiceEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(ThrownDiceEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        
        float age = entity.age + tickDelta;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(age * 20.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(age * 15.0f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(age * 10.0f));
        
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getTexture(entity)));
        renderCube(matrices, vertexConsumer, light);
        
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    private void renderCube(MatrixStack matrices, VertexConsumer vertices, int light) {
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f positionMatrix = entry.getPositionMatrix();
        Matrix3f normalMatrix = entry.getNormalMatrix();

        // Front
        vertex(vertices, positionMatrix, normalMatrix, -SIZE, -SIZE, SIZE, 0, 1, 0, 0, 1, light);
        vertex(vertices, positionMatrix, normalMatrix, SIZE, -SIZE, SIZE, 1, 1, 0, 0, 1, light);
        vertex(vertices, positionMatrix, normalMatrix, SIZE, SIZE, SIZE, 1, 0, 0, 0, 1, light);
        vertex(vertices, positionMatrix, normalMatrix, -SIZE, SIZE, SIZE, 0, 0, 0, 0, 1, light);

        // Back
        vertex(vertices, positionMatrix, normalMatrix, SIZE, -SIZE, -SIZE, 0, 1, 0, 0, -1, light);
        vertex(vertices, positionMatrix, normalMatrix, -SIZE, -SIZE, -SIZE, 1, 1, 0, 0, -1, light);
        vertex(vertices, positionMatrix, normalMatrix, -SIZE, SIZE, -SIZE, 1, 0, 0, 0, -1, light);
        vertex(vertices, positionMatrix, normalMatrix, SIZE, SIZE, -SIZE, 0, 0, 0, 0, -1, light);

        // Top
        vertex(vertices, positionMatrix, normalMatrix, -SIZE, SIZE, SIZE, 0, 1, 0, 1, 0, light);
        vertex(vertices, positionMatrix, normalMatrix, SIZE, SIZE, SIZE, 1, 1, 0, 1, 0, light);
        vertex(vertices, positionMatrix, normalMatrix, SIZE, SIZE, -SIZE, 1, 0, 0, 1, 0, light);
        vertex(vertices, positionMatrix, normalMatrix, -SIZE, SIZE, -SIZE, 0, 0, 0, 1, 0, light);

        // Bottom
        vertex(vertices, positionMatrix, normalMatrix, -SIZE, -SIZE, -SIZE, 0, 1, 0, -1, 0, light);
        vertex(vertices, positionMatrix, normalMatrix, SIZE, -SIZE, -SIZE, 1, 1, 0, -1, 0, light);
        vertex(vertices, positionMatrix, normalMatrix, SIZE, -SIZE, SIZE, 1, 0, 0, -1, 0, light);
        vertex(vertices, positionMatrix, normalMatrix, -SIZE, -SIZE, SIZE, 0, 0, 0, -1, 0, light);

        // Right
        vertex(vertices, positionMatrix, normalMatrix, SIZE, -SIZE, SIZE, 0, 1, 1, 0, 0, light);
        vertex(vertices, positionMatrix, normalMatrix, SIZE, -SIZE, -SIZE, 1, 1, 1, 0, 0, light);
        vertex(vertices, positionMatrix, normalMatrix, SIZE, SIZE, -SIZE, 1, 0, 1, 0, 0, light);
        vertex(vertices, positionMatrix, normalMatrix, SIZE, SIZE, SIZE, 0, 0, 1, 0, 0, light);

        // Left
        vertex(vertices, positionMatrix, normalMatrix, -SIZE, -SIZE, -SIZE, 0, 1, -1, 0, 0, light);
        vertex(vertices, positionMatrix, normalMatrix, -SIZE, -SIZE, SIZE, 1, 1, -1, 0, 0, light);
        vertex(vertices, positionMatrix, normalMatrix, -SIZE, SIZE, SIZE, 1, 0, -1, 0, 0, light);
        vertex(vertices, positionMatrix, normalMatrix, -SIZE, SIZE, -SIZE, 0, 0, -1, 0, 0, light);
    }

    private void vertex(VertexConsumer vertices, Matrix4f positionMatrix, Matrix3f normalMatrix, 
                       float x, float y, float z, float u, float v, float nx, float ny, float nz, int light) {
        vertices.vertex(positionMatrix, x, y, z)
            .color(255, 255, 255, 255)
            .texture(u, v)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(light)
            .normal(normalMatrix, nx, ny, nz)
            .next();
    }

    @Override
    public Identifier getTexture(ThrownDiceEntity entity) {
        if (entity.isRed()) {
            return RED_TEXTURE;
        } else if (entity.isBlack()) {
            return BLACK_TEXTURE;
        }
        return WHITE_TEXTURE;
    }
}
