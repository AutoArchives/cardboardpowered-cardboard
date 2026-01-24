package org.cardboardpowered.impl.block;

import net.kyori.adventure.text.Component;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CardboardCommandBlock extends CardboardBlockEntityState<CommandBlockEntity> implements CommandBlock {

    private String command;
    private String name;
    
    public CardboardCommandBlock(World world, CommandBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardCommandBlock(CardboardCommandBlock state, Location location) {
        super(state, location);
    }
    
    @Override
    public CardboardCommandBlock copy() {
        return new CardboardCommandBlock(this, null);
    }

    @Override
    public CardboardCommandBlock copy(Location location) {
        return new CardboardCommandBlock(this, location);
    }

    /*
    public CardboardCommandBlock(Block block) {
        super(block, CommandBlockBlockEntity.class);
    }

    public CardboardCommandBlock(final Material material, final CommandBlockBlockEntity cmdblock) {
        super(material, cmdblock);
    }
    */

    @Override
    public void load(CommandBlockEntity cmdblock) {
        super.load(cmdblock);
        command = cmdblock.getCommandBlock().getCommand();
        name = CraftChatMessage.fromComponent(cmdblock.getCommandBlock().getCustomName());
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public void setCommand(String command) {
        this.command = command != null ? command : "";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name != null ? name : "@";
    }

    @Override
    public void applyTo(CommandBlockEntity commandBlock) {
        super.applyTo(commandBlock);
        commandBlock.getCommandBlock().setCommand(command);
        commandBlock.getCommandBlock().setCustomName(CraftChatMessage.fromStringOrNull(name));
    }

    @Override
    public int getSuccessCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public @NotNull Component lastOutput() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void lastOutput(@Nullable Component arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setSuccessCount(int i) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public @NotNull Component name() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void name(@Nullable Component arg0) {
        // TODO Auto-generated method stub
        
    }

}