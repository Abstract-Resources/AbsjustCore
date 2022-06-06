package dev.absjustcore.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
public final class MetaData {

    @Getter @Setter private String prefix;
    @Getter @Setter private String suffix;

    private Set<String> prefixes;
    private Set<String> suffixes;

    public void invalidate() {
        this.prefixes.clear();

        this.suffixes.clear();
    }

    public void invalidatePrefix(String prefix) {
        this.prefixes.clear();

        this.prefix = prefix;
    }

    public void invalidateSuffix(String suffix) {
        this.suffixes.clear();

        this.suffix = suffix;
    }

    public void addPrefix(String prefix) {
        this.prefixes.add(prefix);
    }

    public void removePrefix(String prefix) {
        this.prefixes.removeIf(targetPrefix -> targetPrefix.equalsIgnoreCase(prefix));
    }

    public void addSuffix(String suffix) {
        this.suffixes.add(suffix);
    }

    public void removeSuffix(String suffix) {
        this.prefixes.removeIf(targetSuffix -> targetSuffix.equalsIgnoreCase(suffix));
    }

    public Set<String> getPrefixes() {
        return Collections.unmodifiableSet(this.prefixes);
    }

    public Set<String> getSuffixes() {
        return Collections.unmodifiableSet(this.suffixes);
    }

    public boolean findPrefix(String prefix) {
        return this.prefixes.contains(prefix) || Objects.equals(this.prefix, prefix);
    }

    public boolean findSuffix(String suffix) {
        return this.suffixes.contains(suffix) || Objects.equals(this.suffix, suffix);
    }

    public void recalculate() {

    }

    public static MetaData empty() {
        return new MetaData(
                null,
                null,
                new HashSet<>(),
                new HashSet<>()
        );
    }
}