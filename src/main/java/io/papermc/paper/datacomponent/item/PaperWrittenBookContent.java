package io.papermc.paper.datacomponent.item;

import com.google.common.base.Preconditions;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.text.Filtered;
import io.papermc.paper.util.MCUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.util.JsonHelper;
import org.bukkit.craftbukkit.util.Handleable;
import org.jetbrains.annotations.Unmodifiable;

import static io.papermc.paper.adventure.PaperAdventure.asAdventure;
import static io.papermc.paper.adventure.PaperAdventure.asVanilla;

public record PaperWrittenBookContent(
    net.minecraft.component.type.WrittenBookContentComponent impl
) implements WrittenBookContent, Handleable<net.minecraft.component.type.WrittenBookContentComponent> {

    @Override
    public net.minecraft.component.type.WrittenBookContentComponent getHandle() {
        return this.impl;
    }

    @Override
    public Filtered<String> title() {
        return Filtered.of(this.impl.title().raw(), this.impl.title().filtered().orElse(null));
    }

    @Override
    public String author() {
        return this.impl.author();
    }

    @Override
    public int generation() {
        return this.impl.generation();
    }

    @Override
    public @Unmodifiable List<Filtered<Component>> pages() {
        return MCUtil.transformUnmodifiable(
            this.impl.pages(),
            page -> Filtered.of(asAdventure(page.raw()), page.filtered().map(PaperAdventure::asAdventure).orElse(null))
        );
    }

    @Override
    public boolean resolved() {
        return this.impl.resolved();
    }

    static final class BuilderImpl implements WrittenBookContent.Builder {

        private final List<RawFilteredPair<net.minecraft.text.Text>> pages = new ObjectArrayList<>();
        private RawFilteredPair<String> title;
        private String author;
        private int generation = 0;
        private boolean resolved = false;

        BuilderImpl(final Filtered<String> title, final String author) {
            validateTitle(title.raw());
            if (title.filtered() != null) {
                validateTitle(title.filtered());
            }
            this.title = new RawFilteredPair<>(title.raw(), Optional.ofNullable(title.filtered()));
            this.author = author;
        }

        private static void validateTitle(final String title) {
            Preconditions.checkArgument(
                title.length() <= net.minecraft.component.type.WrittenBookContentComponent.MAX_TITLE_LENGTH,
                "Title cannot be longer than %s, was %s",
                net.minecraft.component.type.WrittenBookContentComponent.MAX_TITLE_LENGTH,
                title.length()
            );
        }

        private static void validatePageLength(final Component page) {
            final String flagPage = JsonHelper.toSortedString(GsonComponentSerializer.gson().serializeToTree(page));
            Preconditions.checkArgument(
                flagPage.length() <= net.minecraft.component.type.WrittenBookContentComponent.MAX_SERIALIZED_PAGE_LENGTH,
                "Cannot have page length more than %s, had %s",
                net.minecraft.component.type.WrittenBookContentComponent.MAX_SERIALIZED_PAGE_LENGTH,
                flagPage.length()
            );
        }

        @Override
        public WrittenBookContent.Builder title(final String title) {
            validateTitle(title);
            this.title = RawFilteredPair.of(title);
            return this;
        }

        @Override
        public WrittenBookContent.Builder filteredTitle(final Filtered<String> title) {
            validateTitle(title.raw());
            if (title.filtered() != null) {
                validateTitle(title.filtered());
            }
            this.title = new RawFilteredPair<>(title.raw(), Optional.ofNullable(title.filtered()));
            return this;
        }

        @Override
        public WrittenBookContent.Builder author(final String author) {
            this.author = author;
            return this;
        }

        @Override
        public WrittenBookContent.Builder generation(final int generation) {
            Preconditions.checkArgument(
                generation >= 0 && generation <= net.minecraft.component.type.WrittenBookContentComponent.MAX_GENERATION,
                "generation must be between %s and %s, was %s",
                0, net.minecraft.component.type.WrittenBookContentComponent.MAX_GENERATION,
                generation
            );
            this.generation = generation;
            return this;
        }

        @Override
        public WrittenBookContent.Builder resolved(final boolean resolved) {
            this.resolved = resolved;
            return this;
        }

        @Override
        public WrittenBookContent.Builder addPage(final ComponentLike page) {
            final Component component = page.asComponent();
            validatePageLength(component);
            this.pages.add(RawFilteredPair.of(asVanilla(component)));
            return this;
        }

        @Override
        public WrittenBookContent.Builder addPages(final List<? extends ComponentLike> pages) {
            for (final ComponentLike page : pages) {
                final Component component = page.asComponent();
                validatePageLength(component);
                this.pages.add(RawFilteredPair.of(asVanilla(component)));
            }
            return this;
        }

        @Override
        public WrittenBookContent.Builder addFilteredPage(final Filtered<? extends ComponentLike> page) {
            final Component raw = page.raw().asComponent();
            validatePageLength(raw);
            Component filtered = null;
            if (page.filtered() != null) {
                filtered = page.filtered().asComponent();
                validatePageLength(filtered);
            }
            this.pages.add(new RawFilteredPair<>(asVanilla(raw), Optional.ofNullable(filtered).map(PaperAdventure::asVanilla)));
            return this;
        }

        @Override
        public WrittenBookContent.Builder addFilteredPages(final List<Filtered<? extends ComponentLike>> pages) {
            pages.forEach(this::addFilteredPage);
            return this;
        }

        @Override
        public WrittenBookContent build() {
            return new PaperWrittenBookContent(new net.minecraft.component.type.WrittenBookContentComponent(
                this.title,
                this.author,
                this.generation,
                new ObjectArrayList<>(this.pages),
                this.resolved
            ));
        }
    }
}
