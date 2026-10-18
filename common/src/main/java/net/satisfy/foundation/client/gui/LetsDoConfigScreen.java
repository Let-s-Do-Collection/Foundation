package net.satisfy.foundation.client.gui;

import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.foundation.Foundation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class LetsDoConfigScreen extends Screen {
    private static final int BUTTON_SIZE = 20;
    private static final int ICON_SIZE = 16;
    private static final int ICON_GAP = 4;

    private static final List<Link> LINKS = List.of(
            new Link("website", "https://www.lets-do.ch"),
            new Link("github", "https://github.com/Let-s-Do-Collection"),
            new Link("discord", "https://discord.com/invite/Vqu6wYZwdZ"),
            new Link("kofi", "https://ko-fi.com/letsdo"),
            new Link("patreon", "https://www.patreon.com/cw/u78595058")
    );

    private final @Nullable Screen parent;
    private final Function<Screen, Screen> configScreen;
    private final Component version;

    public LetsDoConfigScreen(@Nullable Screen parent, String modId, Function<Screen, Screen> configScreen) {
        super(modName(modId));
        this.parent = parent;
        this.configScreen = configScreen;
        this.version = Component.literal(Platform.getOptionalMod(modId).map(mod -> "v" + mod.getVersion()).orElse(""))
                .withStyle(ChatFormatting.GRAY);
    }

    private static Component modName(String modId) {
        return Component.literal(Platform.getOptionalMod(modId).map(Mod::getName).orElse(modId));
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;

        addRenderableWidget(Button.builder(Component.translatable("gui.foundation.config.configure"), button -> minecraft.setScreen(configScreen.apply(this)))
                .bounds(centerX - 100, centerY - 20, 200, BUTTON_SIZE)
                .build());

        int rowWidth = LINKS.size() * BUTTON_SIZE + (LINKS.size() - 1) * ICON_GAP;
        int x = centerX - rowWidth / 2;
        int y = height - 64;
        for (Link link : LINKS) {
            Component label = Component.translatable("gui.foundation.config.link." + link.id());
            SpriteIconButton button = SpriteIconButton.builder(label, b -> ConfirmLinkScreen.confirmLinkNow(this, link.url()), true)
                    .sprite(ResourceLocation.fromNamespaceAndPath(Foundation.MOD_ID, "links/" + link.id()), ICON_SIZE, ICON_SIZE)
                    .size(BUTTON_SIZE, BUTTON_SIZE)
                    .build();
            button.setX(x);
            button.setY(y);
            button.setTooltip(Tooltip.create(label));
            addRenderableWidget(button);
            x += BUTTON_SIZE + ICON_GAP;
        }

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .bounds(centerX - 100, height - 36, 200, BUTTON_SIZE)
                .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 20, 0xFFFFFF);
        graphics.drawCenteredString(font, version, width / 2, 32, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    private record Link(String id, String url) {
    }
}
