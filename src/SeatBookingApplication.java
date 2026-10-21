
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SeatBookingApplication {

    private static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) {

        // Validate event details
        String eventName = readRequiredText("Enter event name: ");

        int totalSeats = readOptionalPositiveInt("Enter total number of seats (press Enter for default 10): ", 10);

        Event event = new Event(eventName, totalSeats);
        BookingService bookingService = new BookingService(event);

        StatusMonitor monitor = new StatusMonitor(bookingService, 5000);
        Thread monitorThread = new Thread(monitor, "Monitor");
        monitorThread.start();

        ExecutorService executor = Executors.newFixedThreadPool(3);

        boolean isRunning = true;
        try {
            while (isRunning) {
                System.out.println(
                        "\n==================== EVENT BOOKING MENU ===================="
                                + "\n1. Booking Single seat."
                                + "\n2. Submit concurrent bookings."
                                + "\n3. View available seats."
                                + "\n4. View confirmed bookings."
                                + "\n5. Summary"
                                + "\n6. Exit"
                                + "\n============================================================"
                );

                int choice = readInt("Enter your option: ");
                switch (choice) {
                    // Case 1: Single booking
                    case 1 -> {
                        String customerName = readRequiredText("Enter your name: ");
                        int seatNumber = readInt("Enter seat number (1-" + totalSeats + "): ");

                        Future<?> future = executor.submit(new BookingTask(bookingService, customerName, seatNumber));

                        // Wait for this booking before showing the menu again
                        future.get();
                    }

                    // Case 2: Multiple concurrent bookings
//                    case 2 -> {
//                        int count = readInt("Number of booking requests: ");
//
//                        List<Future<?>> futures = new ArrayList<>();
//                        List<String> customerNames = new ArrayList<>();
//
//                        // Submit all requests before waiting for results
//                        for (int i = 1; i <= count; i++) {
//                            System.out.println("\nRequest " + i);
//
//                            String customerName =
//                                    readRequiredText("Enter your name: ");
//
//                            int seatNumber = readInt("Enter seat number (1-" + totalSeats + "): ");
//                            customerNames.add(customerName);
//                            futures.add(executor.submit(new BookingTask(bookingService, customerName, seatNumber)));
//                            futures.get(i-1).get();
//                        }
//
//                        System.out.println(
//                                "\nAll requests submitted. Results:"
//                        );

                        // Collect results only after every task is submitted
//                        for (int i = 0; i < futures.size(); i++) {
//                            try {
//                                Object result = futures.get(i).get();
//
//                                System.out.println(
//                                        "Booking successful: " + result
//                                );
//                            } catch (ExecutionException e) {
//                                System.out.println("Booking failed for "+futures.get(i).get()+e.getCause());
//                            }
//                        }
//                    }

                    // Case 2: Multiple concurrent bookings
                    case 2 -> {
                        System.out.print("Enter customer name: ");
                        String name1 = input.nextLine();

                        System.out.print("Enter the seat number: ");
                        int seatNumber1 = input.nextInt();
                        input.nextLine();
                        System.out.print("Enter customer name: ");
                        String name2 = input.nextLine();

                        System.out.print("Enter the seat number: ");
                        int seatNumber2 = input.nextInt();
                        input.nextLine();

                        Future<?> future1 = executor.submit(new BookingTask(bookingService, name1, seatNumber1));
                        Future<?> future2 = executor.submit(new BookingTask(bookingService, name2, seatNumber2));
                        // Wait for both tasks to finish.
                        future1.get();
                        future2.get();
                    }

                    // Case 3: Available seats
                    case 3 -> System.out.println(
                            "Available seats: "
                                    + bookingService.getAvailableSeats()
                    );

                    // Case 4: Confirmed bookings
                    case 4 -> {
                        List<Booking> bookings =
                                bookingService.getBookings();

                        if (bookings.isEmpty()) {
                            System.out.println("No confirmed bookings.");
                        } else {
                            System.out.println("Confirmed bookings:");

                            for (Booking booking : bookings) {
                                System.out.println(booking);
                            }
                        }
                    }

                    // Case 5: Summary
                    case 5 -> bookingService.displaySummary();

                    // Case 6: Exit
                    case 6 -> {
                        isRunning = false;
                        System.out.println("Exiting application...");
                    }

                    default -> System.out.println(
                            "Invalid menu choice. Please try again."
                    );
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Application interrupted.");

        } catch (java.util.concurrent.ExecutionException e){
            System.out.println("Booking failed for "+e.getCause());
        }finally {
            // Stop accepting new tasks and finish queued tasks
            executor.shutdown();

            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }

            // Stop monitor and wait for its thread to finish
            monitor.stopMonitoring();
            monitorThread.interrupt();

            try {
                monitorThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println("\nFinal event summary:");
            bookingService.displaySummary();

            input.close();
        }
    }

    // Read text that must not be empty
    private static String readRequiredText(String message) {
        while (true) {
            System.out.print(message);
            String value = input.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    // Read an integer within a specified range
    private static int readInt(String message) {

        while (true) {
            System.out.print(message);
            String value = input.nextLine().trim();

            try {
                int number = Integer.parseInt(value);
                return number;

            } catch (NumberFormatException e) {
                System.out.println(
                        "Invalid input. Please enter a whole number."
                );
            }
        }
    }

    // Read positive seat count, or use the default when Enter is pressed
    private static int readOptionalPositiveInt(
            String message, int defaultValue) {

        while (true) {
            System.out.print(message);
            String value = input.nextLine().trim();

            if (value.isEmpty()) {
                return defaultValue;
            }
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a positive whole number.");
            }
        }
    }

    // Print the reason a submitted booking failed
    private static void printBookingError(
            String customerName, ExecutionException e) {

        Throwable cause = e.getCause();

        String message = cause != null
                ? cause.getMessage()
                : e.getMessage();

        System.out.println(
                "Booking failed for " + customerName + ": " + message
        );
    }
}
