package net.explorviz.history.repository.persistence.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Projections;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import net.explorviz.landscape.model.store.Timestamp;
import net.explorviz.shared.querying.Query;
import net.explorviz.shared.querying.QueryException;
import net.explorviz.shared.querying.QueryResult;
import net.explorviz.shared.querying.Queryable;
import org.bson.Document;

/**
 * Auxiliary repository for accessing timestamps of persistent landscapes objects.
 */
public class TimestampRepository implements Queryable<Timestamp> {

  private static final String FILTER_ARG_TYPE = "type";
  private static final String FILTER_ARG_FROM = "from";
  private static final String FILTER_ARG_TO = "to";

  private final MongoHelper mongoHelper;


  @Inject
  public TimestampRepository(final MongoHelper helper) {
    this.mongoHelper = helper;
  }


  @Override
  public QueryResult<Timestamp> query(final Query<Timestamp> query) throws QueryException {

    List<Timestamp> result = new ArrayList<>();

    if (query.getFilters().get(FILTER_ARG_TYPE) == null) {
      result.addAll(this.getReplayTimestamps());
      result.addAll(this.getLandscapeTimestamps());
    } else { // Add all
      findOfType(query.getFilters().get(FILTER_ARG_TYPE), result);
    }

    final List<String> fromFilters = query.getFilters().get(FILTER_ARG_FROM);
    final List<String> toFilters = query.getFilters().get(FILTER_ARG_TO);


    String from = null;
    String to = null;
    if (toFilters != null && !toFilters.isEmpty()) {
      to = toFilters.get(0);
    }
    if (fromFilters != null && !fromFilters.isEmpty()) {
      from = fromFilters.get(0);
    }

    try {
      result = TimestampQueryHelper.filterByTimeRange(result, from, to);
    } catch (final IllegalArgumentException e) {
      throw new QueryException("Filters 'from' and 'to' must be integral values", e);
    }


    result = TimestampQueryHelper.sort(result);

    // Paginate
    final long total = result.size();
    if (query.doPaginate()) {
      result = TimestampQueryHelper.paginate(result, query.getPageNumber(), query.getPageSize());
    }

    return new QueryResult<>(query, result, total);
  }

  private void findOfType(final List<String> types, final List<Timestamp> result) {
    for (final String type : types) {
      if (type.toLowerCase(Locale.ENGLISH).contentEquals("landscape")) {
        result.addAll(this.getLandscapeTimestamps());
      } else if (type.toLowerCase(Locale.ENGLISH).contentEquals("replay")) {
        result.addAll(this.getReplayTimestamps());
      }
    }
  }



  /**
   * Retrieves all landscape timestamps currently stored in the db. Each timestamp is a unique
   * identifier of an object.
   *
   * @return list of all timestamps
   */
  public List<Timestamp> getLandscapeTimestamps() {
    final MongoCollection<Document> landscapeCollection = this.mongoHelper.getLandscapeCollection();


    final FindIterable<Document> documents = landscapeCollection.find()
        .projection(Projections.include(MongoHelper.FIELD_TIMESTAMP,
            MongoHelper.FIELD_ID,
            MongoHelper.FIELD_REQUESTS));

    final List<Timestamp> resultList = new ArrayList<>();

    for (final Document doc : documents) {
      final String id = (String) doc.get(MongoHelper.FIELD_ID);
      final long timestamp = (long) doc.get(MongoHelper.FIELD_TIMESTAMP);
      final int totalRequests = (int) doc.get(MongoHelper.FIELD_REQUESTS);

      resultList.add(new Timestamp(id, timestamp, totalRequests)); // NOPMD
    }

    return resultList;
  }

  /**
   * Retrieves all replay landscape timestamps currently stored in the db. Each timestamp is a
   * unique identifier of an object.
   *
   * @return list of all timestamps
   */
  public List<Timestamp> getReplayTimestamps() {
    final MongoCollection<Document> landscapeCollection = this.mongoHelper.getReplayCollection();

    final FindIterable<Document> documents = landscapeCollection.find()
        .projection(Projections.include(MongoHelper.FIELD_ID,
            MongoHelper.FIELD_TIMESTAMP,
            MongoHelper.FIELD_REQUESTS));

    final List<Timestamp> resultList = new ArrayList<>();

    for (final Document doc : documents) {
      final String id = String.valueOf(doc.get(MongoHelper.FIELD_ID));
      final long timestamp = (long) doc.get(MongoHelper.FIELD_TIMESTAMP);
      final int totalRequests = (int) doc.get(MongoHelper.FIELD_REQUESTS);

      resultList.add(new Timestamp(id, timestamp, totalRequests)); // NOPMD
    }

    return resultList;
  }

}
