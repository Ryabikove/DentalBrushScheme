package ru.yanes.data;

import java.util.Objects;
import lombok.Data;

@Data
public class Space {
	private final Tooth leftTooth;
	private final Tooth rightTooth;
	private final int position;
	private Brush innerBrush = null;
	private Brush outerBrush = null;


	public boolean isInnerEmpty() {
		return Objects.isNull(this.innerBrush);
	}

	public boolean isOuterEmpty() {
		return Objects.isNull(this.outerBrush);
	}

	public boolean isAvailable() {
		return this.leftTooth.isAvailable() || this.rightTooth.isAvailable();
	}

	public void setInnerBrush(Brush brush) {
		if (this.isAvailable() && this.isOuterEmpty()) {
			if (Objects.equals(this.innerBrush, brush)) {
				this.innerBrush = null;
			} else {
				this.innerBrush = brush;
			}
		} else {
			throw new IllegalStateException("Space is not available");
		}
	}

	public void setOuterBrush(Brush brush) {
		if (this.isAvailable() && this.isInnerEmpty()) {
			if (Objects.equals(this.outerBrush, brush)) {
				this.outerBrush = null;
			} else {
				this.outerBrush = brush;
			}
		} else {
			throw new IllegalStateException("Space is not available");
		}
	}

	public void emptyInnerBrush() {
		if (this.isAvailable()) {
			this.innerBrush = null;
		} else {
			throw new IllegalStateException("Space is not available");
		}
	}

	public void emptyOuterBrush() {
		if (this.isAvailable()) {
			this.outerBrush = null;
		} else {
			throw new IllegalStateException("Space is not available");
		}
	}
}