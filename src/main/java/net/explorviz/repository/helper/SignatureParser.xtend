package net.explorviz.repository.helper;

class SignatureParser {
	def public static Signature parse(String operationSignatureStr, boolean javaConstructor) {
		val result = new Signature()

		var restOfOperationSignatureStr = parseParameterList(operationSignatureStr, result)
		parseModifiersAndReturnAndName(restOfOperationSignatureStr, javaConstructor, result)
		parseFQClassnameAndOperationName(javaConstructor, result)

		result
	}

	def private static String parseParameterList(String operationSignatureStr, Signature sig) {
		val openParenIdx = operationSignatureStr.indexOf('(')
		if (openParenIdx != -1) {
			val splitParams = operationSignatureStr.substring(openParenIdx + 1, operationSignatureStr.length() - 1).
				split(",")
			for (splitParam : splitParams) {
				sig.getParamTypeList.add(splitParam.trim())
			}
			operationSignatureStr.substring(0, openParenIdx)
		} else {
			operationSignatureStr
		}
	}

	def private static parseModifiersAndReturnAndName(String restOfOperationSignatureStr, boolean javaConstructor,
		Signature sig) {
		val nameBeginIdx = restOfOperationSignatureStr.lastIndexOf(' ')
		if (nameBeginIdx == -1) {
			sig.name = restOfOperationSignatureStr
		} else {
			val modRetNameArr = restOfOperationSignatureStr.split("\\s")
			var modifierEndLength = 0
			if (javaConstructor) {
				modifierEndLength = modRetNameArr.length - 1
			} else {
				sig.returnType = modRetNameArr.get(modRetNameArr.length - 2)
				modifierEndLength = modRetNameArr.length - 2
			}
			var i = 0
			while (i < modifierEndLength) {
				sig.getModifierList.add(modRetNameArr.get(i).trim())
				i = i + 1
			}
			sig.name = modRetNameArr.get(modRetNameArr.length - 1)
		}
	}

	def private static parseFQClassnameAndOperationName(boolean javaConstructor, Signature result) {
		val opNameIdx = result.getName.lastIndexOf('.')
		result.fullQualifiedName = if (opNameIdx != -1) {
			result.getName.substring(0, opNameIdx)
		} else {
			""
		}

		if (javaConstructor) {
			val onlyClassName = result.fullQualifiedName.substring(result.fullQualifiedName.lastIndexOf('.') + 1)
			result.operationName = "new " + onlyClassName
		} else {
			result.operationName = result.getName.substring(opNameIdx + 1)
		}
	}
}
