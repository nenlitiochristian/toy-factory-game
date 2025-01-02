package main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

class Game {
	Game() {
		Scanner scan = new Scanner(System.in);
		boolean gameOver = false;
		while (!gameOver) {
			clearScreen();
			System.out.println("Toy Factory Manager");
			System.out.println("1. New Game");
			System.out.println("2. Load Game");
			System.out.println("3. Highscore");
			System.out.println("4. Exit");
			System.out.print(">> ");

			int userChoice = -1;
			try {
				userChoice = Integer.parseInt(scan.next());
			} catch (NumberFormatException e) {
				continue;
			}

			PlayerData currentPlayer = null;
			switch (userChoice) {
			case 1:
				currentPlayer = makeNewPlayer(scan);
				if (currentPlayer == null)
					break;
				startGame(scan, currentPlayer);
				break;
			case 2:
				currentPlayer = loadExistingPlayer(scan);
				if (currentPlayer == null)
					break;
				startGame(scan, currentPlayer);
				break;
			case 3:
				showLeaderboard(scan);
				break;
			case 4:
				gameOver = true;
				break;
			}
		}
		scan.close();
	}

	public static void startGame(Scanner scan, PlayerData playerData) {
		int userChoice = -1;
		boolean continueGame = true;
		do {
			clearScreen();
			savePlayerDataToFile(playerData);

			if (playerData.getCurrentOrderData().getCountdown() <= 0) {
				showPlayerData(playerData, "Order");
				System.out.println("You ran out of time!");
				System.out.println("Ending game...");
				endGame(playerData, scan);
				System.out.println("Press enter to go back...");
				try {
					System.in.read();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}

			showPlayerData(playerData, "Order", "Player");
			System.out.println("1. Show Toy List");
			System.out.println("2. Produce Toys");
			System.out.println("3. Manage Workers");
			System.out.println("4. Exit Game (Your progress is always saved automatically)");
			System.out.println("5. End Game");
			System.out.print(">> ");

			try {
				userChoice = Integer.parseInt(scan.next());
			} catch (NumberFormatException e) {
				userChoice = -1;
				continue;
			}
			switch (userChoice) {
			case 1:
				showToyList(playerData);
				break;
			case 2:
				produceToys(playerData);
				break;
			case 3:
				manageWorkers(playerData, scan);
				break;
			case 4:
				continueGame = false;
				break;

			case 5:
				clearScreen();
				String yesOrNo = "";
				boolean keepLooping = true;
				while (keepLooping) {
					System.out.print(
							"Are you sure you want to end your game? (you cannot load this save after this) [Y/N]: ");
					yesOrNo = scan.next();
					if (yesOrNo.length() == 1) {
						switch (yesOrNo.charAt(0)) {
						case 'Y':
						case 'y':
							endGame(playerData, scan);
							keepLooping = false;
							continueGame = false;
							break;
						case 'N':
						case 'n':
							keepLooping = false;
						}
					}
				}
				break;

			default:
				break;
			}
		} while (continueGame);
	}

	private static void showToyList(PlayerData playerData) {
		clearScreen();
		showPlayerData(playerData, "Order", "ToyList");
		System.out.println("Press enter to go back...");
		try {
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void produceToys(PlayerData playerData) {
		int workhours = playerData.decideWorkhours();
		for (String progressBar = "#"; progressBar.length() < "##########".length(); progressBar += "#") {
			clearScreen();
			System.out.println("Producing toys...");
			System.out.printf("[%-10s] %d%%\n", progressBar, progressBar.length() * 10);

			try {
				TimeUnit.MILLISECONDS.sleep(workhours * 100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		clearScreen();
		showPlayerData(playerData, "Order");
		System.out.println(
				"Your factory has produced " + playerData.makeToys(workhours) + " toys in " + workhours + " hours");

		if (playerData.orderCanBeFinished()) {
			System.out.println("You have finished an order and received " + playerData.finishOrder() + "gold");
		}

		System.out.println("Press enter to go back...");
		try {
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void manageWorkers(PlayerData playerData, Scanner scan) {
		int userChoice = -1;
		while (true) {
			clearScreen();
			showPlayerData(playerData, "WorkerList");
			System.out.println("Money: " + playerData.getMoney());
			System.out.println("1. Hire new worker ");
			System.out.println("2. Upgrade worker");
			System.out.println("3. Go back");
			System.out.print(">> ");

			try {
				userChoice = Integer.parseInt(scan.next());
			} catch (NumberFormatException e) {
				userChoice = -1;
				continue;
			}
			switch (userChoice) {
			case 1:
				buyNewWorker(playerData, scan);
				break;
			case 2:
				upgradeWorker(playerData, scan);
				break;
			case 3:
				return;
			default:
				break;
			}
		}
	}

	private static void buyNewWorker(PlayerData playerData, Scanner scan) {
		clearScreen();
		showPlayerData(playerData, "WorkerList");
		String userChoice = "";
		boolean keepLooping = true;
		while (keepLooping) {
			System.out.print("Hire a new worker for 500 gold [Y/N]: ");
			userChoice = scan.next();
			if (userChoice.length() == 1) {
				switch (userChoice.charAt(0)) {
				case 'Y':
				case 'y':
					keepLooping = false;
					break;
				case 'N':
				case 'n':
					return;
				}
			}
		}
		if (playerData.getMoney() >= 500) {
			System.out.println("Bought a level 1 worker for 500 gold");
			playerData.buyWorker();
		} else {
			System.out.println("Not enough money");
		}
		System.out.println("Press enter to go back...");
		try {
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void upgradeWorker(PlayerData playerData, Scanner scan) {
		clearScreen();
		showPlayerData(playerData, "WorkerList");
		System.out.println(" No. | Upgrade | Price");
		int level = 1;
		int[] prices = { 600, 800, 1100, 1500 };
		for (int price : prices) {
			System.out.println("  " + level + "  | " + level + " -> " + (level + 1) + " | " + price);
			level++;
		}
		System.out.println("==================================");

		int userChoice = -1;
		while (true) {
			System.out.print("Input which upgrade you want to buy [only applies to one worker]: ");
			try {
				userChoice = Integer.parseInt(scan.next());
			} catch (NumberFormatException e) {
				userChoice = -1;
				continue;
			}
			if (userChoice >= 1 && userChoice <= 4)
				break;
		}

		WorkerLevel workerLevel;
		switch (userChoice) {
		case 1:
			workerLevel = WorkerLevel.ONE;
			break;
		case 2:
			workerLevel = WorkerLevel.TWO;
			break;
		case 3:
			workerLevel = WorkerLevel.THREE;
			break;
		case 4:
			workerLevel = WorkerLevel.FOUR;
			break;
		default:
			workerLevel = WorkerLevel.ONE;
			break;
		}

		String yesOrNo = "";
		boolean keepLooping = true;
		while (keepLooping) {
			System.out.print("Upgrade your worker for " + playerData.getWorkerList().getUpgradePrice(workerLevel)
					+ " gold [Y/N]: ");
			yesOrNo = scan.next();

			if (yesOrNo.length() == 1) {
				switch (yesOrNo.charAt(0)) {
				case 'Y':
				case 'y':
					keepLooping = false;
					break;
				case 'N':
				case 'n':
					return;
				}
			}
		}

		if (playerData.getWorkerList().getWorker(workerLevel) < 1) {
			System.out.println("You don't have a worker of that type");
		} else if (playerData.tryUpgradingWorker(workerLevel)) {
			System.out.println("Upgraded a level " + (workerLevel.ordinal() + 1) + " worker for "
					+ playerData.getWorkerList().getUpgradePrice(workerLevel) + " gold");
		} else {
			System.out.println("Not enough money");
		}

		System.out.println("Press enter to go back...");
		try {
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// return true if end game
	// false if cancel
	private static void endGame(PlayerData playerData, Scanner scan) {
		File saveFile = new File("save.txt");
		int dataCount = 0;
		List<String> usernamesData = new ArrayList<String>();
		try {
			Scanner scanFile = new Scanner(saveFile);
			while (scanFile.hasNextLine()) {
				usernamesData.add(scanFile.nextLine());
				dataCount++;
			}
			scanFile.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

		// rewrite the save file but with the player name removed
		try {
			FileWriter saveFileRewrite = new FileWriter("save.txt");
			for (int i = 0; i < dataCount; i++) {
				if (usernamesData.get(i).equals(playerData.getUsername()) == false)
					saveFileRewrite.append(usernamesData.get(i));
			}
			saveFileRewrite.close();
		} catch (IOException e) {
			System.out.println("Had trouble ending game");
			e.printStackTrace();
		}

		// append to the scoreboard
		try {
			FileWriter writer = new FileWriter("hiscore.txt", true);
			writer.append(
					playerData.getUsername() + "#" + playerData.getOrdersDone() + "#" + playerData.getMoney() + "\n");
			writer.close();
		} catch (IOException e) {
			System.out.println("Failed to save player name to file");
			e.printStackTrace();
		}

		// delete user save file
		File userFile = new File(playerData.getUsername() + ".txt");
		userFile.delete();
	}

	public static PlayerData makeNewPlayer(Scanner scan) {
		clearScreen();
		System.out.println("Toy Factory Manager");
		while (true) {
			System.out.print("Input player's name [0 to go back]: ");

			String userInput = scan.next();
			if (userInput.charAt(0) == '0' && userInput.length() == 1) {
				return null;
			} else if (userInput.length() < 3 || userInput.length() > 20) {
				System.out.println("Name must be between 3 to 20 characters");
				continue;
			} else if (playerExistsInFile(userInput)) {
				System.out.println("User with that name already exists!");
				continue;
			}
			PlayerData newPlayer = new PlayerData(userInput);
			savePlayerDataToFile(newPlayer);
			savePlayerNameToFile(userInput);
			return newPlayer;
		}
	}

	public static PlayerData loadExistingPlayer(Scanner scan) {
		clearScreen();
		System.out.println("Toy Factory Manager");

		// print all available usernames
		int playerCount = 0;
		List<String> playerNames = new ArrayList<String>();

		File namesFileRead = new File("save.txt");
		try {
			// if we can make a file then it means it's empty
			// so we try to make a file first
			if (!namesFileRead.createNewFile()) {
				Scanner fileScanner = new Scanner(namesFileRead);
				String temporary;
				while (fileScanner.hasNextLine()) {
					playerCount++;
					temporary = fileScanner.nextLine();
					playerNames.add(temporary);
					System.out.printf("%d. %s\n", playerCount, temporary);
				}
				fileScanner.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (playerCount == 0) {
			System.out.println("No player found, returning in 3 seconds...");
			try {
				TimeUnit.SECONDS.sleep(3);
				return null;
			} catch (InterruptedException e) {
				System.out.println("Sleep() failed");
				return null;
			}
		}

		int userInput;
		while (true) {
			System.out.println("Pick a save file [0 to cancel]: ");
			try {
				userInput = Integer.parseInt(scan.next());
			} catch (NumberFormatException e) {
				continue;
			}
			if (userInput == 0) {
				return null;
			} else if (userInput >= 1 && userInput <= playerCount)
				return readPlayerDataFromFile(playerNames.get(playerCount - 1));
		}
	}

	public static void showPlayerData(PlayerData playerData, String... modes) {
		System.out.println(" " + playerData.getUsername() + "'s Factory");
		System.out.println("==================================");
		for (String mode : modes) {
			switch (mode) {
			case "Order":
				System.out.println(" Current Order");
				System.out.printf(" Toy Type: %s\n", playerData.getCurrentOrderData().getToy().getToyName());
				System.out.printf(" Level: %s\n", playerData.getCurrentOrderData().getLevel());
				System.out.printf(" Quantity: %s\n", playerData.getCurrentOrderData().getToy().getToyAmount());
				System.out.printf(" Time: %s\n", playerData.getCurrentOrderData().getCountdown());
				System.out.println("==================================");
				break;

			case "Player":
				System.out.println(" " + playerData.getUsername() + "'s Data");
				System.out.printf(" Money: %s\n", playerData.getMoney());
				System.out.printf(" Orders Done: %s\n", playerData.getOrdersDone());
				System.out.println("==================================");
				break;

			case "ToyList":
				ToyList tempTL = playerData.getToyList();
				System.out.println(" Toy Name         | Price | Amount ");
				ToyType[] toyTypes = { ToyType.TEDDY_BEAR, ToyType.TOY_CAR, ToyType.TOY_PLANE, ToyType.RC_CAR,
						ToyType.TRAIN_SET, ToyType.TRANSFORM_ROBOT };
				for (ToyType toyType : toyTypes) {
					Toy toy = tempTL.getToy(toyType);
					System.out.printf(" %-16s | %-5d | %-5d \n", toy.getToyName(), toy.getToyPrice(),
							toy.getToyAmount());
				}
				System.out.println("==================================");
				break;

			case "WorkerList":
				WorkerList tempWL = playerData.getWorkerList();
				System.out.println(" " + playerData.getUsername() + "'s Worker List");
				WorkerLevel[] workerLevels = { WorkerLevel.ONE, WorkerLevel.TWO, WorkerLevel.THREE, WorkerLevel.FOUR,
						WorkerLevel.FIVE };
				for (WorkerLevel workerLevel : workerLevels) {
					System.out.printf(" Level %d Worker : %d\n", workerLevel.ordinal() + 1,
							tempWL.getWorker(workerLevel));
				}
				System.out.println("==================================");
				break;

			}
		}
	}

	public static void savePlayerNameToFile(String username) {
		try {
			FileWriter writer = new FileWriter("save.txt", true);
			writer.append(username + "\n");
			writer.close();
		} catch (IOException e) {
			System.out.println("Failed to save player name to file");
			e.printStackTrace();
		}
	}

	public static void savePlayerDataToFile(PlayerData playerData) {
		try {
			FileWriter writer = new FileWriter(playerData.getUsername() + ".txt");
			// player data
			writer.write(playerData.getUsername() + "\n" + playerData.getMoney() + "\n" + playerData.getDifficulty()
					+ "\n" + playerData.getOrdersDone() + "\n" + playerData.getCurrentExperience() + "\n");

			// player worker list data
			WorkerList temporaryWL = playerData.getWorkerList();
			writer.write(temporaryWL.getWorker(WorkerLevel.ONE) + "\n" + temporaryWL.getWorker(WorkerLevel.TWO) + "\n"
					+ temporaryWL.getWorker(WorkerLevel.THREE) + "\n" + temporaryWL.getWorker(WorkerLevel.FOUR) + "\n"
					+ temporaryWL.getWorker(WorkerLevel.FIVE) + "\n");

			// player current order data
			Order temporaryCO = playerData.getCurrentOrderData();
			writer.write(temporaryCO.getToy().getToyType() + "\n" + temporaryCO.getToy().getToyAmount() + "\n"
					+ temporaryCO.getLevel() + "\n" + temporaryCO.getCountdown() + "\n");

			// player toy list data
			ToyList temporaryTL = playerData.getToyList();
			writer.write(temporaryTL.getToy(ToyType.TEDDY_BEAR).getToyAmount() + "\n"
					+ temporaryTL.getToy(ToyType.TOY_CAR).getToyAmount() + "\n"
					+ temporaryTL.getToy(ToyType.TOY_PLANE).getToyAmount() + "\n"
					+ temporaryTL.getToy(ToyType.RC_CAR).getToyAmount() + "\n"
					+ temporaryTL.getToy(ToyType.TRAIN_SET).getToyAmount() + "\n"
					+ temporaryTL.getToy(ToyType.TRANSFORM_ROBOT).getToyAmount() + "\n");

			writer.close();
		} catch (IOException e) {
			System.out.println("Failed to save player to file");
			e.printStackTrace();
		}
	}

	public static PlayerData readPlayerDataFromFile(String filename) {
		File file = new File(filename + ".txt");
		try {
			Scanner reader = new Scanner(file);
			String username = reader.nextLine();
			int money = Integer.parseInt(reader.nextLine());
			int difficulty = Integer.parseInt(reader.nextLine());
			int ordersDone = Integer.parseInt(reader.nextLine());
			int currentExperience = Integer.parseInt(reader.nextLine());

			// read worker list data
			int[] workers = { 0, 0, 0, 0, 0 };
			for (int i = 0; i < 5; i++) {
				workers[i] = Integer.parseInt(reader.nextLine());
			}
			WorkerList workerList = new WorkerList(workers[0], workers[1], workers[2], workers[3], workers[4]);

			// read current order data
			ToyType toyType = ToyType.valueOf(reader.nextLine());
			int toyAmount = Integer.parseInt(reader.nextLine());
			Toy orderedToy = new Toy(toyType, toyAmount);
			int level = Integer.parseInt(reader.nextLine());
			int countdown = Integer.parseInt(reader.nextLine());
			Order currentOrderData = new Order(orderedToy, level, countdown);

			// read toy list data
			int teddyBearCount = Integer.parseInt(reader.nextLine());
			int toyCarCount = Integer.parseInt(reader.nextLine());
			int toyPlaneCount = Integer.parseInt(reader.nextLine());
			int rcCarCount = Integer.parseInt(reader.nextLine());
			int trainSetCount = Integer.parseInt(reader.nextLine());
			int transformRobotCount = Integer.parseInt(reader.nextLine());
			ToyList toyList = new ToyList();
			toyList.addToy(ToyType.TEDDY_BEAR, teddyBearCount).addToy(ToyType.TOY_CAR, toyCarCount)
					.addToy(ToyType.TOY_PLANE, toyPlaneCount).addToy(ToyType.RC_CAR, rcCarCount)
					.addToy(ToyType.TRAIN_SET, trainSetCount).addToy(ToyType.TRANSFORM_ROBOT, transformRobotCount);

			reader.close();
			return new PlayerData(username, money, difficulty, ordersDone, currentExperience, workerList, toyList,
					currentOrderData);
		} catch (FileNotFoundException e) {
			System.out.println("Problem with reading save file");
			e.printStackTrace();
		}
		return new PlayerData(filename);
	}

	public static boolean playerExistsInFile(String username) {
		File save = new File("save.txt");
		File hiscore = new File("hiscore.txt");
		try {
			save.createNewFile();
			hiscore.createNewFile();
		} catch (IOException e) {
			System.out.println("An error occured in reading file");
			e.printStackTrace();
		}

		boolean usernameFound = false;
		// check in save.txt
		try {
			String temporary;
			Scanner fileScanner = new Scanner(save);
			while (fileScanner.hasNextLine()) {
				temporary = fileScanner.nextLine();
				if (temporary.equals(username)) {
					usernameFound = true;
					break;
				}
			}
			fileScanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}

		// check in hiscore.txt
		try {
			String temporary;
			Scanner fileScanner = new Scanner(hiscore);
			while (fileScanner.hasNextLine()) {
				temporary = fileScanner.nextLine();
				String temporaryUsername = temporary.substring(0, temporary.indexOf('#'));
				if (temporaryUsername.equals(username)) {
					usernameFound = true;
					break;
				}
			}
			fileScanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}

		return usernameFound;
	}

	public static void showLeaderboard(Scanner scan) {
		File scoreboardFile = new File("hiscore.txt");
		try {
			scoreboardFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<ScoreboardData> scoreboardDatas = new ArrayList<ScoreboardData>();
		int dataCount = 0;
		try (Scanner fileScan = new Scanner(scoreboardFile)) {
			while (fileScan.hasNextLine()) {
				int tempOrdersDone;
				int tempMoney;
				String tempName;
				String tempString;
				tempString = fileScan.nextLine();
				StringTokenizer stringTokenizer = new StringTokenizer(tempString);
				tempName = stringTokenizer.nextToken("#");
				tempOrdersDone = Integer.parseInt(stringTokenizer.nextToken("#"));
				tempMoney = Integer.parseInt(stringTokenizer.nextToken("#"));
				ScoreboardData tempSD = new ScoreboardData(tempName, tempOrdersDone, tempMoney);
				scoreboardDatas.add(tempSD);
				dataCount++;
			}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}

		System.out.println("Toy Factory Manager Leaderboard");
		System.out.println("==================================================");
		if (dataCount <= 0) {
			System.out.println("No data found....");
			System.out.println("Press enter to go back...");
			try {
				System.in.read();
			} catch (Exception e) {
				return;
			}
			return;
		}

		// bubble sort
		for (int i = 0; i < dataCount - 1; i++) {
			for (int j = 0; j < dataCount - 1 - i; j++) {
				if (scoreboardDatas.get(j).ordersDone < scoreboardDatas.get(j + 1).ordersDone) {
					ScoreboardData temp;
					temp = scoreboardDatas.get(j);
					scoreboardDatas.set(j, scoreboardDatas.get(j + 1));
					scoreboardDatas.set(j + 1, temp);
				}
			}
		}

		// print the sorted data
		System.out.printf(" %-3s | %-20s | %-11s | %-5s\n", "No.", "Username", "Orders Done", "Money");
		for (int i = 0; i < dataCount; i++) {
			System.out.printf(" %-3s | %-20s | %-11s | %-5s\n", "No.", scoreboardDatas.get(i).username,
					scoreboardDatas.get(i).ordersDone, scoreboardDatas.get(i).money);
		}
		System.out.println("==================================================");
		System.out.println("Press enter to go back...");
		try {
			System.in.read();
		} catch (Exception e) {
			return;
		}
		return;
	}

	public class RandomNumber {
		private final static Random rand = new Random();

		public static int generate(int low, int high) {
			if (low > high) {
				throw new IllegalArgumentException("Invalid range: low can't be higher than high");
			}
			int range = high - low + 1;
			return rand.nextInt(range) + low;
		}
	}

	// with help from
	// https://stackoverflow.com/questions/2979383/how-to-clear-the-console/33379766#33379766
	// also with some help from my friend
	public static void clearScreen() {
		// as a backup if everything fails
		// in this case just give some space to separate menus
		System.out.printf("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

		// if ANSI is supported use ANSI
		if (System.console() != null && System.getenv().get("TERM") != null) {
			System.out.print("\033[H\033[2J");
			System.out.flush();
		}
		// if ANSI is not supported try using cmd /c cls
		else {
			try {
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
