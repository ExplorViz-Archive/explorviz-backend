package net.explorviz.history.repository.persistence.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Projections;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.explorviz.landscape.model.store.Timestamp;
import net.explorviz.shared.querying.Query;
import net.explorviz.shared.querying.QueryException;
import net.explorviz.shared.querying.QueryResult;
import net.explorviz.shared.querying.Queryable;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Auxiliary repository for accessing timestamps of persistent landscapes objects.
 *
 *
 */
public class TimestampRepository implements Queryable<Timestamp> {

  private static final Logger LOGGER = LoggerFactory.getLogger(TimestampRepository.class);

  private final MongoHelper mongoHelper;


  @Inject
  public TimestampRepository(final MongoHelper helper) {
    this.mongoHelper = helper;
  }


  @Override
  public QueryResult<Timestamp> query(final Query<Timestamp> query) throws QueryException {
    final String filterArgType = "type";
    final String filterArgFrom = "from";
    final String filterArgTo = "to";


    List<Timestamp> result = new ArrayList<>();

    if (query.getFilters().get(filterArgType) != null) {
      for (final String type : query.getFilters().get(filterArgType)) {
        if (type.toLowerCase().contentEquals("landscape")) {
          result.addAll(this.getLandscapeTimestamps());
        } else if (type.toLowerCase().contentEquals("replay")) {
          result.addAll(this.getReplayTimestamps());
        } else {
          // Unknown type
          return new QueryResult<>(query, new ArrayList<Timestamp>(), 0);
        }
      }
    } else { // Add all
      result.addAll(this.getReplayTimestamps());
      result.addAll(this.getLandscapeTimestamps());
    }

    if (query.getFilters().get(filterArgFrom) != null) {
      if (query.getFilters().get(filterArgFrom).size() > 1) {
        LOGGER.warn("More than one 'from' given, only applying the first");
      }
      try {
        final long fromTs = Long.parseLong(query.getFilters().get(filterArgFrom).get(0));
        if (fromTs <= 0) {
          throw new QueryException("Filter 'from' must be positive");
        }
        result = result.parallelStream()
            .filter(t -> fromTs <= t.getTimestamp())
            .collect(Collectors.toList());
      } catch (final NumberFormatException e) {
        throw new QueryException("Filter 'from' must be integral", e);
      }
    }

    if (query.getFilters().get(filterArgTo) != null) {
      if (query.getFilters().get(filterArgTo).size() > 1) {
        LOGGER.warn("More than one 'to' given, only applying the first");
      }
      try {
        final long toTs = Long.parseLong(query.getFilters().get(filterArgTo).get(0));
        if (toTs <= 0) {
          throw new QueryException("Filter 'to' must be positive");
        }
        result = result.parallelStream()
            .filter(t -> toTs >= t.getTimestamp())
            .collect(Collectors.toList());
      } catch (final NumberFormatException e) {
        throw new QueryException("Filter 'from' must be integral", e);
      }
    }

    // Sort descending
    result.sort((t1, t2) -> {
      if (t1.getTimestamp() == t2.getTimestamp()) {
        return 0;
      }
      return t1.getTimestamp() > t2.getTimestamp() ? -1 : 1;
    });

    // Paginate
    final long total = result.size();
    if (query.doPaginate()) {
      final int pageFrom = Math.min(query.getPageNumber() * query.getPageSize(), result.size());
      final int pageTo = Math.min(query.getPageSize() * query.getPageNumber() + query.getPageSize(),
          result.size());

      result = new ArrayList<>(result.subList(pageFrom, pageTo));
    }

    return new QueryResult<>(query, result, total);
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
