package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.server.network.FilteredText;
import net.minecraft.world.item.component.WrittenBookContent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftMetaBook;
import org.bukkit.craftbukkit.inventory.CraftMetaItem;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.inventory.meta.BookMeta;

@DelegateDeserialization(value=SerializableMeta.class)
public class CraftMetaBookSigned
extends CraftMetaItem
implements BookMeta {
    static final CraftMetaItem.ItemMetaKeyType<WrittenBookContent> BOOK_CONTENT = new CraftMetaItem.ItemMetaKeyType<WrittenBookContent>(DataComponents.WRITTEN_BOOK_CONTENT);
    static final CraftMetaItem.ItemMetaKey BOOK_TITLE = new CraftMetaItem.ItemMetaKey("title");
    static final CraftMetaItem.ItemMetaKey BOOK_AUTHOR = new CraftMetaItem.ItemMetaKey("author");
    static final CraftMetaItem.ItemMetaKey BOOK_PAGES = new CraftMetaItem.ItemMetaKey("pages");
    static final CraftMetaItem.ItemMetaKey RESOLVED = new CraftMetaItem.ItemMetaKey("resolved");
    static final CraftMetaItem.ItemMetaKey GENERATION = new CraftMetaItem.ItemMetaKey("generation");
    static final int MAX_PAGES = Integer.MAX_VALUE;
    static final int MAX_PAGE_LENGTH = 1024;
    static final int MAX_TITLE_LENGTH = 32;
    protected String title;
    protected String author;
    protected List<net.minecraft.network.chat.Component> pages;
    protected boolean resolved;
    protected int generation;
    private BookMeta.Spigot spigot = new SpigotMeta();
    public static final LegacyComponentSerializer LEGACY_DOWNSAMPLING_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder().character('\u00a7').build();

    CraftMetaBookSigned(CraftMetaItem meta) {
        super(meta);
        
        
        if (meta instanceof CraftMetaBookSigned) {
            CraftMetaBookSigned bookMeta = (CraftMetaBookSigned)meta;
            this.title = bookMeta.title;
            this.author = bookMeta.author;
            this.resolved = bookMeta.resolved;
            this.generation = bookMeta.generation;
            if (bookMeta.pages != null) {
                this.pages = new ArrayList<net.minecraft.network.chat.Component>(bookMeta.pages.size());
                this.pages.addAll(bookMeta.pages);
            }
        } else if (meta instanceof CraftMetaBook) {
            CraftMetaBook bookMeta = (CraftMetaBook)meta;
            if (bookMeta.pages != null) {
                this.pages = new ArrayList<net.minecraft.network.chat.Component>(bookMeta.pages.size());
                for (String page : bookMeta.pages) {
                    net.minecraft.network.chat.Component component = CraftChatMessage.fromString(page, true, true)[0];
                    this.pages.add(component);
                }
            }
        }
    }

    CraftMetaBookSigned(DataComponentPatch tag, Set<DataComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        CraftMetaBookSigned.getOrEmpty(tag, BOOK_CONTENT).ifPresent(written -> {
            this.title = written.title().raw();
            this.author = written.author();
            this.resolved = written.resolved();
            this.generation = written.generation();
            List<Filterable<net.minecraft.network.chat.Component>> pages = written.pages();
            this.pages = new ArrayList<net.minecraft.network.chat.Component>(pages.size());
            for (int i2 = 0; i2 < Math.min(pages.size(), Integer.MAX_VALUE); ++i2) {
                net.minecraft.network.chat.Component page = pages.get(i2).raw();
                this.pages.add(page);
            }
        });
    }

    CraftMetaBookSigned(Map<String, Object> map) {
        super(map);
        this.setAuthor(org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta.getString(map, CraftMetaBookSigned.BOOK_AUTHOR.BUKKIT, true));
        this.setTitle(org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta.getString(map, CraftMetaBookSigned.BOOK_TITLE.BUKKIT, true));
        Iterable pages = org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta.getObject(Iterable.class, map, CraftMetaBookSigned.BOOK_PAGES.BUKKIT, true);
        if (pages != null) {
            this.pages = new ArrayList<net.minecraft.network.chat.Component>();
            for (Object page : pages) {
                if (!(page instanceof String)) continue;
                this.internalAddPage(CraftChatMessage.fromJSON(CraftChatMessage.fromJSONOrStringToJSON((String)page, false, true, 1024, false)));
            }
        }
        this.resolved = org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta.getBoolean(map, CraftMetaBookSigned.RESOLVED.BUKKIT);
        this.generation = org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta.getObjectOptionally(Integer.class, map, CraftMetaBookSigned.GENERATION.BUKKIT, true).orElse(0);
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator itemData) {
        super.applyToItem(itemData);
        if (this.pages != null) {
            ArrayList<Filterable<net.minecraft.network.chat.Component>> list = new ArrayList<Filterable<net.minecraft.network.chat.Component>>();
            for (net.minecraft.network.chat.Component page : this.pages) {
                list.add(Filterable.passThrough(page));
            }
            itemData.put(BOOK_CONTENT, new WrittenBookContent(Filterable.from(FilteredText.passThrough(this.title == null ? "" : this.title)), this.author == null ? "" : this.author, this.generation, list, this.resolved));
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && this.isBookEmpty();
    }

    boolean isBookEmpty() {
        return this.pages == null && !this.hasAuthor() && !this.hasTitle() && !this.hasGeneration() && !this.resolved;
    }

    @Override
    boolean applicableTo(Material type) {
        return type == Material.WRITTEN_BOOK;
    }

    public boolean hasAuthor() {
        return this.author != null;
    }

    public boolean hasTitle() {
        return this.title != null;
    }

    public boolean hasPages() {
        return this.pages != null && !this.pages.isEmpty();
    }

    public boolean hasGeneration() {
        return this.generation != 0;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean setTitle(String title) {
        if (title == null) {
            this.title = null;
            return true;
        }
        if (title.length() > 32) {
            return false;
        }
        this.title = title;
        return true;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public BookMeta.Generation getGeneration() {
        return BookMeta.Generation.values()[this.generation];
    }

    public void setGeneration(BookMeta.Generation generation) {
        this.generation = generation == null ? 0 : generation.ordinal();
    }

    public String getPage(int page) {
        Preconditions.checkArgument((boolean)this.isValidPage(page), (String)"Invalid page number (%s)", (int)page);
        return CraftChatMessage.fromComponent(this.pages.get(page - 1));
    }

    public void setPage(int page, String text) {
        Preconditions.checkArgument((boolean)this.isValidPage(page), (String)"Invalid page number (%s/%s)", (int)page, (int)this.getPageCount());
        String newText = this.validatePage(text);
        this.pages.set(page - 1, CraftChatMessage.fromStringOrNull(newText, true));
    }

    public void setPages(String ... pages) {
        this.setPages(Arrays.asList(pages));
    }

    public void addPage(String ... pages) {
        for (String page : pages) {
            page = this.validatePage(page);
            this.internalAddPage(CraftChatMessage.fromStringOrNull(page, true));
        }
    }

    String validatePage(String page) {
        if (page == null) {
            page = "";
        } else if (page.length() > 1024) {
            page = page.substring(0, 1024);
        }
        return page;
    }

    private void internalAddPage(net.minecraft.network.chat.Component page) {
        if (this.pages == null) {
            this.pages = new ArrayList<net.minecraft.network.chat.Component>();
        } else if (this.pages.size() >= Integer.MAX_VALUE) {
            return;
        }
        this.pages.add(page);
    }

    public int getPageCount() {
        return this.pages == null ? 0 : this.pages.size();
    }

    public List<String> getPages() {
        if (this.pages == null) {
            return ImmutableList.of();
        }
        return (List)this.pages.stream().map(CraftChatMessage::fromComponent).collect(ImmutableList.toImmutableList());
    }

    public void setPages(List<String> pages) {
        if (pages.isEmpty()) {
            this.pages = null;
            return;
        }
        if (this.pages != null) {
            this.pages.clear();
        }
        for (String page : pages) {
            this.addPage(page);
        }
    }

    private boolean isValidPage(int page) {
        return page > 0 && page <= this.getPageCount();
    }

    public boolean isResolved() {
        return this.resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    @Override
    public CraftMetaBookSigned clone() {
        CraftMetaBookSigned meta = (CraftMetaBookSigned)super.clone();
        if (this.pages != null) {
            meta.pages = new ArrayList<net.minecraft.network.chat.Component>(this.pages);
        }
        meta.spigot = meta.new SpigotMeta();
        return meta;
    }

    @Override
    int applyHash() {
        int original;
        int hash = original = super.applyHash();
        if (this.hasTitle()) {
            hash = 61 * hash + this.title.hashCode();
        }
        if (this.hasAuthor()) {
            hash = 61 * hash + 13 * this.author.hashCode();
        }
        if (this.pages != null) {
            hash = 61 * hash + 17 * this.pages.hashCode();
        }
        if (this.resolved) {
            hash = 61 * hash + 17 * Boolean.hashCode(this.resolved);
        }
        if (this.hasGeneration()) {
            hash = 61 * hash + 19 * Integer.hashCode(this.generation);
        }
        return original != hash ? CraftMetaBook.class.hashCode() ^ hash : hash;
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaBookSigned) {
            CraftMetaBookSigned that = (CraftMetaBookSigned)meta;
            return (this.hasTitle() ? that.hasTitle() && this.title.equals(that.title) : !that.hasTitle()) && (this.hasAuthor() ? that.hasAuthor() && this.author.equals(that.author) : !that.hasAuthor()) && Objects.equals(this.pages, that.pages) && Objects.equals(this.resolved, that.resolved) && Objects.equals(this.generation, that.generation);
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaBookSigned || this.isBookEmpty());
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        if (this.hasTitle()) {
            builder.put(CraftMetaBookSigned.BOOK_TITLE.BUKKIT, this.title);
        }
        if (this.hasAuthor()) {
            builder.put(CraftMetaBookSigned.BOOK_AUTHOR.BUKKIT, this.author);
        }
        if (this.pages != null) {
            builder.put(CraftMetaBookSigned.BOOK_PAGES.BUKKIT, ImmutableList.copyOf(this.pages));
        }
        if (this.resolved) {
            builder.put(CraftMetaBookSigned.RESOLVED.BUKKIT, this.resolved);
        }
        if (this.generation != 0) {
            builder.put(CraftMetaBookSigned.GENERATION.BUKKIT, this.generation);
        }
        return builder;
    }

    public BookMeta.Spigot spigot() {
        return this.spigot;
    }

    private CraftMetaBookSigned(Component title, Component author, List<Component> pages) {
        super((CraftMetaItem)Bukkit.getItemFactory().getItemMeta(Material.WRITABLE_BOOK));
        this.title = title == null ? null : LEGACY_DOWNSAMPLING_COMPONENT_SERIALIZER.serialize(title);
        this.author = author == null ? null : LEGACY_DOWNSAMPLING_COMPONENT_SERIALIZER.serialize(author);
        this.pages = PaperAdventure.asVanilla(pages.subList(0, Math.min(Integer.MAX_VALUE, pages.size())));
    }

    public BookMeta.BookMetaBuilder toBuilder() {
        return new CraftMetaBookSignedBuilder();
    }

    public Component title() {
        return this.title == null ? null : LEGACY_DOWNSAMPLING_COMPONENT_SERIALIZER.deserialize(this.title);
    }

    public BookMeta title(Component title) {
        this.setTitle(title == null ? null : LEGACY_DOWNSAMPLING_COMPONENT_SERIALIZER.serialize(title));
        return this;
    }

    public Component author() {
        return this.author == null ? null : LEGACY_DOWNSAMPLING_COMPONENT_SERIALIZER.deserialize(this.author);
    }

    public BookMeta author(Component author) {
        this.setAuthor(author == null ? null : LEGACY_DOWNSAMPLING_COMPONENT_SERIALIZER.serialize(author));
        return this;
    }

    public Component page(int page) {
        Preconditions.checkArgument((boolean)this.isValidPage(page), "Invalid page number");
        return PaperAdventure.asAdventure(this.pages.get(page - 1));
    }

    public void page(int page, Component data) {
        if (!this.isValidPage(page)) {
            throw new IllegalArgumentException("Invalid page number " + page + "/" + this.pages.size());
        }
        this.pages.set(page - 1, PaperAdventure.asVanillaNullToEmpty(data));
    }

    public List<Component> pages() {
        if (this.pages == null) {
            return ImmutableList.of();
        }
        return (List)this.pages.stream().map(PaperAdventure::asAdventure).collect(ImmutableList.toImmutableList());
    }

    public BookMeta pages(List<Component> pages) {
        if (this.pages != null) {
            this.pages.clear();
        }
        for (Component page : pages) {
            this.addPages(page);
        }
        return this;
    }

    public BookMeta pages(Component ... pages) {
        if (this.pages != null) {
            this.pages.clear();
        }
        this.addPages(pages);
        return this;
    }

    public void addPages(Component ... pages) {
        if (this.pages == null) {
            this.pages = new ArrayList<net.minecraft.network.chat.Component>();
        }
        for (Component page : pages) {
            if (this.pages.size() >= Integer.MAX_VALUE) {
                return;
            }
            this.pages.add(PaperAdventure.asVanillaNullToEmpty(page));
        }
    }

    private class SpigotMeta
    extends BookMeta.Spigot {
        private SpigotMeta() {
        }

        private String pageToJSON(net.minecraft.network.chat.Component page) {
            return CraftChatMessage.toJSON(page);
        }

        private net.minecraft.network.chat.Component componentsToPage(BaseComponent[] components) {
            return CraftChatMessage.fromJSON(ComponentSerializer.toString((BaseComponent[])components));
        }

        public BaseComponent[] getPage(int page) {
            Preconditions.checkArgument((boolean)CraftMetaBookSigned.this.isValidPage(page), "Invalid page number");
            return ComponentSerializer.parse((String)this.pageToJSON(CraftMetaBookSigned.this.pages.get(page - 1)));
        }

        public void setPage(int page, BaseComponent ... text) {
            if (!CraftMetaBookSigned.this.isValidPage(page)) {
                throw new IllegalArgumentException("Invalid page number " + page + "/" + CraftMetaBookSigned.this.getPageCount());
            }
            BaseComponent[] newText = text == null ? new BaseComponent[]{} : text;
            CraftMetaBookSigned.this.pages.set(page - 1, this.componentsToPage(newText));
        }

        public void setPages(BaseComponent[] ... pages) {
            this.setPages(Arrays.asList(pages));
        }

        public void addPage(BaseComponent[] ... pages) {
            for (BaseComponent[] page : pages) {
                if (page == null) {
                    page = new BaseComponent[]{};
                }
                CraftMetaBookSigned.this.internalAddPage(this.componentsToPage(page));
            }
        }

        public List<BaseComponent[]> getPages() {
            if (CraftMetaBookSigned.this.pages == null) {
                return ImmutableList.of();
            }
            ImmutableList copy = ImmutableList.copyOf(CraftMetaBookSigned.this.pages);
            return new AbstractList<BaseComponent[]>(){
                @Override
                public BaseComponent[] get(int index) {
                    return ComponentSerializer.parse((String)SpigotMeta.this.pageToJSON((net.minecraft.network.chat.Component)copy.get(index)));
                }

                @Override
                public int size() {
                    return copy.size();
                }
            };
        }

        public void setPages(List<BaseComponent[]> pages) {
            if (pages.isEmpty()) {
                CraftMetaBookSigned.this.pages = null;
                return;
            }
            if (CraftMetaBookSigned.this.pages != null) {
                CraftMetaBookSigned.this.pages.clear();
            }
            for (BaseComponent[] page : pages) {
                this.addPage(new BaseComponent[][]{page});
            }
        }
    }

    static final class CraftMetaBookSignedBuilder
    extends CraftMetaBook.CraftMetaBookBuilder {
        private Component title;
        private Component author;

        CraftMetaBookSignedBuilder() {
        }

        @Override
        public BookMeta.BookMetaBuilder title(Component title) {
            this.title = title;
            return this;
        }

        @Override
        public BookMeta.BookMetaBuilder author(Component author) {
            this.author = author;
            return this;
        }

        @Override
        public BookMeta build() {
            return new CraftMetaBookSigned(this.title, this.author, this.pages);
        }
    }
}

