package ru.yanes.data;

import lombok.Getter;

import java.awt.Color;

@Getter
public enum Brush {
	XS(0.4F, new Color(237,60,202)),
	S(0.5F, new Color(248,0,0)),
	M(0.6F, new Color(66,170,255)),
	L(0.7F, new Color(251, 255, 0)),
	XL(0.8F, new Color(76,187,23)),
	XXL(1.1F, new Color(178,236,93));

	private final float diameter;
	private final Color color;

	Brush(float diameter, Color color) {
		this.diameter = diameter;
		this.color = color;
	}
}
