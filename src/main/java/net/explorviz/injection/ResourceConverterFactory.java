package net.explorviz.injection;

import org.glassfish.hk2.api.Factory;

import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.SerializationFeature;
import net.explorviz.model.Landscape;

public class ResourceConverterFactory implements Factory<ResourceConverter> {

	private ResourceConverter converter;

	public ResourceConverterFactory() {
		this.converter =  new ResourceConverter(Landscape.class, net.explorviz.model.System.class);
		this.converter.enableSerializationOption(SerializationFeature.INCLUDE_RELATIONSHIP_ATTRIBUTES);
	}

	@Override
	public void dispose(ResourceConverter arg0) {
	}

	@Override
	public ResourceConverter provide() {
		return converter;
	}

}
