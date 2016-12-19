package net.explorviz.model.helper;

import net.explorviz.math.Vector4f;

public class ColorDefinitions {
	public static final Vector4f pipeColor = new Vector4f(242 / 255f, 144 / 255f, 28 / 255f, 1f);
	public static final Vector4f pipeHighlightColor = new Vector4f(255 / 255f, 0 / 255f, 0 / 255f,
			1f);
	public static final Vector4f pipeColorTrans = new Vector4f(242 / 255f, 144 / 255f, 28 / 255f,
			0.06f);

	public static final Vector4f systemPlusColor = new Vector4f(1f, 0.596078f, 0.11372549f, 1f);
	public static final Vector4f systemForegroundColor = new Vector4f(0f, 0f, 0f, 1f);
	public static final Vector4f systemBackgroundColor = new Vector4f(0.78f, 0.78f, 0.78f, 1f);

	public static final Vector4f nodeGroupPlusColor = new Vector4f(1f, 0.596078f, 0.11372549f, 1f);
	public static final Vector4f nodeGroupBackgroundColor = new Vector4f(0.08235f, 0.6f,
			0.16470588f, 1f);

	public static final Vector4f nodeForegroundColor = new Vector4f(1f, 1f, 1f, 1f);
	public static final Vector4f nodeBackgroundColor = new Vector4f(0f, 0.733333f, 0.2549019f, 1f);

	public static final Vector4f applicationForegroundColor = new Vector4f(1f, 1f, 1f, 1f);
	public static final Vector4f applicationBackgroundColor = new Vector4f(0.2745098f, 0.090196f,
			0.705882f, 1f);
	public static final Vector4f applicationBackgroundRightColor = new Vector4f(111 / 255f,
			82 / 255f, 180 / 255f, 1f);

	public static final Vector4f componentFoundationColor = systemBackgroundColor;
	public static final Vector4f componentFirstColor = nodeGroupBackgroundColor;
	public static final Vector4f componentSecondColor = nodeBackgroundColor;

	public static final Vector4f componentSyntheticColor = new Vector4f(27f / 255f, 88f / 255f,
			184f / 255f, 1f);
	public static final Vector4f componentSyntheticSecondColor = new Vector4f(86f / 255f,
			156f / 255f, 227f / 255f, 1f);

	public static final Vector4f clazzColor = applicationBackgroundColor;

	public static final Vector4f highlightColor = new Vector4f(0.9f, 0f, 0f, 1f);
	// BLUE: new Vector4f(0.0745098f, 0.090196f, 0.905882f, 1f);
	public static final Vector4f communicationInColor = new Vector4f(0.7f, 0f, 0f, 1f);
	public static final Vector4f communicationOutColor = new Vector4f(0f, 0f, 0.7f, 1f);
}
