package ru.yanes.data;

import lombok.Data;

@Data
public class Tooth {
	private boolean available = true;
	private final int position;
}
