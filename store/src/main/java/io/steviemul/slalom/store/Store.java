package io.steviemul.slalom.store;

import com.couchbase.lite.Blob;
import com.couchbase.lite.Collection;
import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Function;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import io.steviemul.slalom.store.exception.StoreException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class Store {

  private static final String CONTENT = "content";
  private static final String CONTENT_TYPE_BINARY = "application/octet-stream";
  private static final String COUNT = "count";

  private final String name;

  private Database db;

  public Store start() throws StoreException {
    try {
      CouchbaseLite.init();

      log.info("Local Store initialized");

      db = new Database(name);
    } catch (CouchbaseLiteException e) {
      throw new StoreException("Error initializing store", e);
    }

    return this;
  }

  public Collection getCollection(String name) throws StoreException {
    try {
      return Optional.ofNullable(db.getCollection(name))
          .orElse(createCollection(name));
    } catch (CouchbaseLiteException e) {
      throw new StoreException("Error retrieving collection", e);
    }
  }

  public Collection createCollection(String name) throws StoreException {
    try {
      return db.createCollection(name);
    } catch (CouchbaseLiteException e) {
      throw new StoreException("Error creating collection", e);
    }
  }

  public int count(String collectionName) throws StoreException {

    try {
      Collection collection = getCollection(collectionName);

      Query countQuery = QueryBuilder.select(
              SelectResult.expression(Function.count(Expression.string("*")))
                  .as(COUNT))
          .from(DataSource.collection(collection));

      ResultSet results = countQuery.execute();

      return results.allResults().stream()
          .findFirst()
          .map(r -> r.getInt(COUNT))
          .orElse(0);
    } catch (CouchbaseLiteException e) {
      throw new StoreException("Error getting count", e);
    }
  }

  public Document save(String collectionName, String id, String json) throws StoreException {
    try {
      MutableDocument document = new MutableDocument(id)
          .setJSON(json);

      Collection collection = getCollection(collectionName);

      collection.save(document);

      return document;
    } catch (CouchbaseLiteException e) {
      throw new StoreException("Error creating document", e);
    }
  }

  public boolean exists(String collectionName, String id) {
    try {
      return (getCollection(collectionName).getDocument(id) != null);
    } catch (Exception e) {
      return false;
    }
  }

  public Document save(String collectionName, String id, byte[] content) throws StoreException {
    try {
      MutableDocument document = new MutableDocument(id)
          .setBlob(CONTENT, new Blob(CONTENT_TYPE_BINARY, content));

      Collection collection = getCollection(collectionName);

      collection.save(document);

      return document;
    } catch (CouchbaseLiteException e) {
      throw new StoreException("Error creating document", e);
    }
  }

  public byte[] getBlobContent(String collectionName, String id) throws StoreException {

    Document document = getDocument(collectionName, id);

    return document.getBlob(CONTENT).getContent();
  }

  public Document getDocument(String collectionName, String id) throws StoreException {
    try {
      Collection collection = getCollection(collectionName);

      return collection.getDocument(id);
    } catch (CouchbaseLiteException e) {
      throw new StoreException("Error retrieving document", e);
    }
  }

  public void delete() {
    try {
      db.delete();
    } catch (CouchbaseLiteException e) {
      log.error("Unable to delete db");
    }
  }
}
