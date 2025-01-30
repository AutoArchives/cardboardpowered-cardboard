package org.bukkit.craftbukkit.inventory.view;

import com.google.common.base.Preconditions;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.screen.LecternScreenHandler;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.LecternInventory;
import org.bukkit.inventory.view.LecternView;

public class CraftLecternView extends CraftInventoryView<LecternScreenHandler, LecternInventory> implements LecternView {

    public CraftLecternView(final HumanEntity player, final LecternInventory viewing, final LecternScreenHandler container) {
        super(player, viewing, container);
    }

    @Override
    public int getPage() {
        return ((LecternScreenHandler) this.container).getPage();
    }

    @Override
    public void setPage(final int page) {
        Preconditions.checkArgument(page >= 0, "The minimum page is 0");
        this.container.setProperty(LecternBlockEntity.field_31348, page);
    }
}
