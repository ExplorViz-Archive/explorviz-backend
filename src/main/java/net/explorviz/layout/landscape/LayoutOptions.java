/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2009 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package net.explorviz.layout.landscape;

import java.util.EnumSet;

import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.Alignment;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeLabelPlacement;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.EdgeType;
import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.options.PortAlignment;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;


/**
 * Definition of layout options. Layout options are divided into programmatic options, which are
 * defined by static code, and user interface options, which are defined by extension point. The
 * former can be accessed with static variables, while the latter are accessed with methods.
 * 
 * @kieler.design 2011-03-14 reviewed by cmot, cds
 * @kieler.rating yellow 2013-01-09 review KI-32 by ckru, chsch
 * @author msp
 * 
 * @containsLayoutOptions
 */
public final class LayoutOptions {

    // ///// PROGRAMMATIC LAYOUT OPTIONS ///////

    /**
     * Whether the shift from the old layout to the new computed layout shall be animated.
     * [programmatically set]
     */
    public static final IProperty<Boolean> ANIMATE = new Property<Boolean>(
            "de.cau.cs.kieler.animate", true);

    /**
     * The minimal time for animations, in milliseconds. [programmatically set]
     */
    public static final IProperty<Integer> MIN_ANIMATION_TIME = new Property<Integer>(
            "de.cau.cs.kieler.minAnimTime", 400);

    /**
     * The maximal time for animations, in milliseconds. [programmatically set]
     */
    public static final IProperty<Integer> MAX_ANIMATION_TIME = new Property<Integer>(
            "de.cau.cs.kieler.maxAnimTime", 4000);

    /**
     * Factor for calculation of animation time. The higher the value, the longer the animation
     * time. If the value is 0, the resulting time is always equal to the minimum defined by
     * {@link #MIN_ANIMATION_TIME}. [programmatically set]
     */
    public static final IProperty<Integer> ANIMATION_TIME_FACTOR = new Property<Integer>(
            "de.cau.cs.kieler.animTimeFactor", 100);


    /**
     * Whether the associated node is to be interpreted as a comment box. In that case its placement
     * should be similar to how labels are handled. Any edges incident to a comment box specify to
     * which graph elements the comment is related. [programmatically set]
     */
    public static final IProperty<Boolean> COMMENT_BOX = new Property<Boolean>(
            "de.cau.cs.kieler.commentBox", false);

    /**
     * The diagram type of a parent node. Diagram types are defined via extension point and are
     * given an identifier and a name. The value of this option must be one of the pre-defined
     * diagram types. [programmatically set]
     */
    public static final IProperty<String> DIAGRAM_TYPE = new Property<String>(
            "de.cau.cs.kieler.diagramType");

    /**
     * Where to place an edge label: at the head, center, or tail. [programmatically set]
     */
    public static final IProperty<EdgeLabelPlacement> EDGE_LABEL_PLACEMENT =
            new Property<EdgeLabelPlacement>("de.cau.cs.kieler.edgeLabelPlacement",
                    EdgeLabelPlacement.UNDEFINED);

    /**
     * The type of edge. This is usually used for UML class diagrams, where associations must be
     * handled differently from generalizations. [programmatically set]
     */
    public static final IProperty<EdgeType> EDGE_TYPE = new Property<EdgeType>(
            "de.cau.cs.kieler.edgeType", EdgeType.NONE);

    /**
     * The name of the font that is used for a label. [programmatically set]
     */
    public static final IProperty<String> FONT_NAME = new Property<String>(
            "de.cau.cs.kieler.fontName");

    /**
     * The size of the font that is used for a label. [programmatically set]
     */
    public static final IProperty<Integer> FONT_SIZE = new Property<Integer>(
            "de.cau.cs.kieler.fontSize", 0);

    /**
     * Whether the associated node is to be interpreted as a hypernode. All incident edges of a
     * hypernode belong to the same hyperedge. [programmatically set]
     */
    public static final IProperty<Boolean> HYPERNODE = new Property<Boolean>(
            "de.cau.cs.kieler.hypernode", false);

    /**
     * This property is not used as option, but as output of the layout algorithms. It is attached
     * to edges and determines the points where junction symbols should be drawn in order to
     * represent hyperedges with orthogonal routing. Whether such points are computed depends on the
     * chosen layout algorithm and edge routing style. The points are put into the vector chain with
     * no specific order. [programmatically set]
     */
    public static final IProperty<KVectorChain> JUNCTION_POINTS = new Property<KVectorChain>(
            "de.cau.cs.kieler.junctionPoints", new KVectorChain());

    /**
     * Whether the hierarchy levels on the path from the selected element to the root of the diagram
     * shall be included in the layout process. [programmatically set]
     */
    public static final IProperty<Boolean> LAYOUT_ANCESTORS = new Property<Boolean>(
            "de.cau.cs.kieler.layoutAncestors", false);

