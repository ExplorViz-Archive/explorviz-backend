package net.explorviz.model.helper

import java.util.concurrent.atomic.AtomicLong
import com.github.jasminb.jsonapi.LongIdHandler
import com.github.jasminb.jsonapi.annotations.Id
import org.eclipse.xtend.lib.annotations.Accessors
import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators

// Needed for cyclical serialization
@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator, property="id")
class BaseEntity implements Serializable {

	private static final AtomicLong ID_GENERATOR = new AtomicLong();

	@Id(LongIdHandler)
	@Accessors Long id

	new() {
		id = ID_GENERATOR.incrementAndGet();
	}

}
