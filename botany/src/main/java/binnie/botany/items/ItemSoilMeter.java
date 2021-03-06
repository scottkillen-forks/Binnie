package binnie.botany.items;

import binnie.botany.CreativeTabBotany;
import binnie.botany.EnumHelper;
import binnie.botany.api.gardening.EnumAcidity;
import binnie.botany.api.gardening.EnumMoisture;
import binnie.botany.api.gardening.IBlockSoil;
import binnie.botany.api.gardening.IGardeningManager;
import binnie.botany.core.BotanyCore;
import binnie.core.util.I18N;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSoilMeter extends Item implements IItemModelRegister {
	public ItemSoilMeter() {
		setCreativeTab(CreativeTabBotany.INSTANCE);
		setTranslationKey("botany.soil_meter");
		setMaxStackSize(1);
		setRegistryName("soil_meter");
	}

	public static String getPH(ItemStack stack, boolean withColor, boolean byNeutralNone) {
		EnumAcidity ph = EnumAcidity.values()[stack.getItemDamage() / 3];
		if (byNeutralNone) {
			if (ph == EnumAcidity.NEUTRAL) {
				return "";
			}
		}
		return TextFormatting.GRAY + I18N.localise("botany.ph") + ": " + EnumHelper.getLocalisedName(ph, withColor);
	}

	public static String getMoisture(ItemStack stack, boolean withColor, boolean byNormalNone) {
		EnumMoisture moisure = EnumMoisture.values()[stack.getItemDamage() % 3];
		if (byNormalNone) {
			if (moisure == EnumMoisture.NORMAL) {
				return "";
			}
		}
		return TextFormatting.GRAY + I18N.localise("botany.moisture") + ": " + EnumHelper.getLocalisedName(moisure, withColor);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		Block block = worldIn.getBlockState(pos).getBlock();
		IGardeningManager gardening = BotanyCore.getGardening();
		if (!gardening.isSoil(block)) {
			pos = pos.down();
			block = worldIn.getBlockState(pos).getBlock();
		}

		if (!gardening.isSoil(block)) {
			pos = pos.down();
			block = worldIn.getBlockState(pos).getBlock();
		}

		if (gardening.isSoil(block) && worldIn.isRemote) {
			IBlockSoil soil = (IBlockSoil) block;
			String info = I18N.localise("botany.soil.type") + ": "
				+ EnumHelper.getLocalisedName(soil.getType(worldIn, pos), true) + ", "
				+ TextFormatting.WHITE + I18N.localise("botany.moisture") + ": "
				+ EnumHelper.getLocalisedName(soil.getMoisture(worldIn, pos), true) + ", "
				+ TextFormatting.WHITE + I18N.localise("botany.ph") + ": "
				+ EnumHelper.getLocalisedName(soil.getPH(worldIn, pos), true);

			ITextComponent chat = new TextComponentString(info);
			player.sendStatusMessage(chat, false);
		}
		return EnumActionResult.SUCCESS;
	}
}
