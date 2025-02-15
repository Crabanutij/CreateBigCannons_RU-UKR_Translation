package rbasamoyai.createbigcannons.cannons.cannonend;

import java.util.List;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import rbasamoyai.createbigcannons.cannons.CannonBehavior;
import rbasamoyai.createbigcannons.cannons.ICannonBlockEntity;
import rbasamoyai.createbigcannons.cannons.cannonend.ScrewBreechBlock.OpenState;

public class ScrewBreechBlockEntity extends KineticTileEntity implements ICannonBlockEntity {

	private CannonBehavior cannonBehavior;
	private float openProgress;
	
	public ScrewBreechBlockEntity(BlockEntityType<? extends ScrewBreechBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		behaviours.add(this.cannonBehavior = new CannonBehavior(this, this::canLoadBlock));
	}
	
	@Override public boolean canLoadBlock(StructureBlockInfo blockInfo) { return false; }

	@Override public CannonBehavior cannonBehavior() { return this.cannonBehavior; }
	
	@Override
	public void tick() {
		super.tick();
		
		if (this.getSpeed() == 0) return;
		float progress = this.getOpeningSpeed();
		if (Math.abs(progress) > 0) {
			this.openProgress = Mth.clamp(this.openProgress + progress, 0.0f, 1.0f);
		}
		
		OpenState openState = this.getBlockState().getValue(ScrewBreechBlock.OPEN);
		if (this.openProgress <= 0 && openState != OpenState.CLOSED) {
			this.level.setBlock(this.worldPosition, this.getBlockState().setValue(ScrewBreechBlock.OPEN, OpenState.CLOSED), 3);
		} else if (this.openProgress >= 1 && openState != OpenState.OPEN) {
			this.level.setBlock(this.worldPosition, this.getBlockState().setValue(ScrewBreechBlock.OPEN, OpenState.OPEN), 3);
		} else if (this.openProgress > 0 && this.openProgress < 1 && openState != OpenState.PARTIAL) {
			this.level.setBlock(this.worldPosition, this.getBlockState().setValue(ScrewBreechBlock.OPEN, OpenState.PARTIAL), 3);
		}
	}
	
	public float getOpeningSpeed() {
		return Math.abs(this.getSpeed()) > 0 ? this.getSpeed() / 512.0f : 0.0f;
	}
	
	public float getRenderedBlockOffset(float partialTicks) {
		return Mth.clamp(this.openProgress + this.getOpeningSpeed() * partialTicks, 0.0f, 1.0f);
	}
	
	public float getOpenProgress() { return this.openProgress; }
	public void setOpenProgress(float openProgress) { this.openProgress = openProgress; }
	
	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putFloat("Progress", this.openProgress);
	}
	
	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		this.openProgress = compound.getFloat("Progress");
	}

}
