package io.papermc.paper.datacomponent.item;

import com.google.common.base.Preconditions;
import io.papermc.paper.text.Filtered;
import io.papermc.paper.util.MCUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.text.RawFilteredPair;
import org.bukkit.craftbukkit.util.Handleable;
import org.jetbrains.annotations.Unmodifiable;

public record PaperWritableBookContent(
    net.minecraft.component.type.WritableBookContentComponent impl
) implements WritableBookContent, Handleable<net.minecraft.component.type.WritableBookContentComponent> {

    @Override
    public net.minecraft.component.type.WritableBookContentComponent getHandle() {
        return this.impl;
    }

    @Override
    public @Unmodifiable List<Filtered<String>> pages() {
        return MCUtil.transformUnmodifiable(this.impl.pages(), input -> Filtered.of(input.raw(), input.filtered().orElse(null)));
    }

    static final class BuilderImpl implements WritableBookContent.Builder {

        private final List<RawFilteredPair<String>> pages = new ObjectArrayList<>();

        private static void validatePageLength(final String page) {
            Preconditions.checkArgument(
                page.length() <= net.minecraft.component.type.WritableBookContentComponent.MAX_PAGE_LENGTH,
                "Cannot have page length more than %s, had %s",
                net.minecraft.component.type.WritableBookContentComponent.MAX_PAGE_LENGTH,
                page.length()
            );
        }

        private static void validatePageCount(final int current, final int add) {
            final int newSize = current + add;
            Preconditions.checkArgument(
                newSize <= net.minecraft.component.type.WritableBookContentComponent.MAX_PAGE_COUNT,
                "Cannot have more than %s pages, had %s",
                net.minecraft.component.type.WritableBookContentComponent.MAX_PAGE_COUNT,
                newSize
            );
        }

        @Override
        public WritableBookContent.Builder addPage(final String page) {
            validatePageLength(page);
            validatePageCount(this.pages.size(), 1);
            this.pages.add(RawFilteredPair.of(page));
            return this;
        }

        @Override
        public WritableBookContent.Builder addPages(final List<String> pages) {
            validatePageCount(this.pages.size(), pages.size());
            for (final String page : pages) {
                validatePageLength(page);
                this.pages.add(RawFilteredPair.of(page));
            }
            return this;
        }

        @Override
        public WritableBookContent.Builder addFilteredPage(final Filtered<String> page) {
            validatePageLength(page.raw());
            if (page.filtered() != null) {
                validatePageLength(page.filtered());
            }
            validatePageCount(this.pages.size(), 1);
            this.pages.add(new RawFilteredPair<>(page.raw(), Optional.ofNullable(page.filtered())));
            return this;
        }

        @Override
        public WritableBookContent.Builder addFilteredPages(final List<Filtered<String>> pages) {
            validatePageCount(this.pages.size(), pages.size());
            for (final Filtered<String> page : pages) {
                validatePageLength(page.raw());
                if (page.filtered() != null) {
                    validatePageLength(page.filtered());
                }
                this.pages.add(new RawFilteredPair<>(page.raw(), Optional.ofNullable(page.filtered())));
            }
            return this;
        }

        @Override
        public WritableBookContent build() {
            if (this.pages.isEmpty()) {
                return new PaperWritableBookContent(net.minecraft.component.type.WritableBookContentComponent.DEFAULT);
            }

            return new PaperWritableBookContent(
                new net.minecraft.component.type.WritableBookContentComponent(new ObjectArrayList<>(this.pages))
            );
        }
    }
}
