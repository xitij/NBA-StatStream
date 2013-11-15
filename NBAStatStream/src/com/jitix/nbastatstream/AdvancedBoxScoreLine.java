package com.jitix.nbastatstream;

/*
 * AdvancedBoxScoreLine:
 * 	Class to hold the advanced box score stats that are
 * 	calculated by the BasketballGame.
 */
class AdvancedBoxScoreLine {
	public float TrueShootingPercent;
	public float EFGPercent;
	public float ORebPercent;
	public float DRebPercent;
	public float TotRebPercent;
	public float AssistPercent;
	public float StealPercent;
	public float BlockPercent;
	public float TOPercent;
	public float Usage;
	public float OffRating;
	public float DefRating;

	public void setEmpty() {
		TrueShootingPercent = 0.0f;
		EFGPercent = 0.0f;
		ORebPercent = 0.0f;
		DRebPercent = 0.0f;
		TotRebPercent = 0.0f;
		AssistPercent = 0.0f;
		StealPercent = 0.0f;
		BlockPercent = 0.0f;
		TOPercent = 0.0f;
		Usage = 0.0f;
		OffRating = 0.0f;
		DefRating = 0.0f;
	}
}