    /**
     * The minimal height of a node. [programmatically set]
     */
    public static final IProperty<Float> MIN_HEIGHT = new Property<Float>(
            "de.cau.cs.kieler.minHeight", 0f, 0f);

    /**
     * The minimal width of a node. [programmatically set]
     */
    public static final IProperty<Float> MIN_WIDTH = new Property<Float>(
            "de.cau.cs.kieler.minWidth", 0f, 0f);

    /**
     * No layout is done for the associated element. This is used to mark parts of a diagram to
     * avoid their inclusion in the layout graph, or to mark parts of the layout graph to prevent
     * layout engines from processing them. [programmatically set]
     * 
     * If you wish to exclude the contents of a compound node from automatic layout, while the node
     * itself is still considered on its own layer, set
     * {@link de.cau.cs.kieler.kiml.util.FixedLayoutProvider FixedLayoutProvider#ID} as
     * {@link LayoutOptions#ALGORITHM} for this node.
     */
    public static final IProperty<Boolean> NO_LAYOUT = new Property<Boolean>(
            "de.cau.cs.kieler.noLayout", false);

    /**
     * Offset of a graph element. [programmatically set] This is mostly used to indicate the
     * distance of a port from its node: with a positive offset the port is moved outside of the
     * node, while with a negative offset the port is moved towards the inside. An offset of 0 means
     * that the port is placed directly on the node border, i.e.
     * <ul>
     * <li>if the port side is north, the port's south border touches the nodes's north border;</li>
     * <li>if the port side is east, the port's west border touches the nodes's east border;</li>
     * <li>if the port side is south, the port's north border touches the node's south border;</li>
     * <li>if the port side is west, the port's east border touches the node's west border.</li>
     * </ul>
     */
    public static final IProperty<Float> OFFSET = new Property<Float>("de.cau.cs.kieler.offset");

    /**
     * The offset to the port position where connections shall be attached. For compatibility
     * reasons, the ID still starts with the KLay Layered ID.
     */
    public static final IProperty<KVector> PORT_ANCHOR = new Property<KVector>(
            "de.cau.cs.kieler.klay.layered.portAnchor");

    /**
     * The index of a port in the fixed order of ports around its node. [programmatically set] The
     * order is assumed as clockwise, starting with the leftmost port on the top side. This option
     * must be set if {@link #PORT_CONSTRAINTS} is set to {@link PortConstraints#FIXED_ORDER} and no
     * specific positions are given for the ports. Additionally, the option {@link #PORT_SIDE} must
     * be defined in this case.
     */
    public static final IProperty<Integer> PORT_INDEX = new Property<Integer>(
            "de.cau.cs.kieler.portIndex");

    /**
     * On which side of its corresponding node a port is situated. [programmatically set] This
     * option must be set if {@link #PORT_CONSTRAINTS} is set to {@link PortConstraints#FIXED_SIDE}
     * or {@link PortConstraints#FIXED_ORDER} and no specific positions are given for the ports.
     */
    public static final IProperty<PortSide> PORT_SIDE = new Property<PortSide>(
            "de.cau.cs.kieler.portSide", PortSide.UNDEFINED);

    /**
     * Whether a progress bar shall be displayed during layout computations. [programmatically set]
     */
    public static final IProperty<Boolean> PROGRESS_BAR = new Property<Boolean>(
            "de.cau.cs.kieler.progressBar", false);

    /**
     * Whether the layout configuration of a certain graph element should be reset before a layout
     * run. This might be useful to pass further information to the layout algorithm or during the
     * execution of a 'layout' chain where multiple layout providers are executed after each other.
     * It allows an information flow from earlier to later layout algorithms. However, in most cases
     * a 'clean' graph is desirable. By default {@code true}. [programmatically set]
     */
    public static final IProperty<Boolean> RESET_CONFIG = new Property<Boolean>(
            "de.cau.cs.kieler.resetConfig", true);

    /**
     * The scaling factor to be applied to the corresponding node in recursive layout.
     * [programmatically set] It causes the corresponding node's size to be adjusted, and its ports
     * & labels to be sized and placed accordingly after the layout of that node has been determined
     * (and before the node itself and its siblings get arranged). The scaling is not reverted
     * afterwards, so the resulting layout graph contains the adjusted size and position data. This
     * option is currently not supported if {@link #LAYOUT_HIERARCHY} is set.
     */
    public static final IProperty<Float> SCALE_FACTOR = new Property<Float>(
            "de.cau.cs.kieler.scaleFactor", 1f);

    /**
     * The thickness of an edge. [programmatically set] This is a hint on the line width used to
     * draw an edge, possibly requiring more space to be reserved for it.
     */
    public static final IProperty<Float> THICKNESS = new Property<Float>(
            "de.cau.cs.kieler.thickness", 1f);

