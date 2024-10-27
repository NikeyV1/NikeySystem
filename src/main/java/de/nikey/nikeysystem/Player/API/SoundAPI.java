package de.nikey.nikeysystem.Player.API;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;

public class SoundAPI {
    public static void sendResourcePack(Audience target, ResourcePackInfo info) {
        final ResourcePackRequest request = ResourcePackRequest.resourcePackRequest()
                .packs(info)
                .prompt(Component.text("System needs download!"))
                .required(true)
                .build();

        target.sendResourcePacks(request);
    }
}
