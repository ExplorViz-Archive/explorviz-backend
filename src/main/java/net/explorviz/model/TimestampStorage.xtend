package net.explorviz.model

import net.explorviz.model.helper.BaseEntity
import com.github.jasminb.jsonapi.annotations.Type
import java.util.ArrayList
import com.github.jasminb.jsonapi.annotations.Relationship
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors
import org.glassfish.jersey.server.ParamException.QueryParamException

@Type("timestampstorage")
class TimestampStorage extends BaseEntity {

	@Relationship("timestamps")
	@Accessors List<Timestamp> timestamps = new ArrayList<Timestamp>

	def void addTimestamp(Timestamp t) {
		timestamps.add(t)
	}

	def List<Timestamp> filterTimestampsAfterTimestamp(long timestamp, int intervalSize) {

		val length = this.timestamps.size

		var position = 0;
		// iterate backwards and find passed timestamp	
		for (i : length >.. 0) {

			val element = this.timestamps.get(i)

			if (element.id.equals(timestamp)) {

				position = this.timestamps.indexOf(element)

				try {

					if (intervalSize != 0) {
						if (position + intervalSize > length) {
							return this.timestamps.subList(position, length)
						} else {
							return this.timestamps.subList(position, position + intervalSize)
						}
					} else {
						// all timestamps starting at position
						return this.timestamps.subList(position, length)
					}

				} catch (IllegalArgumentException e) {
					throw new QueryParamException(e, "Error in query parameter(s)", "10")
				}

			}
		}
		return new ArrayList<Timestamp>
	}

	def List<Timestamp> filterTimestampsBeforeTimestamp(long fromTimestamp, int intervalSize) {

		val length = this.timestamps.size

		var position = 0;
		// iterate backwards and find passed timestamp	
		for (i : length >.. 0) {

			val element = this.timestamps.get(i)

			if (element.id.equals(fromTimestamp)) {

				position = this.timestamps.indexOf(element)

				try {

					if (intervalSize != 0) {
						if (position - intervalSize < 0) {
							return this.timestamps.subList(0, position)
						} else {
							return this.timestamps.subList(position - intervalSize, position)
						}
					} else {
						// all timestamps starting at position
						return this.timestamps.subList(0, position)
					}

				} catch (IllegalArgumentException e) {
					throw new QueryParamException(e, "Error in query parameter(s)", "10")
				}

			}
		}
		return new ArrayList<Timestamp>
	}

	def List<Timestamp> filterOldestTimestamps(int intervalSize) {
		if (this.timestamps.empty)
			return new ArrayList<Timestamp>

		val length = this.timestamps.size;

		try {

			if (intervalSize != 0) {
				if (intervalSize >= length) {
					return this.timestamps.subList(0, length)
				} else {
					return this.timestamps.subList(0, intervalSize)
				}

			} else {
				return this.timestamps
			}

		} catch (IllegalArgumentException e) {
			throw new QueryParamException(e, "Error in query parameter(s)", "10")
		}

	}

	def List<Timestamp> filterMostRecentTimestamps(int intervalSize) {
		if (this.timestamps.empty)
			return new ArrayList<Timestamp>

		val length = this.timestamps.size;

		try {

			if (intervalSize != 0) {
				if (intervalSize >= length) {
					return this.timestamps.subList(0, length)
				} else {
					return this.timestamps.subList(length - intervalSize, length)
				}
			} else {
				return this.timestamps
			}

		} catch (IllegalArgumentException e) {
			throw new QueryParamException(e, "Error in query parameter(s)", "10")
		}

	}

}
