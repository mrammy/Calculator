package sonar.calculator.mod.common.item.tools;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import sonar.calculator.mod.CalculatorConfig;
import sonar.calculator.mod.CalculatorItems;
import sonar.core.utils.helpers.FontHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CalcSword extends ItemSword {
	ToolMaterial type;

	public CalcSword(ToolMaterial material) {
		super(material);
		type = material;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		super.onLeftClickEntity(stack, player, entity);
		if (type == CalculatorItems.FireDiamond) {
			entity.setFire(4);
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		super.addInformation(stack, player, list, par4);
		if (!CalculatorConfig.isEnabled(stack)) {
			list.add(FontHelper.translate("calc.ban"));
		}
	}

	@Override
	public boolean isItemTool(ItemStack stack) {
		return true;
	}
}
