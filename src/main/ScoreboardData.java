package main;

class ScoreboardData {
	public final String username;
	public final int ordersDone;
	public final int money;

	ScoreboardData(String username, int ordersDone, int money) {
		this.username = username;
		this.ordersDone = ordersDone;
		this.money = money;
	}
}