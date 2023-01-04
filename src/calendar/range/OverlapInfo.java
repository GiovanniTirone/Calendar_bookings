package calendar.range;

public class OverlapInfo {

    private boolean overlap;

    private OverlapInfoEnum overlapInfoEnum;

    public OverlapInfo (boolean overlap, OverlapInfoEnum overlapInfoEnum){
        this.overlap = overlap;
        this.overlapInfoEnum = overlapInfoEnum;
    }

    public boolean isOverlap() {
        return overlap;
    }

    public void setOverlap(boolean overlap) {
        this.overlap = overlap;
    }

    public OverlapInfoEnum getOverlapInfoEnum() {
        return overlapInfoEnum;
    }

    public void setOverlapInfoEnum(OverlapInfoEnum overlapInfoEnum) {
        this.overlapInfoEnum = overlapInfoEnum;
    }

    public void printDetails () {
        System.out.println("Overlap: " + overlap + " - " + "Info: " + overlapInfoEnum.name());
    }

}
