package main;

import main.Game.RandomNumber;

class PlayerData {
	private final String username;
	private int money;
	private int difficulty;
	private int ordersDone;
	private int currentExperience;

	private Order currentOrder;
	private ToyList myToys;
	private WorkerList myWorkers;

	public PlayerData(String username) {
		this.username = username;
		money = 0;
		difficulty = 1;
		ordersDone = 0;

		myWorkers = new WorkerList();
		myToys = new ToyList();
		currentOrder = new Order(myWorkers.getTotalWorkers(), difficulty);
	}

	// use this only when loading an existing player
	public PlayerData(String username, int money, int difficulty, int ordersDone, int currentExperience,
			WorkerList myWorkers, ToyList myToys, Order currentOrder) {
		this.username = username;
		this.money = money;
		this.difficulty = difficulty;
		this.ordersDone = ordersDone;
		this.currentExperience = currentExperience;

		this.myWorkers = myWorkers;
		this.myToys = myToys;
		this.currentOrder = currentOrder;
	}

	public int decideWorkhours() {
		return RandomNumber.generate(7, 15);
	}

	public int makeToys(int workhours) {
		ToyType currentType = currentOrder.getToy().getToyType();
		int toysMade = (int) myWorkers.getTotalWorkersWeighted() * workhours / currentOrder.getLevel();
		myToys.getToy(currentType).addToy(toysMade);
		return toysMade;
	}

	public boolean orderCanBeFinished() {
		ToyType currentType = currentOrder.getToy().getToyType();
		if (myToys.getToy(currentType).getToyAmount() >= currentOrder.getToy().getToyAmount())
			return true;

		return false;
	}

	public int finishOrder() {
		ToyType currentType = currentOrder.getToy().getToyType();
		int amountSold = currentOrder.getToy().getToyAmount();
		int earnedMoney = myToys.getToy(currentType).sellToy(amountSold);
		currentOrder = new Order(myWorkers.getTotalWorkers(), difficulty);
		ordersDone++;
		currentExperience++;
		checkLevelUp();
		money += earnedMoney;
		return earnedMoney;
	}

	private int calculateLevelUpTarget(int x) {
		if (x == 1 || x == 2)
			return 3;
		return calculateLevelUpTarget(x - 1) + calculateLevelUpTarget(x - 2)
				+ (int) (0.5 * calculateLevelUpTarget(x - 1));
	}

	public void checkLevelUp() {
		if (currentExperience >= calculateLevelUpTarget(difficulty)) {
			currentExperience -= calculateLevelUpTarget(difficulty);
			difficulty++;
		}
	}

	public boolean tryUpgradingWorker(WorkerLevel workerLevel) {
		if (workerLevel == WorkerLevel.FIVE)
			return false;
		if (money < myWorkers.getUpgradePrice(workerLevel))
			return false;
		spendMoney(myWorkers.getUpgradePrice(workerLevel));
		switch (workerLevel) {
		case ONE:
			myWorkers.upgradeWorkers(WorkerLevel.ONE);
			break;
		case TWO:
			myWorkers.upgradeWorkers(WorkerLevel.TWO);
			break;
		case THREE:
			myWorkers.upgradeWorkers(WorkerLevel.THREE);
			break;
		case FOUR:
			myWorkers.upgradeWorkers(WorkerLevel.FOUR);
			break;
		default:
			break;
		}
		return true;
	}

	public String getUsername() {
		return username;
	}

	public int getMoney() {
		return money;
	}

	public int getCurrentExperience() {
		return currentExperience;
	}

	public int getOrdersDone() {
		return ordersDone;
	}

	public Order getCurrentOrderData() {
		return currentOrder;
	}

	public ToyList getToyList() {
		return myToys;
	}

	public WorkerList getWorkerList() {
		return myWorkers;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public PlayerData buyWorker() {
		money -= 500;
		myWorkers.addNewWorker();
		return this;
	}

	public PlayerData earnMoney(int amount) {
		money += amount;
		return this;
	}

	public PlayerData spendMoney(int amount) {
		money -= amount;
		return this;
	}
}