public class BookingTask implements Runnable {
    private final BookingService bookingService;
    private final String customerName;
    private final int seatNumber;

    public BookingTask(BookingService bookingService, String customerName, int seatNumber) {
        this.bookingService = bookingService;
        this.customerName = customerName;
        this.seatNumber = seatNumber;
    }

    @Override
    public void run(){
        try{
            Booking booking = bookingService.bookingSeat(customerName,seatNumber);
            System.out.println("============================================================\n" +
                    "Booking successful for "+customerName+".");
        }catch (InvalidBookingException exception){
            System.out.println("Booking failed for "+customerName+": "+exception.getMessage());
        }
        finally {
            System.out.println("Attempt ended!\n" +
                    "============================================================");
        }


    }
}