    /**
     * Whether the zoom level shall be set to view the whole diagram after layout. [programmatically
     * set]
     */
    public static final IProperty<Boolean> ZOOM_TO_FIT = new Property<Boolean>(
            "de.cau.cs.kieler.zoomToFit", false);

    // ///// USER INTERFACE LAYOUT OPTIONS ///////

    /**
     * Which layout algorithm to use for the content of a parent node. This can be either a layout
     * algorithm identifier or a layout type identifier. In the latter case KIML tries to find the
     * most suitable layout algorithm that matches the given layout type.
     */
    public static final IProperty<String> ALGORITHM = new Property<String>(
            "de.cau.cs.kieler.algorithm");

    /**
     * Alignment of a node. The meaning of this option depends on the specific layout algorithm.
     */
    public static final IProperty<Alignment> ALIGNMENT = new Property<Alignment>(
            "de.cau.cs.kieler.alignment", Alignment.AUTOMATIC);

    /**
     * The desired aspect ratio of a parent node. The algorithm should try to arrange the graph in
     * such a way that the width / height ratio of the resulting drawing approximates the given
     * value.
     */
    public static final IProperty<Float> ASPECT_RATIO = new Property<Float>(
            "de.cau.cs.kieler.aspectRatio", 0f);

    /**
     * The bend points of an edge. This is used by the
     * {@link de.cau.cs.kieler.kiml.FixedLayoutProvider} to specify a pre-defined routing for an
     * edge. The vector chain must include the source point, any bend points, and the target point.
     */
    public static final IProperty<KVectorChain> BEND_POINTS = new Property<KVectorChain>(
            "de.cau.cs.kieler.bendPoints");

    /**
     * Spacing of the content of a parent node to its inner border. The inner border is the node
     * border, which is given by width and height, with subtracted insets.
     */
    public static final IProperty<Float> BORDER_SPACING = new Property<Float>(
            "de.cau.cs.kieler.borderSpacing", -1.0f);

    /**
     * Whether the algorithm should run in debug mode for the content of a parent node.
     */
    public static final IProperty<Boolean> DEBUG_MODE = new Property<Boolean>(
            "de.cau.cs.kieler.debugMode", false);

    /**
     * The overall direction of layout: right, left, down, or up.
     */
    public static final IProperty<Direction> DIRECTION = new Property<Direction>(
            "de.cau.cs.kieler.direction", Direction.UNDEFINED);

    /**
     * What kind of edge routing style should be applied for the content of a parent node.
     * Algorithms may also set this option to single edges in order to mark them as splines. The
     * bend point list of edges with this option set to {@link EdgeRouting#SPLINES} must be
     * interpreted as control points for a piecewise cubic spline.
     */
    public static final IProperty<EdgeRouting> EDGE_ROUTING = new Property<EdgeRouting>(
            "de.cau.cs.kieler.edgeRouting", EdgeRouting.UNDEFINED);

    /**
     * Whether the size of contained nodes should be expanded to fill the whole area.
     */
    public static final IProperty<Boolean> EXPAND_NODES = new Property<Boolean>(
            "de.cau.cs.kieler.expandNodes", false);

    /**
     * Whether the algorithm should be run in interactive mode for the content of a parent node.
     * What this means exactly depends on how the specific algorithm interprets this option. Usually
     * in the interactive mode algorithms try to modify the current layout as little as possible.
     */
    public static final IProperty<Boolean> INTERACTIVE = new Property<Boolean>(
            "de.cau.cs.kieler.interactive", false);

    /**
     * Determines the amount of space to be left around labels.
     */
    public static final IProperty<Float> LABEL_SPACING = new Property<Float>(
            "de.cau.cs.kieler.labelSpacing", 3.0f, 0.0f);

    /**
     * Whether the whole hierarchy shall be layouted. If this option is not set, each hierarchy
     * level of the graph is processed independently, possibly by different layout algorithms,
     * beginning with the lowest level. If it is set, the algorithm is responsible to process all
     * hierarchy levels that are contained in the associated parent node.
     * 
     * @see GraphFeature#COMPOUND
     */
    public static final IProperty<Boolean> LAYOUT_HIERARCHY = new Property<Boolean>(
            "de.cau.cs.kieler.layoutHierarchy", false);

    /**
     * The way node labels are placed. Defaults to node labels not being touched.
     */
    public static final IProperty<EnumSet<NodeLabelPlacement>> NODE_LABEL_PLACEMENT =
            new Property<EnumSet<NodeLabelPlacement>>("de.cau.cs.kieler.nodeLabelPlacement",
                    NodeLabelPlacement.fixed());

