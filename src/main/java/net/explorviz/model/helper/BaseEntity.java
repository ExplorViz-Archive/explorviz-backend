package net.explorviz.model.helper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.xtext.xbase.lib.Pure;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;

@SuppressWarnings("serial")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "id")
public class BaseEntity implements Serializable {
	private static final AtomicLong ID_GENERATOR = new AtomicLong();

	@Id(LongIdHandler.class)
	private Long id;

	/*
	 * This attribute can be used by extensions to insert custom properties to any
	 * meta-model object. Non primitive types (your custom model class) must be
	 * annotated with type annotations, e.g., as shown in any meta-model entity
	 */
	private final Map<String, Object> extensionAttributes = new HashMap<String, Object>();

	private long timestamp;

	@Pure
	public Long getId() {
		return this.id;
	}

	public void initializeID() {
		this.id = Long.valueOf(BaseEntity.ID_GENERATOR.incrementAndGet());
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	public Map<String, Object> getExtensionAttributes() {
		return extensionAttributes;
	}

}
