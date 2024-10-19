package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.util.Identifier;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.meta.KnowledgeBookMeta;

@DelegateDeserialization(value=SerializableMeta.class)
public class CraftMetaKnowledgeBook
extends CraftMetaItem
implements KnowledgeBookMeta {
    static final CraftMetaItem.ItemMetaKeyType<List<Identifier>> BOOK_RECIPES = new CraftMetaItem.ItemMetaKeyType<List<Identifier>>(DataComponentTypes.RECIPES, "Recipes");
    static final int MAX_RECIPES = Short.MAX_VALUE;
    protected List<NamespacedKey> recipes = new ArrayList<NamespacedKey>();

    CraftMetaKnowledgeBook(CraftMetaItem meta) {
        super(meta);
        if (meta instanceof CraftMetaKnowledgeBook) {
            CraftMetaKnowledgeBook bookMeta = (CraftMetaKnowledgeBook)meta;
            this.recipes.addAll(bookMeta.recipes);
        }
    }

    CraftMetaKnowledgeBook(ComponentChanges tag, Set<ComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        CraftMetaKnowledgeBook.getOrEmpty(tag, BOOK_RECIPES).ifPresent(pages -> {
            for (int i2 = 0; i2 < pages.size(); ++i2) {
                Identifier recipe = (Identifier)pages.get(i2);
                this.addRecipe(CraftNamespacedKey.fromMinecraft(recipe));
            }
        });
    }

    CraftMetaKnowledgeBook(Map<String, Object> map) {
        super(map);
        Iterable pages = SerializableMeta.getObject(Iterable.class, map, CraftMetaKnowledgeBook.BOOK_RECIPES.BUKKIT, true);
        if (pages != null) {
            for (Object page : pages) {
                if (!(page instanceof String)) continue;
                this.addRecipe(CraftNamespacedKey.fromString((String)page));
            }
        }
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator itemData) {
        super.applyToItem(itemData);
        if (this.hasRecipes()) {
            ArrayList<Identifier> list = new ArrayList<Identifier>();
            for (NamespacedKey recipe : this.recipes) {
                list.add(CraftNamespacedKey.toMinecraft(recipe));
            }
            itemData.put(BOOK_RECIPES, list);
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && this.isBookEmpty();
    }

    boolean isBookEmpty() {
        return !this.hasRecipes();
    }

    @Override
    boolean applicableTo(Material type) {
        return type == Material.KNOWLEDGE_BOOK;
    }

    public boolean hasRecipes() {
        return !this.recipes.isEmpty();
    }

    public void addRecipe(NamespacedKey ... recipes) {
        for (NamespacedKey recipe : recipes) {
            if (recipe == null) continue;
            if (this.recipes.size() >= Short.MAX_VALUE) {
                return;
            }
            this.recipes.add(recipe);
        }
    }

    public List<NamespacedKey> getRecipes() {
        return Collections.unmodifiableList(this.recipes);
    }

    public void setRecipes(List<NamespacedKey> recipes) {
        this.recipes.clear();
        for (NamespacedKey recipe : recipes) {
            this.addRecipe(recipe);
        }
    }

    @Override
    public CraftMetaKnowledgeBook clone() {
        CraftMetaKnowledgeBook meta = (CraftMetaKnowledgeBook)super.clone();
        meta.recipes = new ArrayList<NamespacedKey>(this.recipes);
        return meta;
    }

    @Override
    int applyHash() {
        int original;
        int hash = original = super.applyHash();
        if (this.hasRecipes()) {
            hash = 61 * hash + 17 * this.recipes.hashCode();
        }
        return original != hash ? CraftMetaKnowledgeBook.class.hashCode() ^ hash : hash;
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaKnowledgeBook) {
            CraftMetaKnowledgeBook that = (CraftMetaKnowledgeBook)meta;
            return this.hasRecipes() ? that.hasRecipes() && this.recipes.equals(that.recipes) : !that.hasRecipes();
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaKnowledgeBook || this.isBookEmpty());
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        if (this.hasRecipes()) {
            ArrayList<String> recipesString = new ArrayList<String>();
            for (NamespacedKey recipe : this.recipes) {
                recipesString.add(recipe.toString());
            }
            builder.put(CraftMetaKnowledgeBook.BOOK_RECIPES.BUKKIT, recipesString);
        }
        return builder;
    }
}

