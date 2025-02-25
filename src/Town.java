/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean dugGoldAlready;
    private TreasureHunter th;

    public TreasureHunter treasureHunter = new TreasureHunter();
    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness, TreasureHunter th) {
        this.shop = shop;
        this.terrain = getNewTerrain();
        this.th = th;

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + item;
                dugGoldAlready = false;
            }
            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        printMessage = shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.33;
        } else {
            noTroubleChance = 0.66;
        }
        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            if (!hunter.hasItemInKit("sword")) {
                int goldDiff = (int) (Math.random() * 10) + 1;
                printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n";
                if (Math.random() > noTroubleChance) {
                    printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                    printMessage += Colors.YELLOW + "\nYou won the brawl and receive " + goldDiff + " gold." + Colors.RESET;
                    hunter.changeGold(goldDiff);
                } else {
                    printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                    printMessage += "\nYou lost the brawl and pay " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET;
                    hunter.changeGold(-goldDiff);
                }
            } else {
                int goldDiff = (int) (Math.random() * 10) + 1;
                printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n";
                printMessage += "The brawler, seeing your sword, realizes he picked a losing fight and gives you his gold." + Colors.RESET;
                printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + Colors.RESET + " gold.";
                hunter.changeGold(goldDiff);
            }
        }
    }

    public void digGold() {
        if (!hunter.hasItemInKit("shovel")) {
            System.out.println("You can't dig for gold without a shovel");
        } else if (dugGoldAlready) {
            System.out.println("You already dug for gold in this town.");
        } else {
            double chance = Math.random();
            if (chance < .5) {
                int goldFound = (int) (Math.random() * 20) + 1;
                System.out.println("You dug up " + goldFound + " gold");
                hunter.changeGold(goldFound);
            } else {
                System.out.println("You dug but only found dirt");
            }
            dugGoldAlready = true;
        }
    }

    public String infoString() {
        return "This nice little town is surrounded by " + Colors.CYAN + terrain.getTerrainName() + Colors.RESET + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < 0.16) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < 0.33) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < 0.5) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < 0.66) {
            return new Terrain("Desert", "Water");
        } else if (rnd < 0.83) {
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boot");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        if (!th.getEasyMode()) {
            double rand = Math.random();
            return (rand < 0.99);
        } else {
            return false;
        }
    }
}