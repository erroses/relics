package it.hurts.sskirillss.relics.client.screen.description.widgets.ability;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.description.AbilityDescriptionScreen;
import it.hurts.sskirillss.relics.client.screen.description.widgets.base.AbstractDescriptionWidget;
import it.hurts.sskirillss.relics.client.screen.utils.ScreenUtils;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.leveling.PacketRelicTweak;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class AbilityRerollButtonWidget extends AbstractDescriptionWidget implements IHoverableWidget {
    private final AbilityDescriptionScreen screen;
    private final String ability;

    public AbilityRerollButtonWidget(int x, int y, AbilityDescriptionScreen screen, String ability) {
        super(x, y, 18, 18);

        this.screen = screen;
        this.ability = ability;
    }

    @Override
    public boolean isLocked() {
        return !RelicItem.mayPlayerReroll(MC.player, screen.stack, ability);
    }

    @Override
    public void onPress() {
        if (!isLocked())
            NetworkHandler.sendToServer(new PacketRelicTweak(screen.pos, ability, PacketRelicTweak.Operation.REROLL));
    }

    @Override
    public void renderButton(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        TextureManager manager = MC.getTextureManager();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, AbilityDescriptionScreen.TEXTURE);

        manager.bindForSetup(AbilityDescriptionScreen.TEXTURE);

        if (RelicItem.mayPlayerReroll(MC.player, screen.stack, ability)) {
            blit(poseStack, x, y, 278, 0, 18, 18, 512, 512);

            if (isHovered)
                blit(poseStack, x - 1, y - 1, 318, 0, 20, 20, 512, 512);
        } else {
            blit(poseStack, x, y, 278, 20, 18, 18, 512, 512);

            RelicAbilityEntry abilityData = RelicItem.getAbilityEntryData(screen.stack, ability);

            if (abilityData == null)
                return;

            if (RelicItem.canUseAbility(screen.stack, ability) && !abilityData.getStats().isEmpty() && isHovered)
                blit(poseStack, x - 1, y - 1, 318, 22, 20, 20, 512, 512);
        }
    }

    @Override
    public void onHovered(PoseStack poseStack, int mouseX, int mouseY) {
        if (!RelicItem.canUseAbility(screen.stack, ability))
            return;

        RelicAbilityEntry data = RelicItem.getAbilityEntryData(screen.stack, ability);

        if (data.getStats().isEmpty())
            return;

        List<FormattedCharSequence> tooltip = Lists.newArrayList();

        int maxWidth = 100;
        int renderWidth = 0;

        int requiredExperience = RelicItem.getRerollRequiredExperience(screen.stack, ability);

        int experience = MC.player.totalExperience;

        MutableComponent negativeStatus = Component.translatable("tooltip.relics.relic.status.negative").withStyle(ChatFormatting.RED);
        MutableComponent positiveStatus = Component.translatable("tooltip.relics.relic.status.positive").withStyle(ChatFormatting.GREEN);

        List<MutableComponent> entries = Lists.newArrayList(
                Component.translatable("tooltip.relics.relic.reroll.description"),
                Component.literal(" "),
                Component.translatable("tooltip.relics.relic.reroll.cost", requiredExperience,
                        (requiredExperience > experience ? negativeStatus : positiveStatus)));

        for (MutableComponent entry : entries) {
            int entryWidth = (MC.font.width(entry) + 4) / 2;

            if (entryWidth > renderWidth)
                renderWidth = Math.min(entryWidth, maxWidth);

            tooltip.addAll(MC.font.split(entry, maxWidth * 2));
        }

        int height = tooltip.size() * 4;

        int renderX = x + width + 3;
        int renderY = y - height / 2;

        ScreenUtils.drawTexturedTooltipBorder(poseStack, new ResourceLocation(Reference.MODID, "textures/gui/tooltip/border/paper.png"),
                renderWidth, height, renderX, renderY);

        int yOff = 0;

        poseStack.scale(0.5F, 0.5F, 0.5F);

        for (FormattedCharSequence entry : tooltip) {
            MC.font.draw(poseStack, entry, (renderX + 9) * 2, (renderY + 9 + yOff) * 2, 0x412708);

            yOff += 4;
        }

        poseStack.scale(1F, 1F, 1F);
    }
}