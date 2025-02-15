package rbasamoyai.createbigcannons.cannonmount;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.OrientedContraptionEntity;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.CBCEntityTypes;

public class PitchOrientedContraptionEntity extends OrientedContraptionEntity {

	public PitchOrientedContraptionEntity(EntityType<? extends PitchOrientedContraptionEntity> type, Level level) {
		super(type, level);
	}
	
	public static PitchOrientedContraptionEntity create(Level level, Contraption contraption, Direction initialOrientation) {
		PitchOrientedContraptionEntity entity = new PitchOrientedContraptionEntity(CBCEntityTypes.PITCH_ORIENTED_CONTRAPTION.get(), level);
		
		entity.setContraption(contraption);
		entity.setInitialOrientation(initialOrientation);
		entity.startAtInitialYaw();
		return entity;
	}
	
	@Override
	public ContraptionRotationState getRotationState() {
		return new CBCContraptionRotationState(this);
	}
	
	@Override
	public void applyLocalTransforms(PoseStack stack, float partialTicks) {
		float initialYaw = this.getInitialYaw();
		float pitch = this.getViewXRot(partialTicks);
		float yaw = this.getViewYRot(partialTicks) + initialYaw;
		
		stack.translate(-0.5f, 0.0f, -0.5f);
		
		TransformStack tstack = TransformStack.cast(stack)
				.nudge(this.getId())
				.centre()
				.rotateY(yaw);
		
		if (this.getInitialOrientation().getAxis() == Direction.Axis.X) {
			tstack.rotateZ(pitch);
		} else {
			tstack.rotateX(pitch);
		}
		tstack.unCentre();
	}
	
	@Override
	public Vec3 applyRotation(Vec3 localPos, float partialTicks) {
		localPos = VecHelper.rotate(localPos, this.getViewXRot(partialTicks), this.getInitialOrientation().getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
		localPos = VecHelper.rotate(localPos, this.getViewYRot(partialTicks), Direction.Axis.Y);
		localPos = VecHelper.rotate(localPos, this.getInitialYaw(), Direction.Axis.Y);
		return localPos;
	}
	
	@Override
	public Vec3 reverseRotation(Vec3 localPos, float partialTicks) {
		localPos = VecHelper.rotate(localPos, -this.getInitialYaw(), Direction.Axis.Y);
		localPos = VecHelper.rotate(localPos, -this.getViewYRot(partialTicks), Direction.Axis.Y);
		localPos = VecHelper.rotate(localPos, -this.getViewXRot(partialTicks), this.getInitialOrientation().getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
		return localPos;
	}

}
