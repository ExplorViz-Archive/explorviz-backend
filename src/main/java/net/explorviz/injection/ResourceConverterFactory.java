package net.explorviz.injection;

import org.glassfish.hk2.api.Factory;

import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.SerializationFeature;

import net.explorviz.model.Application;
import net.explorviz.model.Clazz;
import net.explorviz.model.Communication;
import net.explorviz.model.CommunicationClazz;
import net.explorviz.model.Component;
import net.explorviz.model.Landscape;
import net.explorviz.model.Node;
import net.explorviz.model.NodeGroup;

public class ResourceConverterFactory implements Factory<ResourceConverter> {

	private ResourceConverter converter;

	public ResourceConverterFactory() {
		this.converter = new ResourceConverter(Landscape.class, net.explorviz.model.System.class, NodeGroup.class,
				Node.class, Application.class, Component.class, Clazz.class, CommunicationClazz.class, Communication.class);
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
