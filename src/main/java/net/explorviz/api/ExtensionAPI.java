package net.explorviz.api;

/**
 * ExtensionAPI for ExplorViz Extensions
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public final class ExtensionAPI {

	private ExtensionAPI() {
		// no need to instantiate
	}

	/**
	 * Provides the ExplorViz ExtensionAPI for backend extensions
	 */
	public static ExtensionAPIImpl get() {
		return ExtensionAPIImpl.getInstance();
	}
}
