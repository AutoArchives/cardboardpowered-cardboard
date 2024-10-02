package org.bukkit.craftbukkit.legacy.fieldrename;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.legacy.fieldrename.FieldRenameData;
import org.bukkit.craftbukkit.util.ApiVersion;

@com.github.bsideup.jabel.Desugar
public record FieldRenameData(RenameData<String> renameData, RenameData<NamespacedKey> keyRenameData) {
    
	public String getReplacement(ApiVersion apiVersion, String from) {
        if (from == null) {
            return null;
        }
        from = from.toUpperCase(Locale.ROOT);
        return this.renameData.getReplacement(apiVersion, from);
    }

    public NamespacedKey getReplacement(NamespacedKey from, ApiVersion apiVersion) {
        if (from == null) {
            return null;
        }
        return this.keyRenameData.getReplacement(apiVersion, from);
    }

    public static class Builder {
        private final Map<String, String> data = new HashMap<String, String>();
        private final NavigableMap<ApiVersion, Map<String, String>> versionData = new TreeMap<ApiVersion, Map<String, String>>();
        private final Map<NamespacedKey, NamespacedKey> keyData = new HashMap<NamespacedKey, NamespacedKey>();
        private final NavigableMap<ApiVersion, Map<NamespacedKey, NamespacedKey>> versionKeyData = new TreeMap<ApiVersion, Map<NamespacedKey, NamespacedKey>>();
        private ApiVersion currentVersion;
        private boolean keyRename = false;

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder forVersionsBefore(ApiVersion apiVersion) {
            this.currentVersion = apiVersion;
            this.keyRename = false;
            return this;
        }

        public Builder forAllVersions() {
            this.currentVersion = null;
            this.keyRename = false;
            return this;
        }

        public Builder withKeyRename() {
            this.keyRename = true;
            return this;
        }

        public Builder change(String from, String to) {
            if (this.currentVersion != null) {
                this.versionData.computeIfAbsent(this.currentVersion, d2 -> new HashMap()).put(from.replace('.', '_'), to);
            } else {
                this.data.put(from.replace('.', '_'), to);
            }
            if (this.keyRename) {
                NamespacedKey fromKey = NamespacedKey.minecraft((String)from.toLowerCase(Locale.ROOT));
                NamespacedKey toKey = NamespacedKey.minecraft((String)to.toLowerCase(Locale.ROOT));
                if (this.currentVersion != null) {
                    this.versionKeyData.computeIfAbsent(this.currentVersion, d2 -> new HashMap()).put(fromKey, toKey);
                } else {
                    this.keyData.put(fromKey, toKey);
                }
            }
            return this;
        }

        public FieldRenameData build() {
            return new FieldRenameData(new FieldRenameData.RenameData<String>(this.versionData, this.data), new FieldRenameData.RenameData<NamespacedKey>(this.versionKeyData, this.keyData));
        }
    }

    @com.github.bsideup.jabel.Desugar
    private record RenameData<T>(NavigableMap<ApiVersion, Map<T, T>> versionData, Map<T, T> data) {
        public T getReplacement(ApiVersion apiVersion, T from) {
            from = this.data.getOrDefault(from, from);

            for (Map.Entry<ApiVersion, Map<T, T>> entry : this.versionData.entrySet()) {
                if (apiVersion.isNewerThanOrSameAs(entry.getKey())) {
                    continue;
                }

                from = entry.getValue().getOrDefault(from, from);
            }

            return from;
        }
    }
    
}

