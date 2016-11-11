package net.explorviz.layout.application

import org.eclipse.xtend.lib.annotations.Accessors

class LayoutSegment {
	@Accessors float startX
	@Accessors float startZ

	@Accessors float width
	@Accessors float height

	@Accessors LayoutSegment upperRightChild
	@Accessors LayoutSegment lowerChild
	
	@Accessors LayoutSegment parent

	@Accessors boolean used = false

	def LayoutSegment insertFittingSegment(float toFitWidth, float toFitHeight) {
		if (used == false && toFitWidth <= width && toFitHeight <= height) {
			val resultSegment = new LayoutSegment()
			upperRightChild = new LayoutSegment()
			lowerChild = new LayoutSegment()

			resultSegment.startX = startX
			resultSegment.startZ = startZ
			resultSegment.width = toFitWidth
			resultSegment.height = toFitHeight
			resultSegment.parent = this

			upperRightChild.startX = startX + toFitWidth
			upperRightChild.startZ = startZ
			upperRightChild.width = width - toFitWidth
			upperRightChild.height = toFitHeight
			upperRightChild.parent = this

			if (upperRightChild.width <= 0f) {
				upperRightChild = null
			}
			
			lowerChild.startX = startX
			lowerChild.startZ = startZ + toFitHeight
			lowerChild.width = width
			lowerChild.height = height - toFitHeight
			lowerChild.parent = this
			
			if (lowerChild.height <= 0f) {
				lowerChild = null
			}
			
			used = true

			return resultSegment
		} else {
			var LayoutSegment resultFromUpper = null
			var LayoutSegment resultFromLower = null

			if (upperRightChild != null) {
				resultFromUpper = upperRightChild.insertFittingSegment(toFitWidth, toFitHeight)
			}

			if (lowerChild != null) {
				resultFromLower = lowerChild.insertFittingSegment(toFitWidth, toFitHeight)
			}

			if (resultFromUpper == null) {
				return resultFromLower
			} else if (resultFromLower == null) {
				return resultFromUpper
			} else {
				// choose best fitting square
				val upperBoundX = resultFromUpper.startX + resultFromUpper.width

				val lowerBoundZ = resultFromLower.startZ + resultFromLower.height
				
				if (upperBoundX <= lowerBoundZ) {
					resultFromLower.parent.used = false
					return resultFromUpper
				} else {
					resultFromUpper.parent.used = false
					return resultFromLower
				}
			}
		}
	}
}
