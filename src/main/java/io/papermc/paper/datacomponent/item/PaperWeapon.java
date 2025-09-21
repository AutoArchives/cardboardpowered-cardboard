package io.papermc.paper.datacomponent.item;

import com.google.common.base.Preconditions;
import net.minecraft.component.type.WeaponComponent;
import org.bukkit.craftbukkit.util.Handleable;

public record PaperWeapon(WeaponComponent impl) implements Weapon, Handleable<WeaponComponent> {

    @Override
    public WeaponComponent getHandle() {
        return this.impl;
    }

    public int itemDamagePerAttack() {
        return this.impl.itemDamagePerAttack();
    }

    public float disableBlockingForSeconds() {
        return this.impl.disableBlockingForSeconds();
    }

    static final class BuilderImpl implements Weapon.Builder {

        private int itemDamagePerAttack = 1;
        private float disableBlockingForSeconds;

        BuilderImpl() {
        }

        public Weapon.Builder itemDamagePerAttack(int dam) {
            Preconditions.checkArgument(dam >= 0, "damage must >= 0, was " + dam);
            this.itemDamagePerAttack = dam;
            return this;
        }

        public Weapon.Builder disableBlockingForSeconds(float sec) {
            Preconditions.checkArgument(sec >= 0.0f, "seconds must >= 0, was " + sec);
            this.disableBlockingForSeconds = sec;
            return this;
        }

        public Weapon build() {
            return new PaperWeapon(new WeaponComponent(this.itemDamagePerAttack, this.disableBlockingForSeconds));
        }

    }

}