package net.satisfy.foundation.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.satisfy.foundation.Foundation;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class LetsDoConfigScreen extends Screen {
    private static final int BUTTON_SIZE = 20;
    private static final int ICON_SIZE = 16;
    private static final int ICON_GAP = 4;
    private static final int WIDE_BUTTON_WIDTH = 200;
    private static final int LOGO_BOX_WIDTH = 220;
    private static final int LOGO_BOX_HEIGHT = 80;
    private static final int GAP = 12;

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
    private final ResourceLocation logo;
    private int logoTextureWidth;
    private int logoTextureHeight;
    private int logoX;
    private int logoY;
    private int logoWidth;
    private int logoHeight;
    private int versionY;

    public LetsDoConfigScreen(@Nullable Screen parent, String modId, Function<Screen, Screen> configScreen) {
        super(Component.literal(Platform.getOptionalMod(modId).map(Mod::getName).orElse(modId)));
        this.parent = parent;
        this.configScreen = configScreen;
        this.version = Component.literal(Platform.getOptionalMod(modId).map(mod -> "v" + mod.getVersion()).orElse(""))
                .withStyle(ChatFormatting.GRAY);
        this.logo = ResourceLocation.fromNamespaceAndPath(modId, "textures/gui/logo.png");
        readLogoSize();
    }

    private void readLogoSize() {
        Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(logo);
        if (resource.isEmpty()) {
            return;
        }
        try (InputStream stream = resource.get().open(); NativeImage image = NativeImage.read(stream)) {
            logoTextureWidth = image.getWidth();
            logoTextureHeight = image.getHeight();
        } catch (IOException e) {
            Foundation.LOGGER.warn("Could not read logo {}", logo, e);
        }
    }

    private boolean hasLogo() {
        return logoTextureWidth > 0 && logoTextureHeight > 0;
    }

    @Override
    protected void init() {
        int centerX = width / 2;

        int restHeight = GAP + BUTTON_SIZE + GAP + BUTTON_SIZE + GAP + font.lineHeight + GAP + BUTTON_SIZE;
        int boxWidth = Math.min(LOGO_BOX_WIDTH, width - 40);
        int boxHeight = Math.max(font.lineHeight, Math.min(LOGO_BOX_HEIGHT, height - 20 - restHeight));
        if (hasLogo()) {
            float scale = Math.min((float) boxWidth / logoTextureWidth, (float) boxHeight / logoTextureHeight);
            logoWidth = Math.round(logoTextureWidth * scale);
            logoHeight = Math.round(logoTextureHeight * scale);
        } else {
            logoWidth = boxWidth;
            logoHeight = font.lineHeight;
        }

        int y = Math.max(10, (height - boxHeight - restHeight) / 2);
        logoX = centerX - logoWidth / 2;
        logoY = y + (boxHeight - logoHeight) / 2;
        y += boxHeight + GAP;

        addRenderableWidget(Button.builder(Component.translatable("gui.foundation.config.configure"), button -> minecraft.setScreen(configScreen.apply(this)))
                .bounds(centerX - WIDE_BUTTON_WIDTH / 2, y, WIDE_BUTTON_WIDTH, BUTTON_SIZE)
                .build());
        y += BUTTON_SIZE + GAP;

        int rowWidth = LINKS.size() * BUTTON_SIZE + (LINKS.size() - 1) * ICON_GAP;
        int x = centerX - rowWidth / 2;
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
        y += BUTTON_SIZE + GAP;

        versionY = y;
        y += font.lineHeight + GAP;

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .bounds(centerX - WIDE_BUTTON_WIDTH / 2, y, WIDE_BUTTON_WIDTH, BUTTON_SIZE)
                .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        if (hasLogo()) {
            RenderSystem.enableBlend();
            graphics.blit(logo, logoX, logoY, logoWidth, logoHeight, 0, 0, logoTextureWidth, logoTextureHeight, logoTextureWidth, logoTextureHeight);
            RenderSystem.disableBlend();
        } else {
            graphics.drawCenteredString(font, title, width / 2, logoY, 0xFFFFFF);
        }
        graphics.drawCenteredString(font, version, width / 2, versionY, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    private record Link(String id, String url) {
    }
}
