package ru.yanes.data;

import lombok.Getter;
import java.util.Arrays;

@Getter
public class Mouth {
	private final Tooth[] teeth;
	private final Space[] spaces;

	public Mouth() {
		int[] TEETH_POSITION = new int[] {28, 27, 26, 25, 24, 23, 22, 21, 11, 12, 13, 14, 15, 16, 17, 18, 48, 47, 46, 45, 44, 43, 42, 41, 31, 32, 33, 34, 35, 36, 37, 38};

		int teethNumber = TEETH_POSITION.length;
		this.teeth = new Tooth[teethNumber];

		for(int i = 0; i < teethNumber; ++i) {
			this.teeth[i] = new Tooth(TEETH_POSITION[i]);
//			System.out.println("Tooth - " + i + ", with position - " + TEETH[i].getPosition() + ";");
		}

		// Number of spaces for each jaw lesser by 1 (for 2 jaws by 2)
		int spaceNumber = teeth.length - 2;
		this.spaces = new Space[spaceNumber];


		for(int i = 0; i < spaceNumber; ++i) {
			//As we don't have spaces between 38 and 28 teeth we skip this pare
			if (i >= 15) {
				this.spaces[i] = new Space(this.teeth[i + 1], this.teeth[i + 2], i);
			} else {
				this.spaces[i] = new Space(this.teeth[i], this.teeth[i + 1], i);
			}
//			System.out.println("Space - " + i + ", with left tooth - " + this.SPACES[i].getLeftTooth().getPosition() + ", and right tooth - " + this.SPACES[i].getRightTooth().getPosition() + ";");
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
