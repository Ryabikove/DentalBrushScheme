package ru.yanes.data;

import lombok.Data;

@Data
public class Tooth {
	private final int position;
	private boolean available;

	Tooth(int position) {
		this.position = position;
		this.available = switch (this.position) {
			case 48, 28, 38, 18 -> false;
			default -> true;
		};
	}
}
