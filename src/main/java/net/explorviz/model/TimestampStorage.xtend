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

	def List<Timestamp> filterTimestamps(boolean fromOldest, long fromTimestamp, int intervalSize) {
		if (!this.timestamps.empty) {

			val length = this.timestamps.size;
			var position = 0;
			
			if(!fromOldest && fromTimestamp == 0 && intervalSize == 0) {
				return this.timestamps
			}

			if (fromOldest) {
				val oldestElement = this.timestamps.get(0)
				position = this.timestamps.indexOf(oldestElement)
				if (intervalSize != 0) {
					return this.timestamps.subList(position, intervalSize)
				} else {
					// all timestamps
					return this.timestamps.subList(position, length)
				}
			} 
			else {
				// iterate backwards and find passed timestamp	
				for (i : length >.. 0) {
					val element = this.timestamps.get(i)
					if (element.timestamp.equals(fromTimestamp)) {
						position = this.timestamps.indexOf(element)
						if (intervalSize != 0) {
							return this.timestamps.subList(position, intervalSize)
						} else {
							// all timestamps starting at position
							return this.timestamps.subList(position, length)
						}

					}
				}
			}

			// if timestamp not found => return empty list				
			return new ArrayList<Timestamp>
		} else {
			return new ArrayList<Timestamp>
		}
	}

}
