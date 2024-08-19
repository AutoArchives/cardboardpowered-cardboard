package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftMetaBookSigned;
import org.bukkit.craftbukkit.inventory.CraftMetaItem;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.inventory.meta.BookMeta;
// import org.bukkit.inventory.meta.WritableBookMeta;

@DelegateDeserialization(value=SerializableMeta.class)
public class CraftMetaBook
extends CraftMetaItem
implements BookMeta // ,
/*WritableBookMeta*/ {
    static final CraftMetaItem.ItemMetaKeyType<WritableBookContentComponent> BOOK_CONTENT = new CraftMetaItem.ItemMetaKeyType<WritableBookContentComponent>(DataComponentTypes.WRITABLE_BOOK_CONTENT);
    static final CraftMetaItem.ItemMetaKey BOOK_PAGES = new CraftMetaItem.ItemMetaKey("pages");
    static final int MAX_PAGES = Integer.MAX_VALUE;
    static final int MAX_PAGE_LENGTH = 1024;
    protected List<String> pages;
    private BookMeta.Spigot spigot = new SpigotMeta();

    CraftMetaBook(CraftMetaItem meta) {
        super(meta);
        if (meta instanceof CraftMetaBook) {
            CraftMetaBook bookMeta = (CraftMetaBook)meta;
            if (bookMeta.pages != null) {
                this.pages = new ArrayList<String>(bookMeta.pages.size());
                this.pages.addAll(bookMeta.pages);
            }
        } else if (meta instanceof CraftMetaBookSigned) {
            CraftMetaBookSigned bookMeta = (CraftMetaBookSigned)meta;
            if (bookMeta.pages != null) {
                this.pages = new ArrayList<String>(bookMeta.pages.size());
                this.pages.addAll(Lists.transform(bookMeta.pages, CraftChatMessage::fromComponent));
            }
        }
    }

    CraftMetaBook(ComponentChanges tag, Set<DataComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        CraftMetaBook.getOrEmpty(tag, BOOK_CONTENT).ifPresent(writable -> {
            List<RawFilteredPair<String>> pages = writable.pages();
            this.pages = new ArrayList<String>(pages.size());
            for (int i2 = 0; i2 < Math.min(pages.size(), Integer.MAX_VALUE); ++i2) {
                String page = pages.get(i2).raw();
                page = this.validatePage(page);
                this.pages.add(page);
            }
        });
    }

    CraftMetaBook(Map<String, Object> map) {
        super(map);
        Iterable pages = SerializableMeta.getObject(Iterable.class, map, CraftMetaBook.BOOK_PAGES.BUKKIT, true);
        if (pages != null) {
            this.pages = new ArrayList<String>();
            for (Object page : pages) {
                if (!(page instanceof String)) continue;
                this.internalAddPage(this.validatePage((String)page));
            }
        }
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator itemData) {
        super.applyToItem(itemData);
        if (this.pages != null) {
            ArrayList<RawFilteredPair<String>> list = new ArrayList<RawFilteredPair<String>>();
            for (String page : this.pages) {
                list.add(RawFilteredPair.of(FilteredMessage.permitted(page)));
            }
            itemData.put(BOOK_CONTENT, new WritableBookContentComponent(list));
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && this.isBookEmpty();
    }

    boolean isBookEmpty() {
        return this.pages == null;
    }

    @Override
    boolean applicableTo(Material type) {
        return type == Material.WRITABLE_BOOK;
    }

    public boolean hasAuthor() {
        return false;
    }

    public boolean hasTitle() {
        return false;
    }

    public boolean hasPages() {
        return this.pages != null && !this.pages.isEmpty();
    }

    public boolean hasGeneration() {
        return false;
    }

    public String getTitle() {
        return null;
    }

    public boolean setTitle(String title) {
        return false;
    }

    public String getAuthor() {
        return null;
    }

    public void setAuthor(String author) {
    }

    public BookMeta.Generation getGeneration() {
        return null;
    }

    public void setGeneration(BookMeta.Generation generation) {
    }

    public Component title() {
        return null;
    }

    public BookMeta title(Component title) {
        return this;
    }

    public Component author() {
        return null;
    }

    public BookMeta author(Component author) {
        return this;
    }

    public Component page(int page) {
        Preconditions.checkArgument((boolean)this.isValidPage(page), "Invalid page number");
        return LegacyComponentSerializer.legacySection().deserialize(this.pages.get(page - 1));
    }

    public void page(int page, Component data) {
        if (!this.isValidPage(page)) {
            throw new IllegalArgumentException("Invalid page number " + page + "/" + this.pages.size());
        }
        if (data == null) {
            data = Component.empty();
        }
        this.pages.set(page - 1, LegacyComponentSerializer.legacySection().serialize(data));
    }

    public List<Component> pages() {
        if (this.pages == null) {
            return ImmutableList.of();
        }
        return (List)this.pages.stream().map(arg_0 -> ((LegacyComponentSerializer)LegacyComponentSerializer.legacySection()).deserialize(arg_0)).collect(ImmutableList.toImmutableList());
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
            this.pages = new ArrayList<String>();
        }
        for (Component page : pages) {
            if (this.pages.size() >= Integer.MAX_VALUE) {
                return;
            }
            if (page == null) {
                page = Component.empty();
            }
            this.pages.add(LegacyComponentSerializer.legacySection().serialize(page));
        }
    }

    private CraftMetaBook(List<Component> pages) {
        super((CraftMetaItem)Bukkit.getItemFactory().getItemMeta(Material.WRITABLE_BOOK));
        this.pages = pages.subList(0, Math.min(Integer.MAX_VALUE, pages.size())).stream().map(arg_0 -> ((LegacyComponentSerializer)LegacyComponentSerializer.legacySection()).serialize(arg_0)).collect(Collectors.toList());
    }

    public BookMeta.BookMetaBuilder toBuilder() {
        return new CraftMetaBookBuilder();
    }

    public String getPage(int page) {
        Preconditions.checkArgument((boolean)this.isValidPage(page), (String)"Invalid page number (%s)", (int)page);
        return this.pages.get(page - 1);
    }

    public void setPage(int page, String text) {
        Preconditions.checkArgument((boolean)this.isValidPage(page), (String)"Invalid page number (%s/%s)", (int)page, (int)this.getPageCount());
        String newText = this.validatePage(text);
        this.pages.set(page - 1, newText);
    }

    public void setPages(String ... pages) {
        this.setPages(Arrays.asList(pages));
    }

    public void addPage(String ... pages) {
        for (String page : pages) {
            page = this.validatePage(page);
            this.internalAddPage(page);
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

    private void internalAddPage(String page) {
        if (this.pages == null) {
            this.pages = new ArrayList<String>();
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
        return (List)this.pages.stream().collect(ImmutableList.toImmutableList());
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

    @Override
    public CraftMetaBook clone() {
        CraftMetaBook meta = (CraftMetaBook)super.clone();
        if (this.pages != null) {
            meta.pages = new ArrayList<String>(this.pages);
        }
        meta.spigot = meta.new SpigotMeta();
        return meta;
    }

    @Override
    int applyHash() {
        int original;
        int hash = original = super.applyHash();
        if (this.pages != null) {
            hash = 61 * hash + 17 * this.pages.hashCode();
        }
        return original != hash ? CraftMetaBook.class.hashCode() ^ hash : hash;
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaBook) {
            CraftMetaBook that = (CraftMetaBook)meta;
            return Objects.equals(this.pages, that.pages);
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaBook || this.isBookEmpty());
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        if (this.pages != null) {
            builder.put(CraftMetaBook.BOOK_PAGES.BUKKIT, ImmutableList.copyOf(this.pages));
        }
        return builder;
    }

    public BookMeta.Spigot spigot() {
        return this.spigot;
    }

    private class SpigotMeta
    extends BookMeta.Spigot {
        private SpigotMeta() {
        }

        private String pageToJSON(String page) {
            Text component = CraftChatMessage.fromString(page, true, true)[0];
            return CraftChatMessage.toJSON(component);
        }

        private String componentsToPage(BaseComponent[] components) {
            return CraftChatMessage.fromJSONComponent(ComponentSerializer.toString((BaseComponent[])components));
        }

        public BaseComponent[] getPage(int page) {
            Preconditions.checkArgument((boolean)CraftMetaBook.this.isValidPage(page), "Invalid page number");
            return ComponentSerializer.parse((String)this.pageToJSON(CraftMetaBook.this.pages.get(page - 1)));
        }

        public void setPage(int page, BaseComponent ... text) {
            if (!CraftMetaBook.this.isValidPage(page)) {
                throw new IllegalArgumentException("Invalid page number " + page + "/" + CraftMetaBook.this.getPageCount());
            }
            BaseComponent[] newText = text == null ? new BaseComponent[]{} : text;
            CraftMetaBook.this.pages.set(page - 1, this.componentsToPage(newText));
        }

        public void setPages(BaseComponent[] ... pages) {
            this.setPages(Arrays.asList(pages));
        }

        public void addPage(BaseComponent[] ... pages) {
            for (BaseComponent[] page : pages) {
                if (page == null) {
                    page = new BaseComponent[]{};
                }
                CraftMetaBook.this.internalAddPage(this.componentsToPage(page));
            }
        }

        public List<BaseComponent[]> getPages() {
            if (CraftMetaBook.this.pages == null) {
                return ImmutableList.of();
            }
            ImmutableList copy = ImmutableList.copyOf(CraftMetaBook.this.pages);
            return new AbstractList<BaseComponent[]>(){

                @Override
                public BaseComponent[] get(int index) {
                    return ComponentSerializer.parse((String)SpigotMeta.this.pageToJSON((String)copy.get(index)));
                }

                @Override
                public int size() {
                    return copy.size();
                }
            };
        }

        public void setPages(List<BaseComponent[]> pages) {
            if (pages.isEmpty()) {
                CraftMetaBook.this.pages = null;
                return;
            }
            if (CraftMetaBook.this.pages != null) {
                CraftMetaBook.this.pages.clear();
            }
            for (BaseComponent[] page : pages) {
                this.addPage(new BaseComponent[][]{page});
            }
        }
    }

    static class CraftMetaBookBuilder
    implements BookMeta.BookMetaBuilder {
        protected final List<Component> pages = new ArrayList<Component>();

        CraftMetaBookBuilder() {
        }

        public BookMeta.BookMetaBuilder title(Component title) {
            return this;
        }

        public BookMeta.BookMetaBuilder author(Component author) {
            return this;
        }

        public BookMeta.BookMetaBuilder addPage(Component page) {
            this.pages.add(page);
            return this;
        }

        public BookMeta.BookMetaBuilder pages(Component ... pages) {
            Collections.addAll(this.pages, pages);
            return this;
        }

        public BookMeta.BookMetaBuilder pages(Collection<Component> pages) {
            this.pages.addAll(pages);
            return this;
        }

        public BookMeta build() {
            return new CraftMetaBook(this.pages);
        }
    }
}

