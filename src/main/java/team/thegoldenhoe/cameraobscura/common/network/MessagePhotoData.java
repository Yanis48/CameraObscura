package team.thegoldenhoe.cameraobscura.common.network;

import java.util.Comparator;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePhotoData implements IMessage {

	/** photo name */
	public String name;

	/** photo data */
	public byte[] data;

	/** order used when handling multiple packets of this type */
	public short order;

	/** unique id - used to group multiple messages together */
	public int uuid;

	/** number of bytes total that messages with this uuid should send */
	public int length;

	public MessagePhotoData() {
		super();
	}

	public MessagePhotoData(int uuid, String name, byte[] data, short order, int length) {
		this.name = name;
		this.data = data;
		this.uuid = uuid;
		this.order = order;
		this.length = length;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.uuid = buf.readInt();
		this.order = buf.readShort();
		this.length = buf.readInt();
		this.name = ByteBufUtils.readUTF8String(buf);
		this.data = new byte[buf.readableBytes()];
		buf.readBytes(this.data);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.uuid);
		buf.writeShort(this.order);
		buf.writeInt(this.length);
		ByteBufUtils.writeUTF8String(buf, this.name);
		buf.writeBytes(this.data);
	}

	public static final class Handler implements IMessageHandler<MessagePhotoData, IMessage> {

		@Override
		public IMessage onMessage(MessagePhotoData message, MessageContext ctx) {
			EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
			// temporary, obviously...or is it?
			serverPlayer.getServerWorld().addScheduledTask(() -> {
				serverPlayer.inventory.addItemStackToInventory(new ItemStack(Items.DIAMOND, 1));
			});

			System.out.println("Num bytes recv:" + message.data.length);
			return null;
		}}

	public static final Comparator<MessagePhotoData> COMPARATOR = new Comparator<MessagePhotoData>() {
		@Override
		public int compare(MessagePhotoData m1, MessagePhotoData m2) {
			return m1.order - m2.order;
		}
	};
}
