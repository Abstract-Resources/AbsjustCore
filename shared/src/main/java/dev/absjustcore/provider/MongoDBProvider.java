package dev.absjustcore.provider;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.absjustcore.AbsjustPlugin;
import dev.absjustcore.TaskUtils;
import dev.absjustcore.provider.utils.LocalResultSet;
import dev.absjustcore.provider.utils.StoreMeta;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public final class MongoDBProvider implements Provider {

    private MongoClient client;

    private final Map<String, MongoCollection<Document>> collectionsStored = new HashMap<>();

    @Override
    public void init(StoreMeta storeMeta) {
        String password = storeMeta.fetchString("password");

        MongoCredential credential = password != null ? MongoCredential.createCredential(
                storeMeta.fetchString("username"),
                storeMeta.fetchString("dbname"),
                password.toCharArray()
        ) : null;

        String address = storeMeta.fetchString("address");

        if (address.startsWith(MongoURI.MONGODB_PREFIX) || address.startsWith("mongodb+srv://")) {
            this.client = new MongoClient(new MongoClientURI(address));
        } else {
            String[] addressSplit = address.split(":");

            this.client = new MongoClient(
                    new ServerAddress(addressSplit[0], addressSplit.length > 1 ? Integer.parseInt(addressSplit[1]) : ServerAddress.defaultPort()),
                    credential == null ? Collections.emptyList() : Collections.singletonList(credential)
            );
        }

        MongoDatabase database = this.client.getDatabase(storeMeta.fetchString("dbname"));

        for (String collectionName : new String[]{"players", "groups", "permissions"}) {
            this.collectionsStored.put(collectionName, database.getCollection(collectionName));
        }

        AbsjustPlugin.getLogger().info("Successfully initialized 'MongoDB' as database provider");
    }

    @Override
    public void store(StoreMeta storeMeta) {
        MongoCollection<Document> collection = this.collectionsStored.getOrDefault(storeMeta.getCollection(), null);

        if (collection == null) return;

        collection.replaceOne(
                new Document(storeMeta.fetchFirstValues(1)),
                new Document(storeMeta.getValues()).append("id", new ObjectId())
        );
    }

    @Override
    public void storeAsync(StoreMeta storeMeta) {
        TaskUtils.runAsync(() -> this.store(storeMeta));
    }

    @Override
    public int storeAndFetch(StoreMeta storeMeta) {
        MongoCollection<Document> collection = this.collectionsStored.getOrDefault(storeMeta.getCollection(), null);

        if (collection == null) return -1;

        ObjectId targetObjectId = new ObjectId();

        collection.replaceOne(
                new Document(storeMeta.fetchFirstValues(1)),
                new Document(storeMeta.getValues()).append("id", targetObjectId)
        );

        return targetObjectId.getTimestamp();
    }

    @Override
    public LocalResultSet fetch(StoreMeta storeMeta) {
        try {
            MongoCollection<Document> collection = this.collectionsStored.getOrDefault(storeMeta.getCollection(), null);

            if (collection == null) return null;

            // TODO: The first values inserted is the filter for mongodb
            Document document = collection.find(new Document(storeMeta.fetchFirstValues(1))).first();

            if (document == null) return null;

            return LocalResultSet.fetch(document);
        } finally {
            storeMeta.invalidate();
        }
    }

    public void close() {
        if (this.client != null) this.client.close();

        this.collectionsStored.clear();
    }
}