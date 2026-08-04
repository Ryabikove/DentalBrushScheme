package ru.yanes.data;

import lombok.Getter;
import java.util.Arrays;

@Getter
public class Mouth {
	private final Tooth[] teeth;
	private final Space[] spaces;

	public Mouth(int teeth) {
		this.teeth = new Tooth[teeth];
		this.spaces = new Space[teeth - 1];

		for(int i = 0; i < teeth; ++i) {
			this.teeth[i] = new Tooth(i % 8 + 1 + 10 * (i / 8 + 1));
		}

		for(int i = 0; i < teeth - 1; ++i) {
			this.spaces[i] = new Space(this.teeth[i], this.teeth[i + 1]);
		}

	}

	public Tooth getTooth(int pos) {
		if (pos >= 0 && pos < this.teeth.length) {
			return this.teeth[pos];
		} else {
			throw new IllegalArgumentException("Invalid position: " + pos);
		}
	}

	public Space getSpace(int pos) {
		if (pos >= 0 && pos < this.spaces.length) {
			return this.spaces[pos];
		} else {
			throw new IllegalArgumentException("Invalid position: " + pos);
		}
	}

	public void resetAll() {
		Arrays.stream(this.teeth).forEach((tooth) -> tooth.setAvailable(true));
		Arrays.stream(this.spaces).forEach((space) -> {
			space.emptyInnerBrush();
			space.emptyOuterBrush();
		});
	}
}
