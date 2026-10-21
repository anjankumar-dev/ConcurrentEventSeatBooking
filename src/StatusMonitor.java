public class StatusMonitor implements Runnable{
    private volatile boolean running = true;
    private final long intervalMillis;
    private final BookingService bookingService;

    StatusMonitor(BookingService bookingService,long intervalMillis){
        if(bookingService == null)
            throw new IllegalArgumentException("Booking Service cannot be null");

        if(intervalMillis < 1)
            throw new IllegalArgumentException("Monitoring interval should be positive");

        this.bookingService = bookingService;
        this.intervalMillis = intervalMillis;
    }

    @Override
    public void run(){
        while(running){
            try {
                Thread.sleep(intervalMillis);
            }catch (InterruptedException ex){
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("[Monitor] Monitoring stopped.");
    }

    // stop monitoring
    public void stopMonitoring(){
        running = false;
    }


}
