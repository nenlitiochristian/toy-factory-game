package main;

class ToyList {
	private Toy teddyBear = new Toy(ToyType.TEDDY_BEAR, 0);
	private Toy toyCar = new Toy(ToyType.TOY_CAR, 0);
	private Toy toyPlane = new Toy(ToyType.TOY_PLANE, 0);
	private Toy rcCar = new Toy(ToyType.RC_CAR, 0);
	private Toy trainSet = new Toy(ToyType.TRAIN_SET, 0);
	private Toy transformRobot = new Toy(ToyType.TRANSFORM_ROBOT, 0);

	public ToyList() {
	}

	public Toy getToy(ToyType toyType) {
		switch (toyType) {
		case TEDDY_BEAR:
			return teddyBear;
		case TOY_CAR:
			return toyCar;
		case TOY_PLANE:
			return toyPlane;
		case RC_CAR:
			return rcCar;
		case TRAIN_SET:
			return trainSet;
		case TRANSFORM_ROBOT:
			return transformRobot;
		default:
			throw new IllegalArgumentException("Invalid toy type");
		}

	}

	public ToyList addToy(ToyType toyType, int amount) {
		switch (toyType) {
		case TEDDY_BEAR:
			teddyBear.addToy(amount);
			break;
		case TOY_CAR:
			toyCar.addToy(amount);
			break;
		case TOY_PLANE:
			toyPlane.addToy(amount);
			break;
		case RC_CAR:
			rcCar.addToy(amount);
			break;
		case TRAIN_SET:
			trainSet.addToy(amount);
			break;
		case TRANSFORM_ROBOT:
			transformRobot.addToy(amount);
			break;
		default:
			throw new IllegalArgumentException("Invalid toy type");
		}
		return this;
	}

	public int sellToy(ToyType toyType, int amount) {
		Toy toy;
		switch (toyType) {
		case TEDDY_BEAR:
			toy = teddyBear;
			break;
		case TOY_CAR:
			toy = toyCar;
			break;
		case TOY_PLANE:
			toy = toyPlane;
			break;
		case RC_CAR:
			toy = rcCar;
			break;
		case TRAIN_SET:
			toy = trainSet;
			break;
		case TRANSFORM_ROBOT:
			toy = transformRobot;
			break;
		default:
			throw new IllegalArgumentException("Invalid toy type");
		}
		return toy.sellToy(amount);
	}
}