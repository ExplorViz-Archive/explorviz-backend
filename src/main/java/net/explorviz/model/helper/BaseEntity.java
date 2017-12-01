package net.explorviz.model.helper;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "id")
@SuppressWarnings("all")
public class BaseEntity implements Serializable {
	private static final AtomicLong ID_GENERATOR = new AtomicLong();

	@Id(LongIdHandler.class)
	@Accessors
	private Long id;

	public BaseEntity() {
		this.id = Long.valueOf(BaseEntity.ID_GENERATOR.incrementAndGet());
	}

	@Pure
	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}
}
