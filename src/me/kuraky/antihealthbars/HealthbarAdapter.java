package me.kuraky.antihealthbars;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_16_R2.DataWatcher;

import java.lang.reflect.Field;
import java.util.List;

public class HealthbarAdapter extends PacketAdapter {

    public HealthbarAdapter() {
        super(AntiHealthbars.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Server.ENTITY_METADATA);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        int entityId = event.getPacket().getIntegers().read(0);
        int playerId = event.getPlayer().getEntityId();

        if(entityId != playerId) {
            Object packet = event.getPacket().deepClone().getHandle();
            try {
                Field itemsField = packet.getClass().getDeclaredField("b");
                itemsField.setAccessible(true);
                List<DataWatcher.Item<?>> items = (List<DataWatcher.Item<?>>) itemsField.get(packet);

                for (DataWatcher.Item<?> item : items) {
                    //index for health in metadata is 8, however there are also other values with the same index(e.g. item frame rotation)
                    //health however is the only float with the index 8, hence we check it's class
                    if (item.a().hashCode() == 8 && item.b().getClass() == Float.class) {
                        Field healthField = item.getClass().getDeclaredField("b");
                        healthField.setAccessible(true);
                        float health = (Float) healthField.get(item);
                        if (health > 0) { //we don't want to change entity's health if it's equal to 0, otherwise death animation won't play
                            healthField.set(item, AntiHealthbars.getInstance().getCustomHealth());
                            event.setPacket(PacketContainer.fromPacket(packet));
                        }
                        break;
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException | ClassCastException | NoClassDefFoundError e) {
                e.printStackTrace();
                ProtocolLibrary.getProtocolManager().removePacketListener(this);
            }
        }
    }
}
