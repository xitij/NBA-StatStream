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
	
	public float getTrueShootingPercent() {
		return TrueShootingPercent;
	}
	
	public void setTrueShootingPercent(float TrueShootingPercent) {
		this.TrueShootingPercent = TrueShootingPercent;
	}
	
	public float getEFGPercent() {
		return EFGPercent;
	}
	
	public void setEFGPercent(float EFGPercent) {
		this.EFGPercent = EFGPercent;
	}
	
	public float getORebPercent() {
		return ORebPercent;
	}
	
	public void setORebPercent(float ORebPercent) {
		this.ORebPercent = ORebPercent;
	}
	
	public float getDRebPercent() {
		return DRebPercent;
	}
	
	public void setDRebPercent(float DRebPercent) {
		this.DRebPercent = DRebPercent;
	}
	public float getTotRebPercent() {
		return TotRebPercent;
	}
	
	public void setTotRebPercent(float TotRebPercent) {
		this.TotRebPercent = TotRebPercent;
	}
	
	public float getAstPercent() {
		return AssistPercent;
	}
	
	public void setAstPercent(float AstPercent) {
		this.AssistPercent = AstPercent;
	}
	
	public float geStealPercent() {
		return StealPercent;
	}
	
	public void setStealPercent(float StealPercent) {
		this.StealPercent = StealPercent;
	}
	
	public float getBlockPercent() {
		return BlockPercent;
	}
	
	public void setBlockPercent(float BlockPercent) {
		this.BlockPercent = BlockPercent;
	}
	
	public float getTOPercent() {
		return TOPercent;
	}
	
	public void setTOPercent(float TOPercent) {
		this.TOPercent = TOPercent;
	}
	
	public float getUsage() {
		return Usage;
	}
	
	public void setUsage(float Usage) {
		this.Usage = Usage;
	}
	
	public float getOffRating() {
		return OffRating;
	}
	
	public void setOffRating(float OffRating) {
		this.OffRating = OffRating;
	}
	
	public float getDefRating() {
		return DefRating;
	}
	
	public void setDefRating(float DefRating) {
		this.DefRating = DefRating;
	}

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