/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task{

    protected String completeBy;

    public Deadline (String description, String completeBy){
        super(description);
        this.completeBy = completeBy;
    }

    @Override
    public String toString(){
        return "[D]" + super.toString() +
                " (by: " + completeBy + ")";
    }
}
