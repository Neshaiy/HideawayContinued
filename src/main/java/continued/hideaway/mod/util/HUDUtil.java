package continued.hideaway.mod.util;

import continued.hideaway.mod.HideawayPlus;
import net.minecraft.world.BossEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HUDUtil {
    public static String getCurrentRoomName() {
        if (HideawayPlus.connected()) {
            BossEvent bar = HideawayPlus.client().getSingleplayerServer().getCustomBossEvents().getEvents().stream().findFirst().get();
            String text = bar.getName().getString();
            Pattern pattern = Pattern.compile("\\w*'s Room", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group().replace("'s Room", "");
            } else return null;
        } else return null;
    }
}
