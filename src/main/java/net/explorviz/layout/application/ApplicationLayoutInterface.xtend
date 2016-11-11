package net.explorviz.layout.application

import net.explorviz.model.Application
import net.explorviz.model.Clazz
import net.explorviz.model.Communication
import net.explorviz.model.Component
import net.explorviz.model.helper.CommunicationAppAccumulator
import net.explorviz.model.helper.Draw3DNodeEntity
import net.explorviz.model.helper.EdgeState
import net.explorviz.math.Vector3f
import net.explorviz.layout.exceptions.LayoutException
import net.explorviz.math.MathHelpers
import java.util.ArrayList
import java.util.List
import net.explorviz.layout.application.LayoutSegment

class ApplicationLayoutInterface {

	public val static insetSpace = 4.0f
	public val static labelInsetSpace = 8.0f

	public val static externalPortsExtension = new Vector3f(3f, 3.5f, 3f)

	val static clazzWidth = 2.0f

	val static floorHeight = 0.75f * 4f

	val static clazzSizeDefault = 0.05f
	val static clazzSizeEachStep = 1.1f

	val static pipeSizeDefault = 0.1f
	val static pipeSizeEachStep = 0.45f

	val static comp = new ComponentAndClassComparator()

	def static applyLayout(Application application) throws LayoutException {
		
		val foundationComponent = application.components.get(0)

		calcClazzHeight(foundationComponent)
		initNodes(foundationComponent)

		doLayout(foundationComponent)
		setAbsoluteLayoutPosition(foundationComponent)

		layoutEdges(application)

		for (commu : application.incomingCommunications)
			layoutIncomingCommunication(commu, application.components.get(0))

		for (commu : application.outgoingCommunications)
			layoutOutgoingCommunication(commu, application.components.get(0))

		application
	}

	def private static void calcClazzHeight(Component component) {
		val clazzes = new ArrayList<Clazz>()
		getClazzList(component, clazzes, true)

		val instanceCountList = new ArrayList<Integer>()
		for (clazz : clazzes)
			instanceCountList.add(clazz.instanceCount)

		val categories = MathHelpers::getCategoriesForClazzes(instanceCountList)

		for (clazz : clazzes)
			clazz.height = (clazzSizeEachStep * categories.get(clazz.instanceCount) + clazzSizeDefault) * 4f
	}

	def private static void getClazzList(Component component, List<Clazz> clazzes, boolean beginning) {
		for (child : component.children)
			getClazzList(child, clazzes, false)

		for (clazz : component.clazzes)
			clazzes.add(clazz)
	}

	def private static void initNodes(Component component) {
		for (child : component.children)
			initNodes(child)

		for (clazz : component.clazzes)
			applyMetrics(clazz)

		applyMetrics(component)
	}

	def private static applyMetrics(Clazz clazz) {
		clazz.width = clazzWidth
		clazz.depth = clazzWidth
	}

	def private static applyMetrics(Component component) {
		component.height = getHeightOfComponent(component)
		component.width = -1f
		component.depth = -1f
	}

	def private static getHeightOfComponent(Component component) {
		if (!component.opened) {
			var childrenHeight = floorHeight

			for (child : component.children)
				if (child.height > childrenHeight)
					childrenHeight = child.height

			for (child : component.clazzes)
				if (child.height > childrenHeight)
					childrenHeight = child.height

			childrenHeight + 0.1f
		} else {
			floorHeight
		}
	}

	def private static void doLayout(Component component) {
		for (child : component.children)
			doLayout(child)

		layoutChildren(component)
	}

	def private static layoutChildren(Component component) {
		val tempList = new ArrayList<Draw3DNodeEntity>()
		tempList.addAll(component.clazzes)
		tempList.addAll(component.children)

		val segment = layoutGeneric(tempList, component.opened)

		component.width = segment.width
		component.depth = segment.height
	}

	def private static layoutGeneric(List<Draw3DNodeEntity> children, boolean openedComponent) {
		val rootSegment = createRootSegment(children)

		var maxX = 0f
		var maxZ = 0f

		children.sortInplace(comp)

		for (child : children) {
			val childWidth = (child.width + insetSpace * 2)
			val childHeight = (child.depth + insetSpace * 2)
			child.positionY = 0f

			val foundSegment = rootSegment.insertFittingSegment(childWidth, childHeight)

			child.positionX = foundSegment.startX + insetSpace
			child.positionZ = foundSegment.startZ + insetSpace

			if (foundSegment.startX + childWidth > maxX) {
				maxX = foundSegment.startX + childWidth
			}
			if (foundSegment.startZ + childHeight > maxZ) {
				maxZ = foundSegment.startZ + childHeight
			}
		}

		rootSegment.width = maxX
		rootSegment.height = maxZ

		addLabelInsetSpace(rootSegment, children)

		rootSegment
	}

	def static addLabelInsetSpace(LayoutSegment segment, List<Draw3DNodeEntity> entities) {
		for (entity : entities)
			entity.positionX = entity.positionX + labelInsetSpace

		segment.width = segment.width + labelInsetSpace
	}

	private def static createRootSegment(List<Draw3DNodeEntity> children) {
		var worstCaseWidth = 0f
		var worstCaseHeight = 0f

		for (child : children) {
			worstCaseWidth = worstCaseWidth + (child.width + insetSpace * 2)
			worstCaseHeight = worstCaseHeight + (child.depth + insetSpace * 2)
		}

		val rootSegment = new LayoutSegment()
		rootSegment.startX = 0f
		rootSegment.startZ = 0f

		rootSegment.width = worstCaseWidth
		rootSegment.height = worstCaseHeight

		rootSegment
	}

