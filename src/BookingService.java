import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class BookingService {
    private final Event event;
    private final List<Booking> bookings = new ArrayList<>();
    private final List<Integer> availableSeats = new ArrayList<>();


    public BookingService(Event event) {
        this.event = event;

        // Initialize availableSeats with the numbers 1 through event.getTotalSeats() in the constructor.
        for (int seat = 1; seat <= event.getTotalSeats(); seat++) {
            availableSeats.add(seat);
        }
    }

    public synchronized Booking bookingSeat(String customerName, int seatNumber)
            throws InvalidBookingException{
        validateSeat(seatNumber);
        if(!availableSeats.contains(seatNumber)){
            throw new InvalidBookingException("Seat "+seatNumber+" is already booked.");
        }
        availableSeats.remove(Integer.valueOf(seatNumber));

        Booking booking = new Booking(customerName,seatNumber);
        bookings.add(booking);
        return booking;
    }

    // returning available seats
    public synchronized List<Integer> getAvailableSeats(){
        return new ArrayList<>(availableSeats);
    }

    // returning or displaying the successfull booking records
    public synchronized List<Booking> getBookings(){
        return new ArrayList<>(bookings);
    }

    // displaying the summery
    public synchronized void displaySummary() {
        System.out.println("\n========== EVENT SUMMARY ==========");
        System.out.println("Event: " + event.getEventName());
        System.out.println("Total seats: " + event.getTotalSeats());
        System.out.println("Available seats: " + availableSeats);
        System.out.println("Successful bookings:");

        if (bookings.isEmpty()) {
            System.out.println("No bookings yet.");
        } else {
            for (Booking booking : bookings) {
                System.out.println(booking);
            }
        }

        System.out.println("===================================");
    }

    // Throw InvalidBookingException when the seat number is outside the event range.
    private void validateSeat(int seatNumber) throws InvalidBookingException {
        if(seatNumber <1 || seatNumber > event.getTotalSeats()){
            throw new InvalidBookingException("Invalid seat number: " + seatNumber
                    + ". Valid seats are 1 to "
                    + event.getTotalSeats());
        }
    }
}
