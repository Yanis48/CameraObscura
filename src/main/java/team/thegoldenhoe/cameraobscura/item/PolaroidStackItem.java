package team.thegoldenhoe.cameraobscura.item;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import team.thegoldenhoe.cameraobscura.common.capability.CameraCapabilities;
import team.thegoldenhoe.cameraobscura.common.capability.ICameraStorageNBT;
import team.thegoldenhoe.cameraobscura.common.capability.ICameraStorageNBT.PolaroidStackStorage;

public class PolaroidStackItem extends Item {

	public PolaroidStackItem(Settings settings) {
		super(settings);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		int numPrintsRemaining = PolaroidStackStorage.MAX_SAVES;
		if (stack.getTag() == null) {
			tooltip.add(new LiteralText("Empty"));
		}

		ICameraStorageNBT.PolaroidStackStorage storage = stack.getCapability(CameraCapabilities.getPolaroidStackCapability(), null);
		if (storage != null) {
			ArrayList<String> paths = storage.getSavedImagePaths();
			numPrintsRemaining = storage.getMaxSaves() - paths.size();
			tooltip.add(new LiteralText("Prints Remaining: " + numPrintsRemaining).formatted(Formatting.AQUA, Formatting.BOLD));
			tooltip.add(new LiteralText("Usable in polaroid camera").formatted(Formatting.DARK_PURPLE, Formatting.ITALIC));
			if (!storage.canSave()) {
				tooltip.add(new LiteralText("Contains Photo").formatted(Formatting.ITALIC));
			}
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return CameraCapabilities.getProvider(CameraCapabilities.getPolaroidStackCapability(), () -> {
			PolaroidStackStorage ret = new PolaroidStackStorage() {
				@Override
				public void saveImage(String path, EntityPlayer player) {
					super.saveImage(path, player);
					stack.setTagCompound(serializeNBT());

					if (!player.world.isRemote) {
						ItemStack polaroidPhoto = new ItemStack(ItemRegistry.polaroidSingle);
						polaroidPhoto.setTagCompound(new NBTTagCompound());
						polaroidPhoto.getTagCompound().setString("Photo", path);

						player.addItemStackToInventory(polaroidPhoto);
					}
				}
			};
			if (stack.hasTagCompound()) {
				ret.deserializeNBT(stack.getTagCompound());
			}
			return ret;
		});
	}
}