    /**
     * The constraints on port positions for the associated node.
     */
    public static final IProperty<PortConstraints> PORT_CONSTRAINTS =
            new Property<PortConstraints>("de.cau.cs.kieler.portConstraints",
                    PortConstraints.UNDEFINED);

    /**
     * How port labels are placed.
     */
    public static final IProperty<PortLabelPlacement> PORT_LABEL_PLACEMENT =
            new Property<PortLabelPlacement>("de.cau.cs.kieler.portLabelPlacement",
                    PortLabelPlacement.OUTSIDE);

    /**
     * How much space to leave between ports if their positions are determined by the layout
     * algorithm.
     */
    public static final IProperty<Float> PORT_SPACING = new Property<Float>(
            "de.cau.cs.kieler.portSpacing", -1f, 0f);

    /**
     * The default port distribution for all sides.
     */
    public static final IProperty<PortAlignment> PORT_ALIGNMENT = new Property<PortAlignment>(
            "de.cau.cs.kieler.portAlignment", PortAlignment.JUSTIFIED);

    /**
     * The port distribution for northern side.
     */
    public static final IProperty<PortAlignment> PORT_ALIGNMENT_NORTH =
            new Property<PortAlignment>("de.cau.cs.kieler.portAlignment.north",
                    PortAlignment.UNDEFINED);

    /**
     * The port distribution for southern side.
     */
    public static final IProperty<PortAlignment> PORT_ALIGNMENT_SOUTH =
            new Property<PortAlignment>("de.cau.cs.kieler.portAlignment.south",
                    PortAlignment.UNDEFINED);

    /**
     * The port distribution for western side.
     */
    public static final IProperty<PortAlignment> PORT_ALIGNMENT_WEST = new Property<PortAlignment>(
            "de.cau.cs.kieler.portAlignment.west", PortAlignment.UNDEFINED);

    /**
     * The port distribution for eastern side.
     */
    public static final IProperty<PortAlignment> PORT_ALIGNMENT_EAST = new Property<PortAlignment>(
            "de.cau.cs.kieler.portAlignment.east", PortAlignment.UNDEFINED);

    /**
     * The position of a node, port, or label. This is used by the
     * {@link de.cau.cs.kieler.kiml.FixedLayoutProvider} to specify a pre-defined position.
     */
    public static final IProperty<KVector> POSITION = new Property<KVector>(
            "de.cau.cs.kieler.position");

    /**
     * The priority of a graph element. The meaning of this option depends on the specific layout
     * algorithm and the context where it is used.
     */
    public static final IProperty<Integer> PRIORITY = new Property<Integer>(
            "de.cau.cs.kieler.priority");

    /**
     * A pre-defined seed for pseudo-random number generators. This can be used to control
     * randomized layout algorithms. If the value is 0, the seed shall be determined pseudo-randomly
     * (e.g. from the system time).
     */
    public static final IProperty<Integer> RANDOM_SEED = new Property<Integer>(
            "de.cau.cs.kieler.randomSeed");

    /**
     * Whether a self loop should be routed around a node or inside that node. The latter will make
     * the node a compound node if it isn't already, and will require the layout algorithm to support
     * compound nodes with hierarchical ports.
     */
    public static final IProperty<Boolean> SELF_LOOP_INSIDE = new Property<Boolean>(
            "de.cau.cs.kieler.selfLoopInside", false);
    
    /**
     * Property for choosing whether connected components are processed separately.
     */
    public static final IProperty<Boolean> SEPARATE_CC = new Property<Boolean>(
            "de.cau.cs.kieler.separateConnComp");

    /**
     * Constraints for determining node sizes. Each member of the set specifies something that
     * should be taken into account when calculating node sizes. The empty set corresponds to node
     * sizes being fixed.
     */
    public static final IProperty<EnumSet<SizeConstraint>> SIZE_CONSTRAINT =
            new Property<EnumSet<SizeConstraint>>("de.cau.cs.kieler.sizeConstraint",
                    SizeConstraint.fixed());

    /**
     * Options modifying the behavior of the size constraints set on a node. Each member of the set
     * specifies something that should be taken into account when calculating node sizes. The empty
     * set corresponds to no further modifications.
     */
    public static final IProperty<EnumSet<SizeOptions>> SIZE_OPTIONS =
            new Property<EnumSet<SizeOptions>>("de.cau.cs.kieler.sizeOptions", EnumSet.of(
                    SizeOptions.DEFAULT_MINIMUM_SIZE,
                    SizeOptions.COMPUTE_PADDING));
    
    /**
     * Overall spacing between elements. This is mostly interpreted as the minimal distance between
     * each two nodes and should also influence the spacing between edges.
     */
    public static final IProperty<Float> SPACING = new Property<Float>("de.cau.cs.kieler.spacing",
            -1f, 0f);

    /**
     * Hide constructor to avoid instantiation.
     */
    private LayoutOptions() {
    }

}
