package ru.yanes.data;

import lombok.Getter;

@Getter
public enum Brush {
	XS(0.4F),
	S(0.5F),
	M(0.6F),
	L(0.7F),
	XL(0.8F),
	XXL(1.1F);

	private final float diameter;

	Brush(float diameter) {
		this.diameter = diameter;
	}
}
