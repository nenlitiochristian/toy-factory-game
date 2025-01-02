package main;

class WorkerList {
	private int[] workerAmounts = new int[WorkerLevel.values().length];

	public WorkerList() {
		this.addNewWorker().addNewWorker().addNewWorker().addNewWorker().addNewWorker();
	}

	// use this only when loading an existing player
	public WorkerList(int levelOneWorkerAmount, int levelTwoWorkerAmount, int levelThreeWorkerAmount,
			int levelFourWorkerAmount, int levelFiveWorkerAmount) {
		workerAmounts[WorkerLevel.ONE.ordinal()] = levelOneWorkerAmount;
		workerAmounts[WorkerLevel.TWO.ordinal()] = levelTwoWorkerAmount;
		workerAmounts[WorkerLevel.THREE.ordinal()] = levelThreeWorkerAmount;
		workerAmounts[WorkerLevel.FOUR.ordinal()] = levelFourWorkerAmount;
		workerAmounts[WorkerLevel.FIVE.ordinal()] = levelFiveWorkerAmount;
	}

	public WorkerList addNewWorker() {
		workerAmounts[WorkerLevel.ONE.ordinal()]++;
		return this;
	}

	public int getUpgradePrice(WorkerLevel workerLevel) {
		switch (workerLevel) {
		case ONE:
			return 600;
		case TWO:
			return 800;
		case THREE:
			return 1100;
		case FOUR:
			return 1500;
		default:
			return 999999;
		}

	}

	public WorkerList upgradeWorkers(WorkerLevel level) {
		if (level == WorkerLevel.FIVE) {
			return this;
		}

		int ordinal = level.ordinal();
		workerAmounts[ordinal]--;
		workerAmounts[ordinal + 1]++;
		return this;
	}

	public int getWorker(WorkerLevel level) {
		return workerAmounts[level.ordinal()];
	}

	public int getTotalWorkers() {
		int total = 0;
		for (int amount : workerAmounts) {
			total += amount;
		}
		return total;
	}

	public int getTotalWorkersWeighted() {
		int total = 0;
		int level = 1;
		for (int amount : workerAmounts) {
			total += amount * level;
			level++;
		}
		return total;
	}
}