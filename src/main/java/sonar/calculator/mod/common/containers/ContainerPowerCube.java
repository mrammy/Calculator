package sonar.calculator.mod.common.containers;

import ic2.api.item.IElectricItem;
import ic2.api.item.ISpecialElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.calculator.mod.common.tileentity.machines.TileEntityPowerCube;
import sonar.core.inventory.ContainerSync;
import sonar.core.utils.DischargeValues;
import sonar.core.utils.SonarAPI;
import cofh.api.energy.IEnergyContainerItem;

public class ContainerPowerCube extends ContainerSync {
	private TileEntityPowerCube entity;

	public ContainerPowerCube(InventoryPlayer inventory, TileEntityPowerCube entity) {
		super(entity);
		this.entity = entity;

		addSlotToContainer(new Slot(entity, 0, 80, 34));
		addSlotToContainer(new Slot(entity, 1, 28, 60));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(p_82846_2_);

		if ((slot != null) && (slot.getHasStack())) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if ((p_82846_2_ != 1) && (p_82846_2_ != 0)) {

				if (itemstack1.getItem() instanceof IEnergyContainerItem) {
					if (!mergeItemStack(itemstack1, 0, 2, false)) {
						return null;
					}
				} else if (SonarAPI.ic2Loaded() && itemstack1.getItem() instanceof ISpecialElectricItem) {
					ISpecialElectricItem container = (ISpecialElectricItem) itemstack1.getItem();
					if (!mergeItemStack(itemstack1, 0, 2, false)) {
						return null;
					}
				} else if (SonarAPI.ic2Loaded() && itemstack1.getItem() instanceof IElectricItem) {
					if (!mergeItemStack(itemstack1, 0, 2, false)) {
						return null;
					}

				} else if (DischargeValues.discharge().value(itemstack1) > 0) {
					if (!mergeItemStack(itemstack1, 1, 2, false)) {
						return null;
					}
				} else if (SonarAPI.ic2Loaded() && itemstack1.getItem() instanceof IElectricItem) {
					if (!mergeItemStack(itemstack1, 1, 2, false)) {
						return null;
					}
				} else if (SonarAPI.ic2Loaded() && itemstack1.getItem() instanceof ISpecialElectricItem) {
					if (!mergeItemStack(itemstack1, 1, 2, false)) {
						return null;
					}
				} else if ((p_82846_2_ >= 3) && (p_82846_2_ < 30)) {
					if (!mergeItemStack(itemstack1, 29, 38, false)) {
						return null;
					}
				} else if ((p_82846_2_ >= 29) && (p_82846_2_ < 38) && (!mergeItemStack(itemstack1, 2, 29, false))) {
					return null;
				}
			} else if (!mergeItemStack(itemstack1, 2, 38, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(p_82846_1_, itemstack1);
		}

		return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return entity.isUseableByPlayer(player);
	}

}
