public class Event {
    private final String eventName;
    private final int totalSeats;

    public Event(String eventName, int totalSeats){
        if(eventName.isBlank() || eventName == null){
            throw new IllegalArgumentException("Event name should not be empty");
        }
        if(totalSeats <=0){
            throw new IllegalArgumentException("Invalid input. Enter a valid number.");
        }
        this.eventName = eventName;
        this.totalSeats = totalSeats;
    }

    public String getEventName() {
        return eventName;
    }

    public int getTotalSeats() {
        return totalSeats;
    }
}
