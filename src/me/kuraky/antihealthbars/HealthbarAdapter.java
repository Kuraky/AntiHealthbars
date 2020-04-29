package me.kuraky.antihealthbars;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_15_R1.DataWatcher;

import java.lang.reflect.Field;
import java.util.List;

public class HealthbarAdapter extends PacketAdapter {

    public HealthbarAdapter() {
        super(AntiHealthbars.getINSTANCE(), ListenerPriority.HIGHEST, PacketType.Play.Server.ENTITY_METADATA);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        int entityId = event.getPacket().getIntegers().read(0);
        int playerId = event.getPlayer().getEntityId();

        if(entityId != playerId) {
            Object packet = event.getPacket().deepClone().getHandle();
            try {
                Field list = packet.getClass().getDeclaredField("b");
                list.setAccessible(true);

                List<DataWatcher.Item> items = (List<DataWatcher.Item>) list.get(packet);

                for (DataWatcher.Item item : items) {
                    if (item.a().hashCode() == 8) { //index for health in metadata
                        Field b = item.getClass().getDeclaredField("b"); //it's stored there
                        b.setAccessible(true);
                        try {
                            if ((Float) b.get(item) > 0) { //we don't want to change player's health if it's equal to 0, otherwise death animation won't play
                                b.set(item, AntiHealthbars.getINSTANCE().getCustomHealth());
                                event.setPacket(PacketContainer.fromPacket(packet));
                            }
                        } catch (ClassCastException | IllegalArgumentException ignored) { //the class cast exception will be catched a lot, due to 8 also being an index for other values
                        }
                        break;
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                ProtocolLibrary.getProtocolManager().removePacketListener(this);
            }
        }
    }
}
