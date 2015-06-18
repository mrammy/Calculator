package sonar.calculator.mod.client.models;

import sonar.calculator.mod.common.tileentity.misc.TileEntityFluxPlug;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class ModelFluxPlug extends ModelBase {
	// fields
	ModelRenderer Centre;
	ModelRenderer Support1;
	ModelRenderer Support2;
	ModelRenderer Support3;
	ModelRenderer Support4;
	ModelRenderer Support5;
	ModelRenderer Support6;
	ModelRenderer Side1;
	ModelRenderer Side2;
	ModelRenderer Side3;
	ModelRenderer Side4;
	ModelRenderer Side5;
	ModelRenderer Side6;

	public ModelFluxPlug() {
		textureWidth = 64;
		textureHeight = 32;

		Centre = new ModelRenderer(this, 0, 16);
		Centre.addBox(0F, 0F, 0F, 8, 8, 8);
		Centre.setRotationPoint(-4F, 12F, -4F);
		Centre.setTextureSize(64, 32);
		Centre.mirror = true;
		setRotation(Centre, 0F, 0F, 0F);
		Support1 = new ModelRenderer(this, 0, 0);
		Support1.addBox(0F, 0F, 0F, 2, 3, 2);
		Support1.setRotationPoint(-1F, 9F, -1F);
		Support1.setTextureSize(64, 32);
		Support1.mirror = true;
		setRotation(Support1, 0F, 0F, 0F);
		Support2 = new ModelRenderer(this, 0, 0);
		Support2.addBox(0F, 0F, 0F, 2, 3, 2);
		Support2.setRotationPoint(-1F, 20F, -1F);
		Support2.setTextureSize(64, 32);
		Support2.mirror = true;
		setRotation(Support2, 0F, 0F, 0F);
		Support3 = new ModelRenderer(this, 0, 0);
		Support3.addBox(0F, 0F, 0F, 2, 3, 2);
		Support3.setRotationPoint(7F, 15F, -1F);
		Support3.setTextureSize(64, 32);
		Support3.mirror = true;
		setRotation(Support3, 0F, 0F, 1.570796F);
		Support4 = new ModelRenderer(this, 0, 0);
		Support4.addBox(0F, 0F, 0F, 2, 3, 2);
		Support4.setRotationPoint(-4F, 15F, -1F);
		Support4.setTextureSize(64, 32);
		Support4.mirror = true;
		setRotation(Support4, 0F, 0F, 1.570796F);
		Support5 = new ModelRenderer(this, 0, 0);
		Support5.addBox(0F, 0F, 0F, 2, 3, 2);
		Support5.setRotationPoint(-1F, 15F, -4F);
		Support5.setTextureSize(64, 32);
		Support5.mirror = true;
		setRotation(Support5, -1.570796F, 0F, 0F);
		Support6 = new ModelRenderer(this, 0, 0);
		Support6.addBox(0F, 0F, 0F, 2, 3, 2);
		Support6.setRotationPoint(-1F, 15F, 7F);
		Support6.setTextureSize(64, 32);
		Support6.mirror = true;
		setRotation(Support6, -1.570796F, 0F, 0F);
		Side1 = new ModelRenderer(this, 0, 5);
		Side1.addBox(0F, 0F, 0F, 6, 6, 1);
		Side1.setRotationPoint(-3F, 13F, -8F);
		Side1.setTextureSize(64, 32);
		Side1.mirror = true;
		setRotation(Side1, 0F, 0F, 0F);
		Side2 = new ModelRenderer(this, 0, 5);
		Side2.addBox(0F, 0F, 0F, 6, 6, 1);
		Side2.setRotationPoint(-3F, 13F, 7F);
		Side2.setTextureSize(64, 32);
		Side2.mirror = true;
		setRotation(Side2, 0F, 0F, 0F);
		Side3 = new ModelRenderer(this, 0, 5);
		Side3.addBox(0F, 0F, 0F, 6, 6, 1);
		Side3.setRotationPoint(-8F, 13F, 3F);
		Side3.setTextureSize(64, 32);
		Side3.mirror = true;
		setRotation(Side3, 0F, 1.570796F, 0F);
		Side4 = new ModelRenderer(this, 0, 5);
		Side4.addBox(0F, 0F, 0F, 6, 6, 1);
		Side4.setRotationPoint(7F, 13F, 3F);
		Side4.setTextureSize(64, 32);
		Side4.mirror = true;
		setRotation(Side4, 0F, 1.570796F, 0F);
		Side5 = new ModelRenderer(this, 0, 5);
		Side5.addBox(0F, 0F, 0F, 6, 6, 1);
		Side5.setRotationPoint(-3F, 9F, -3F);
		Side5.setTextureSize(64, 32);
		Side5.mirror = true;
		setRotation(Side5, 1.570796F, 0F, 0F);
		Side6 = new ModelRenderer(this, 0, 5);
		Side6.addBox(0F, 0F, 0F, 6, 6, 1);
		Side6.setRotationPoint(-3F, 24F, -3F);
		Side6.setTextureSize(64, 32);
		Side6.mirror = true;
		setRotation(Side6, 1.570796F, 0F, 0F);
	}

	public void render(TileEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(null, f, f1, f2, f3, f4, f5);
		setRotationAngles(null, f, f1, f2, f3, f4, f5);
		Centre.render(f5);
		if (entity != null && entity.getWorldObj() != null) {
			TileEntityFluxPlug flux = (TileEntityFluxPlug) entity;
			if (flux.hasEnergyHandler(ForgeDirection.UP)) {
				Support1.render(f5);
				Side5.render(f5);
			}
			if (flux.hasEnergyHandler(ForgeDirection.DOWN)) {
				Support2.render(f5);
				Side6.render(f5);
			}
			if (flux.hasEnergyHandler(ForgeDirection.NORTH)) {
				Support6.render(f5);
				Side2.render(f5);
			}
			if (flux.hasEnergyHandler(ForgeDirection.SOUTH)) {
				Support5.render(f5);
				Side1.render(f5);
			}
			if (flux.hasEnergyHandler(ForgeDirection.EAST)) {
				Support3.render(f5);
				Side4.render(f5);
			}
			if (flux.hasEnergyHandler(ForgeDirection.WEST)) {
				Support4.render(f5);
				Side3.render(f5);
			}
		} else {
			Support1.render(f5);
			Support2.render(f5);
			Support3.render(f5);
			Support4.render(f5);
			Support5.render(f5);
			Support6.render(f5);
			Side1.render(f5);
			Side2.render(f5);
			Side3.render(f5);
			Side4.render(f5);
			Side5.render(f5);
			Side6.render(f5);
		}
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

}
