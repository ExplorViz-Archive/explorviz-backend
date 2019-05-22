package net.explorviz.common.live_trace_processing;

import gnu.trove.map.hash.TIntObjectHashMap;

public class StringRegistryReceiver {
	private final TIntObjectHashMap<String> inverseStringReg = new TIntObjectHashMap<String>();

	public StringRegistryReceiver() {
	}

	public final String getStringFromId(final int id)
			throws IdNotAvailableException {
		final String result = inverseStringReg.get(id);
		if (result == null) {
			throw new IdNotAvailableException(id);
		}
		return result;
	}

	public int getSize() {
		return inverseStringReg.size();
	}

	public void putStringRecord(final int key, final String value) {
		// System.out.println("Put key " + key + " value " + value);
		inverseStringReg.put(key, value);
		// System.out.println("stringRegistryReceiver size is now " +
		// getSize());
	}
}
