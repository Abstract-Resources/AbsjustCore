package dev.absjustcore.provider.utils;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor (access = AccessLevel.PRIVATE) @Getter
public final class StoreMeta {

    private final String collection;
    private final String statement;

    private final Map<String, Object> values;

    public @NonNull Map<String, Object> fetchFirstValues(int limit) {
        Map<String, Object> copy = new HashMap<>();

        for (Map.Entry<String, Object> entry : this.values.entrySet()) {
            if (copy.size() == limit) break;

            copy.put(entry.getKey(), entry.getValue());
        }

        return copy;
    }

    public String fetchString(String key) {
        return this.values.get(key).toString();
    }

    public void invalidate() {
        this.values.clear();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String collection;
        private String statement;

        private final Map<String, Object> values = new HashMap<>();

        public Builder collection(String collection) {
            this.collection = collection;

            return this;
        }

        public Builder statement(String statement) {
            this.statement = statement;

            return this;
        }

        public Builder append(String key, Object value) {
            this.values.put(key, value);

            return this;
        }

        public StoreMeta build() {
            return new StoreMeta(this.collection, this.statement, this.values);
        }
    }
}