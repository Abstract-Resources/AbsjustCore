package dev.absjustcore.provider.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor (access = AccessLevel.PRIVATE)
public final class StoreMeta {

    @Getter private final String collection;
    @Getter private final String statement;

    private final Set<StoreValue> values;

    public Map<String, Object> values() {
        Map<String, Object> map = new HashMap<>();

        for (StoreValue storeValue : this.values.stream()
                .sorted(Comparator.comparingInt(StoreValue::getId))
                .collect(Collectors.toList())
        ) {
            map.put(storeValue.getKey(), storeValue.getValue());
        }

        return map;
    }

    public Set<StoreValue> listValues() {
        return this.values;
    }

    public @NonNull Map<String, Object> fetchFirstValues(int limit) {
        Map<String, Object> copy = new HashMap<>();

        for (StoreValue value : this.values) {
            if (copy.size() == limit) break;

            copy.put(value.getKey(), value.getValue());
        }

        return copy;
    }

    public String fetchString(String key) {
        StoreValue storeValue = this.values.stream()
                .filter(value -> value.getKey().equalsIgnoreCase(key))
                .findFirst().orElse(null);

        if (storeValue == null) {
            throw new RuntimeException("SQL Value for " + key + " not found...");
        }

        return (String) storeValue.getValue();
    }

    public void flush() {
        this.values.clear();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String collection;
        private String statement;

        private final Set<StoreValue> values = new HashSet<>();

        public Builder collection(String collection) {
            this.collection = collection;

            return this;
        }

        public Builder statement(String statement) {
            this.statement = statement;

            return this;
        }

        public Builder append(String key, Object value) {
            return this.append(-1, key, value);
        }

        public Builder append(int id, String key, Object value) {
            return this.append(StoreValue.builder().id(id).key(key).value(value).build());
        }

        public Builder append(StoreValue value) {
            this.values.add(value);

            return this;
        }

        public StoreMeta build() {
            return new StoreMeta(this.collection, this.statement, this.values);
        }
    }
}