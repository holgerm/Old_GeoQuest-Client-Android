package com.qeevee.ui;

public enum BitmapType {

	MARKER(20), FULLWIDTH(3), FULLSCREEN(3); // LISTIMAGE(20), GRIDWithFourRows,
												// GRIDWithThreeRows,
												// GRIDWithTwoRows;

	public int capacity;

	BitmapType(int capacity) {
		this.capacity = capacity;
	}

}
