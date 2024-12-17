import java.util.*;

public class RiderTester {

    public static void setupDrivers(CabManager cabManager) {
        Car car1 = new Car("Maruti", 123);
        Car car2 = new Car("Audi", 456);
        Car car3 = new Car("Toyota", 789);

        Driver driver1 = new Driver();
        Driver driver2 = new Driver();
        Driver driver3 = new Driver();

        driver1.registerDriverWithCar("Driver1", 123123, car1);
        driver2.registerDriverWithCar("Driver2", 456456, car2);
        driver3.registerDriverWithCar("Driver3", 789789, car3);

        cabManager.registerDriver(driver1);
        cabManager.registerDriver(driver2);
        cabManager.registerDriver(driver3);

        cabManager.addOrUpdateCabLocation(driver1.getID(), new Location(10, 10));
        cabManager.addOrUpdateCabLocation(driver2.getID(), new Location(12, 12));
        cabManager.addOrUpdateCabLocation(driver3.getID(), new Location(15, 15));

        cabManager.updateCabStatus(driver1.getID(), "free");
        cabManager.updateCabStatus(driver2.getID(), "free");
        cabManager.updateCabStatus(driver3.getID(), "free");

        System.out.println("Drivers initialized.\n");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Managers
        CabManager cabManager = new CabManager(new DefaultAssignmentStrategy());
        RiderManager riderManager = new RiderManager();
        BookingManager bookingManager = new BookingManager(cabManager, new BasicPriceCalculator());

        // Setup drivers
        setupDrivers(cabManager);

        Map<String, Rider> riders = new HashMap<>(); // Store all riders
        Map<String, RideDetails> activeRides = new HashMap<>();

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Add Rider and Book Ride");
            System.out.println("2. End a Ride");
            System.out.println("3. View Ride History");
            System.out.println("4. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: // Add Rider and Book Ride
                    System.out.print("Enter Rider Name: ");
                    String riderName = scanner.nextLine();

                    if (!riders.containsKey(riderName)) {
                        Rider newRider = new Rider(riderName);
                        riderManager.setRiderDetailsForRide(newRider);
                        System.out.println("Rider " + riderName + " added successfully.");
                        riders.put(riderName, newRider);
                    }

                    Rider rider = riders.get(riderName);

                    System.out.print("Enter Rider's Current X Location: ");
                    int x = scanner.nextInt();
                    System.out.print("Enter Rider's Current Y Location: ");
                    int y = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    riderManager.setOrUpdateRiderLocation(rider.getID(), new Location(x, y));

                    Driver selectedDriver = cabManager.getAvailableCabs(riderManager.getRiderLocation(rider.getID()));
                    if (selectedDriver != null) {
                        System.out.println("Selected Driver: " + selectedDriver.getName());
                        System.out.print("Enter Destination X Location: ");
                        int destX = scanner.nextInt();
                        System.out.print("Enter Destination Y Location: ");
                        int destY = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        RideDetails ride = bookingManager.bookRide(rider, selectedDriver,
                                riderManager.getRiderLocation(rider.getID()), new Location(destX, destY));

                        activeRides.put(rider.getName(), ride);
                        System.out.println("Ride booked successfully for Rider: " + rider.getName());
                    } else {
                        System.out.println("No drivers available for Rider: " + rider.getName());
                    }
                    break;

                case 2: // End a Ride
                    System.out.print("Enter Rider Name to End the Ride: ");
                    String endRiderName = scanner.nextLine();

                    if (activeRides.containsKey(endRiderName)) {
                        RideDetails rideToEnd = activeRides.get(endRiderName);
                        bookingManager.endRide(rideToEnd, riderManager);
                        activeRides.remove(endRiderName);
                        System.out.println("Ride ended for Rider: " + endRiderName);
                    } else {
                        System.out.println("No active ride found for Rider: " + endRiderName);
                    }
                    break;

                case 3: // View Ride History
                    System.out.print("Enter Rider Name to View Ride History: ");
                    String historyRiderName = scanner.nextLine();

                    if (riders.containsKey(historyRiderName)) {
                        Rider historyRider = riders.get(historyRiderName);
                        List<RideDetails> history = riderManager.getRiderHistory().get(historyRider.getID());

                        System.out.println("Ride History for Rider: " + historyRiderName);
                        if (history != null && !history.isEmpty()) {
                            for (RideDetails rd : history) {
                                System.out.println(rd);
                            }
                        } else {
                            System.out.println("No ride history found for Rider: " + historyRiderName);
                        }
                    } else {
                        System.out.println("Rider not found.");
                    }
                    break;

                case 4: // Exit
                    System.out.println("Exiting program. Goodbye!");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
