package Model;

public class BounceDefinition {

    //If the user spends less that this amount of time on the server, it is a bounce
    private int maxTimeSpentBeforeLeaving;

    //If this is true, then it is only a bounce if the user does not converse
    private boolean shouldUserNotConverse;

    public BounceDefinition(int maxTimeSpentBeforeLeaving, boolean shouldUserNotConverse){
        this.maxTimeSpentBeforeLeaving = maxTimeSpentBeforeLeaving;
        this.shouldUserNotConverse = shouldUserNotConverse;
    }

    public int getMaxTimeSpentBeforeLeaving() {
        return maxTimeSpentBeforeLeaving;
    }

    public boolean getShouldUserNotConverse(){
        return shouldUserNotConverse;
    }
}
