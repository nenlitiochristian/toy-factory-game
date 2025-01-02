package main;

class Toy {
	private final ToyType toyType;
	private final int toyPrice;
	private int toyAmount = 0;

	public Toy(ToyType toyType, int toyAmount) {
		this.toyType = toyType;
		switch (toyType) {
		case TEDDY_BEAR:
			toyPrice = 2;
			break;
		case TOY_CAR:
			toyPrice = 5;
			break;
		case TOY_PLANE:
			toyPrice = 7;
			break;
		case RC_CAR:
			toyPrice = 15;
			break;
		case TRAIN_SET:
			toyPrice = 25;
			break;
		case TRANSFORM_ROBOT:
			toyPrice = 55;
			break;
		default:
			throw new IllegalArgumentException("Invalid toy type");
		}
		this.addToy(toyAmount);
	}

	public ToyType getToyType() {
		return toyType;
	}

	public String getToyName() {
		switch (toyType) {
		case RC_CAR:
			return "RC Car";
		case TEDDY_BEAR:
			return "Teddy Bear";
		case TOY_CAR:
			return "Toy Car";
		case TOY_PLANE:
			return "Toy Plane";
		case TRAIN_SET:
			return "Train Set";
		case TRANSFORM_ROBOT:
			return "Transform Robot";
		}
		return "Error";
	}

	public int getToyPrice() {
		return toyPrice;
	}

	public int getToyAmount() {
		return toyAmount;
	}

	public Toy addToy(int amount) {
		toyAmount += amount;
		return this;
	}

	public int sellToy(int toyAmount) {
		if (this.toyAmount < toyAmount)
			throw new IllegalArgumentException("Not enough toys to sell");
		if (toyAmount < 0)
			throw new IllegalArgumentException("Amount can not be negative");
		this.toyAmount -= toyAmount;
		return toyAmount * toyPrice;
	}

}