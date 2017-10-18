package net.explorviz.math

import java.util.Map
import java.util.HashMap
import java.util.List
import java.util.ArrayList

class MathHelpers {
	def static Map<Integer, Float> getCategoriesForCommunication(List<Integer> list) {
		genericCategories(list, true);
	}

	def static genericCategories(List<Integer> list, boolean linear) {
		val result = new HashMap<Integer, Float>()

		if (list.empty) {
			return result
		}

		list.sortInplace
		if (linear) {
			val listWithout0 = new ArrayList<Integer>()
			for (entry : list)
				if (entry != 0)
					listWithout0.add(entry)

			if (listWithout0.empty) {
				result.put(0, 0f)
				return result
			}
			
			useLinear(listWithout0, list, result)
		} else {

			val listWithout0And1 = new ArrayList<Integer>()
			for (entry : list)
				if (entry != 0 && entry != 1)
					listWithout0And1.add(entry)

			if (listWithout0And1.empty) {
				result.put(0, 0f)
				result.put(1, 1f)
				return result
			}

			useThreshholds(listWithout0And1, list, result)
		}

		result
	}

	def static Map<Integer, Float> getCategoriesForClazzes(List<Integer> list) {
		genericCategories(list, false);
	}

	def private static void useLinear(List<Integer> listWithout0, List<Integer> list, Map<Integer, Float> result) {
		var max = 1
		var secondMax = 1
		for (value : listWithout0) {
			if (value > max) {
				secondMax = max
				max = value
			}
		}

		val oneStep = secondMax / 4f

		val t1 = oneStep
		val t2 = oneStep * 2
		val t3 = oneStep * 3

		for (entry : list)
			result.put(entry, getCategoryFromLinearValues(entry, t1, t2, t3))
	}

	def private static float getCategoryFromLinearValues(int value, float t1, float t2, float t3) {
		if (value <= 0) {
			return 0
		} else if (value <= t1) {
			return 1.5f
		} else if (value <= t2) {
			return 2.5f
		} else if (value <= t3) {
			return 4f
		} else {
			return 6.5f
		}
	}

	def private static void useThreshholds(List<Integer> listWithout0And1, List<Integer> list,
		Map<Integer, Float> result) {
		var max = 1
		for (value : listWithout0And1) {
			if (value > max) {
				max = value
			}
		}

		val oneStep = max / 3f

		val t1 = oneStep
		val t2 = oneStep * 2

		for (entry : list)
			result.put(entry, getCategoryFromValues(entry, t1, t2))
	}

	def private static float getCategoryFromValues(int value, float t1, float t2) {
		if (value == 0) {
			return 0
		} else if (value == 1) {
			return 1
		}

		if (value <= t1) {
			return 2
		} else if (value <= t2) {
			return 3
		} else {
			return 4
		}
	}
}
