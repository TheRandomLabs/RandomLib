package com.therandomlabs.randomlib.config.adapter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.therandomlabs.randomlib.TRLUtils;
import net.minecraftforge.common.config.Property;

public final class PathTypeAdapter implements TRLTypeAdapter {
	private final boolean isArray;

	public PathTypeAdapter(boolean isArray) {
		this.isArray = isArray;
	}

	@Override
	public Object getValue(Property property) {
		if(!isArray) {
			return TRLUtils.getPath(property.getString());
		}

		final String[] array = property.getStringList();
		final List<Path> values = new ArrayList<>(array.length);

		for(String element : array) {
			final Path path = TRLUtils.getPath(element);

			if(path != null) {
				values.add(path);
			}
		}

		return values.toArray(new Path[0]);
	}

	@Override
	public String asString(Object value) {
		return TRLUtils.toStringWithUnixPathSeparators((Path) value);
	}

	@Override
	public boolean isArray() {
		return isArray;
	}
}