	def private static void setAbsoluteLayoutPosition(Component component) {
		for (child : component.children) {
			child.positionX = child.positionX + component.positionX
			child.positionY = child.positionY + component.positionY
			if (component.opened) {
				child.positionY = child.positionY + component.height
			}
			child.positionZ = child.positionZ + component.positionZ
			setAbsoluteLayoutPosition(child)
		}

		for (clazz : component.clazzes) {
			clazz.positionX = clazz.positionX + component.positionX
			clazz.positionY = clazz.positionY + component.positionY
			if (component.opened) {
				clazz.positionY = clazz.positionY + component.height
			}
			clazz.positionZ = clazz.positionZ + component.positionZ
		}
	}

	def private static layoutEdges(Application application) {
		for (commu : application.communicationsAccumulated) {
			//commu.clearAllPrimitiveObjects
			//commu.clearAllHandlers
		}
		application.communicationsAccumulated.clear

		for (commuFromApp : application.communications) {
			if (!commuFromApp.hidden) {
				val source = if (commuFromApp.source.parent.opened)
						commuFromApp.source
					else
						findFirstParentOpenComponent(commuFromApp.source.parent)
				val target = if (commuFromApp.target.parent.opened)
						commuFromApp.target
					else
						findFirstParentOpenComponent(commuFromApp.target.parent)
				if (source != null && target != null) {
					var found = false
					for (commu : application.communicationsAccumulated) {
						if (found == false) {
							found = ((commu.source == source) && (commu.target == target) ||
								(commu.source == target) && (commu.target == source))

								if (found) {
									commu.requests = commu.requests + commuFromApp.requests
									commu.aggregatedCommunications.add(commuFromApp)
								}
							}
						}

						if (found == false) {
							val newCommu = new CommunicationAppAccumulator()
							newCommu.source = source
							newCommu.target = target
							newCommu.requests = commuFromApp.requests

							val start = new Vector3f(source.positionX + source.width / 2f, source.positionY,
								source.positionZ + source.depth / 2f)
							val end = new Vector3f(target.positionX + target.width / 2f, target.positionY + 0.05f,
								target.positionZ + target.depth / 2f)

							newCommu.points.add(start)
							newCommu.points.add(end)

							newCommu.aggregatedCommunications.add(commuFromApp)

							application.communicationsAccumulated.add(newCommu)
						}
					}
				}
			}

			calculatePipeSizeFromQuantiles(application)
		}

		def private static Component findFirstParentOpenComponent(Component entity) {
			if (entity.parentComponent == null || entity.parentComponent.opened) {
				return entity
			}

			return findFirstParentOpenComponent(entity.parentComponent)
		}

		public def static calculatePipeSizeFromQuantiles(Application application) {
			val requestsList = new ArrayList<Integer>
			gatherRequestsIntoList(application, requestsList)

			val categories = MathHelpers::getCategoriesForCommunication(requestsList)

			for (commu : application.communicationsAccumulated)
				if (commu.source != commu.target && commu.state != EdgeState.HIDDEN) {
					commu.pipeSize = categories.get(commu.requests) * pipeSizeEachStep + pipeSizeDefault
				}

			for (commu : application.incomingCommunications) {
					commu.lineThickness = categories.get(commu.requests) * pipeSizeEachStep + pipeSizeDefault
			}
			
			for (commu : application.outgoingCommunications) {
					commu.lineThickness = categories.get(commu.requests) * pipeSizeEachStep + pipeSizeDefault
			}
		}

		private def static gatherRequestsIntoList(Application application, ArrayList<Integer> requestsList) {
			for (commu : application.communicationsAccumulated)
				if (commu.source != commu.target && commu.state != EdgeState.HIDDEN)
					requestsList.add(commu.requests)

			for (commu : application.incomingCommunications)
					requestsList.add(commu.requests)

			for (commu : application.outgoingCommunications)
					requestsList.add(commu.requests)
		}

		def private static void layoutIncomingCommunication(Communication commu, Component foundation) {
			val centerCommuIcon = new Vector3f(foundation.positionX - externalPortsExtension.x * 6f,
				foundation.positionY - foundation.extension.y + externalPortsExtension.y,
				foundation.positionZ + foundation.extension.z * 2f - externalPortsExtension.z)

			layoutInAndOutCommunication(commu, commu.targetClazz, centerCommuIcon)
		}

		def private static void layoutOutgoingCommunication(Communication commu, Component foundation) {
			val centerCommuIcon = new Vector3f(
				foundation.positionX + foundation.extension.x * 2f + externalPortsExtension.x * 4f,
				foundation.positionY - foundation.extension.y + externalPortsExtension.y,
				foundation.positionZ + foundation.extension.z * 2f - externalPortsExtension.z - 12f)

			layoutInAndOutCommunication(commu, commu.sourceClazz, centerCommuIcon)
		}

		def private static void layoutInAndOutCommunication(Communication commu, Clazz internalClazz,
			Vector3f centerCommuIcon) {
			commu.pointsFor3D.clear
			commu.pointsFor3D.add(centerCommuIcon)

			if (internalClazz != null) {
				val end = new Vector3f()
				end.x = internalClazz.positionX + internalClazz.width / 2f
				end.y = internalClazz.centerPoint.y
				end.z = internalClazz.positionZ + internalClazz.depth / 2f
				commu.pointsFor3D.add(end)
			}
		}
	}
