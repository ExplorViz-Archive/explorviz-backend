package net.explorviz.model

import net.explorviz.model.helper.BaseEntity
import com.github.jasminb.jsonapi.annotations.Type
import java.util.ArrayList
import com.github.jasminb.jsonapi.annotations.Relationship
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors

@Type("timestampstorage")
class TimestampStorage extends BaseEntity {

	@Relationship("timestamps")
	@Accessors List<Timestamp> timestamps = new ArrayList<Timestamp>

	new(String id) {
		this.id = id
	}

	def void addTimestamp(Timestamp t) {
		timestamps.add(t)
	}

	def List<Timestamp> filterTimestampsAfterTimestamp(long timestamp, int intervalSize) {

		if (intervalSize == 0) {
			return this.timestamps
		}

		val length = this.timestamps.size

		var position = 0;
		// iterate backwards and find passed timestamp	
		for (i : length >.. 0) {

			val element = this.timestamps.get(i)

			if (element.timestamp.equals(timestamp)) {

				position = this.timestamps.indexOf(element)

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

			}
		}
		return new ArrayList<Timestamp>
	}

	def List<Timestamp> filterTimestampsBeforeTimestamp(long fromTimestamp, int intervalSize) {

		val length = this.timestamps.size

		if (fromTimestamp == 0 && intervalSize == 0) {
			return this.timestamps
		}

		var position = 0;
		// iterate backwards and find passed timestamp	
		for (i : length >.. 0) {

			val element = this.timestamps.get(i)

			if (element.timestamp.equals(fromTimestamp)) {

				position = this.timestamps.indexOf(element)

				if (intervalSize != 0) {
					if (position - intervalSize < 0) {
						return this.timestamps.subList(0, position)
					} else {
						return this.timestamps.subList(position - intervalSize, position)
					}
				} else {
					// all timestamps starting at position
					return this.timestamps.subList(position, length)
				}

			}
		}
		return new ArrayList<Timestamp>
	}

	def List<Timestamp> filterOldestTimestamps(int intervalSize) {
		if (!this.timestamps.empty) {

			val length = this.timestamps.size;

			if (intervalSize != 0) {
				if (intervalSize + 1 >= length) {
					return this.timestamps.subList(0, length)
				} else {
					return this.timestamps.subList(0, intervalSize)
				}

			} else {
				return this.timestamps.subList(0, length)
			}

		} else {
			return new ArrayList<Timestamp>
		}
	}

	def List<Timestamp> filterMostRecentTimestamps(int intervalSize) {
		if (!this.timestamps.empty) {

			val length = this.timestamps.size;

			if (intervalSize != 0) {
				if (intervalSize + 1 >= length) {
					return this.timestamps.subList(0, length)
				} else {
					return this.timestamps.subList(length - intervalSize, length)
				}

			} else {
				return this.timestamps.subList(0, length)
			}

		} else {
			return new ArrayList<Timestamp>
		}
	}

}
