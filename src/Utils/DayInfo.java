package Utils;

public class DayInfo {
    private int day;
    private float numSick;

    public DayInfo(int day, float numSick) {
        this.day = day;
        this.numSick = numSick;
    }

    public int getDay() {
        return this.day;
    }

    public float getNumSick() {
        return this.numSick;
    }

    public void setNumSick(float numSick) {
        this.numSick = numSick;
    }
}
