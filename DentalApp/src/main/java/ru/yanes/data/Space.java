package ru.yanes.data;

import java.util.Objects;
import lombok.Data;

@Data
public class Space {
	private final Tooth leftTooth;
	private final Tooth rightTooth;
	private Brush innerBrush = null;
	private Brush outerBrush = null;

	public boolean isInnerEmpty() {
		return Objects.isNull(this.innerBrush);
	}

	public boolean isOuterEmpty() {
		return Objects.isNull(this.outerBrush);
	}

	public boolean isNotAvailable() {
		return !this.leftTooth.isAvailable() && this.rightTooth.isAvailable();
	}

	public void fillInnerBrush(Brush brush) {
		if (this.isNotAvailable()) {
			throw new IllegalStateException("Space is not available");
		} else {
			this.innerBrush = brush;
		}
	}

	public void fillOuterBrush(Brush brush) {
		if (this.isNotAvailable()) {
			throw new IllegalStateException("Space is not available");
		} else {
			this.outerBrush = brush;
		}
	}

	public void emptyInnerBrush() {
		if (this.isNotAvailable()) {
			throw new IllegalStateException("Space is not available");
		} else {
			this.innerBrush = null;
		}
	}

	public void emptyOuterBrush() {
		if (this.isNotAvailable()) {
			throw new IllegalStateException("Space is not available");
		} else {
			this.outerBrush = null;
		}
	}
}