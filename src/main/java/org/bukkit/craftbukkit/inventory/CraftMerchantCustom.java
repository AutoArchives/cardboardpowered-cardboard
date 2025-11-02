package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.event.player.PlayerPurchaseEvent;
import javax.annotation.Nullable;
import net.kyori.adventure.text.Component;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.ExperienceOrb.SpawnReason;
import org.cardboardpowered.interfaces.IMixinTrader;

public class CraftMerchantCustom implements CraftMerchant {
   private CraftMerchantCustom.MinecraftMerchant merchant;

   @Deprecated
   public CraftMerchantCustom(String title) {
      this.merchant = new CraftMerchantCustom.MinecraftMerchant(title);
      this.getMerchant().craftMerchant = this;
   }

   public CraftMerchantCustom(Component title) {
      this.merchant = new CraftMerchantCustom.MinecraftMerchant(title);
      this.getMerchant().craftMerchant = this;
   }

   public CraftMerchantCustom() {
      this.merchant = new CraftMerchantCustom.MinecraftMerchant();
      this.getMerchant().craftMerchant = this;
   }

   public CraftMerchantCustom.MinecraftMerchant getMerchant() {
      return this.merchant;
   }

   public static class MinecraftMerchant implements Merchant, IMixinTrader {
      private final Text title;
      private final TradeOfferList trades = new TradeOfferList();
      private PlayerEntity tradingPlayer;
      protected CraftMerchant craftMerchant;

      @Deprecated
      public MinecraftMerchant(String title) {
         Preconditions.checkArgument(title != null, "Title cannot be null");
         this.title = CraftChatMessage.fromString(title)[0];
      }

      public MinecraftMerchant(Component title) {
         Preconditions.checkArgument(title != null, "Title cannot be null");
         this.title = PaperAdventure.asVanilla(title);
      }

      public MinecraftMerchant() {
         this.title = EntityType.VILLAGER.getName();
      }

      @Override
      public CraftMerchant getCraftMerchant() {
         return this.craftMerchant;
      }

      @Override
      public void setCustomer(PlayerEntity customer) {
         this.tradingPlayer = customer;
      }

      @Override
      public PlayerEntity getCustomer() {
         return this.tradingPlayer;
      }

      @Override
      public TradeOfferList getOffers() {
         return this.trades;
      }

      // @Override
      public void processTrade(TradeOffer offer, @Nullable PlayerPurchaseEvent event) {
         if (this.getCustomer() instanceof ServerPlayerEntity) {
            if (event == null || event.willIncreaseTradeUses()) {
               offer.use();
            }

            if (event == null || event.isRewardingExp()) {
               this.tradingPlayer
                  .getEntityWorld()
                  .spawnEntity(
                     new ExperienceOrbEntity(
                        this.tradingPlayer.getEntityWorld(),
                        this.tradingPlayer.getX(),
                        this.tradingPlayer.getY(),
                        this.tradingPlayer.getZ(),
                        offer.getMerchantExperience()
                        // SpawnReason.VILLAGER_TRADE,
                        // this.tradingPlayer,
                        // null
                     )
                  );
            }
         }

         this.trade(offer);
      }

      @Override
      public void trade(TradeOffer offer) {
      }

      @Override
      public void onSellingItem(ItemStack stack) {
      }

      public Text getScoreboardDisplayName() {
         return this.title;
      }

      @Override
      public int getExperience() {
         return 0;
      }

      @Override
      public void setExperienceFromServer(int experience) {
      }

      @Override
      public boolean isLeveledMerchant() {
         return false;
      }

      @Override
      public SoundEvent getYesSound() {
         return SoundEvents.ENTITY_VILLAGER_YES;
      }

      @Override
      public void setOffersFromServer(TradeOfferList offers) {
      }

      @Override
      public boolean isClient() {
         return false;
      }

      @Override
      public boolean canInteract(PlayerEntity player) {
         return this.tradingPlayer == player;
      }
   }
}
