package calendar.range;

import calendar.booking.Booking;
import java.lang.reflect.Constructor;
import java.time.LocalTime;
import java.util.TreeSet;

public abstract class RangeTime<DataType> implements Comparable<RangeTime<DataType>> {

    private LocalTime startTime;

    private LocalTime endTime;


    public RangeTime (LocalTime startTime, LocalTime endTime){
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public RangeTime (LocalTime startTime, LocalTime endTime, Booking booking) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }


    public abstract void overlapDatas(DataType newDatas) ;

    public abstract DataType getDatas ( );

    public abstract  void setDatas (DataType datas);

    public OverlapInfo checkOverlapRange (RangeTime newRange){

        boolean startEquals_endInner = newRange.startTime.equals(this.startTime) && newRange.endTime.isBefore(this.endTime);
        boolean startEquals_endOut = newRange.startTime.equals(this.startTime) && newRange.endTime.isAfter(this.startTime);
        boolean startInner_endOut = newRange.startTime.isAfter(this.startTime) && newRange.startTime.isBefore(this.endTime) && newRange.endTime.isAfter(this.endTime);
        boolean endEquals_startInner = newRange.endTime.equals(this.endTime) && newRange.startTime.isAfter(this.startTime);
        boolean endEquals_startOut = newRange.endTime.equals(this.endTime) && newRange.startTime.isBefore(this.startTime);
        boolean endInner_startOut = newRange.endTime.isAfter(this.startTime) && newRange.endTime.isBefore(this.endTime) && newRange.startTime.isBefore(this.startTime);
        boolean totalCover = newRange.startTime.isBefore(this.startTime) && newRange.endTime.isAfter(this.endTime);
        boolean inner = newRange.startTime.isAfter(this.startTime) && newRange.startTime.isBefore(this.endTime)
                            && newRange.endTime.isAfter(this.startTime) && newRange.endTime.isBefore(this.endTime);

        if(endEquals_startInner || startInner_endOut) {
            return new OverlapInfo(true, OverlapInfoEnum.START);
        }
        if(startEquals_endInner || endInner_startOut) {
            return new OverlapInfo(true,OverlapInfoEnum.END);
        }
        if(totalCover || startEquals_endOut || endEquals_startOut) {
            return new OverlapInfo(true,OverlapInfoEnum.TOTAL_COVER);
        }
        if(inner)
            return new OverlapInfo(true,OverlapInfoEnum.INNER);

        return new OverlapInfo(false,OverlapInfoEnum.NO_INFO);
    }

    public boolean checkTimeInRange (LocalTime time) {
        return (time.isAfter(startTime) || time.equals(startTime)) && (time.isBefore(endTime));
    }

    public<RT extends RangeTime> TreeSet<RT> divide (Class<RT> class_RT, LocalTime middleTime) throws Exception {
        if(middleTime.isBefore(getStartTime()) || middleTime.isAfter(getEndTime())){
            throw new Exception("The middle time must be inside the range to divide");
        }
        Constructor<RT> constructor_RT = class_RT.getConstructor(LocalTime.class,LocalTime.class);
        RT newRange1 = constructor_RT.newInstance(getStartTime(),middleTime);
        RT newRange2 = constructor_RT.newInstance(middleTime,getEndTime());
        newRange1.setDatas(getDatas());
        newRange2.setDatas(copyDatas(getDatas()));
        TreeSet<RT> newRanges = new TreeSet<>();
        newRanges.add(newRange1);
        newRanges.add(newRange2);
        return newRanges;
    }

    public abstract DataType copyDatas (DataType dataToCopy);


    @Override
    public int compareTo(RangeTime rangeTime) {
        return this.startTime.compareTo(rangeTime.startTime);
    }

   public abstract String getDetailsString ( ) ;

}
