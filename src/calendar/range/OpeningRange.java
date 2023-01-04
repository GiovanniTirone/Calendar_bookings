package calendar.range;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

public class OpeningRange<RT extends RangeTime> extends RangeTime<TreeSet<RT>>{

    private Class<RT> class_RT;

    Constructor<RT> constructor_RT;

    private TreeSet<RT> subRangesSet;

    public OpeningRange(Class<RT> class_RT, LocalTime startTime, LocalTime endTime) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        super(startTime, endTime);
        this.subRangesSet = new TreeSet<>();
        this.class_RT = class_RT;
        this.constructor_RT = class_RT.getConstructor(LocalTime.class, LocalTime.class);
        subRangesSet.add(constructor_RT.newInstance(startTime,endTime));  //realClassRT.newInstance());
    }


    public LocalTime getOpeningTime () {
        return this.getStartTime();
    }

    public LocalTime getClosureTime () {
        return this.getEndTime();
    }

    public TreeSet<RT> getSubRangesSet() {
        return subRangesSet;
    }

    public Optional<RT> getRangeByTime (LocalTime time){
        for(RT range : subRangesSet){
            if(range.checkTimeInRange(time)){
                return Optional.of(range);
            }
        }
        return Optional.empty();
    }

    public RT higher (RT range) {
        return subRangesSet.higher(range);
    }

    public void addRangeTime (RT newRange) throws Exception {

        if(!checkRangeInOpeningRange(newRange))
            throw new Exception("The range must be in the opening range");


        Map<RT, OverlapInfoEnum> overlappingRanges = new HashMap<>();
        for(RT range : subRangesSet){
            OverlapInfo overlapInfo = range.checkOverlapRange(newRange);
            if(overlapInfo.isOverlap()) {
                overlappingRanges.put(range, overlapInfo.getOverlapInfoEnum());
            }
        }
        for(RT range : overlappingRanges.keySet()){
            divideOverlappingRanges(range,newRange,overlappingRanges.get(range));
        }

    }

    private void divideOverlappingRanges (RT rangeToDivide, RT newRange, OverlapInfoEnum info) throws Exception {

        if (info == OverlapInfoEnum.TOTAL_COVER) {
            rangeToDivide.overlapDatas(newRange.getDatas());
        }
        else {
            TreeSet<RT> dividedRanges;

            switch (info) {
                case START -> {
                    dividedRanges = divideRangeAndSubstitute(rangeToDivide, newRange.getStartTime());
                    dividedRanges.last().overlapDatas(newRange.getDatas());
                }
                case END -> {
                    dividedRanges = divideRangeAndSubstitute(rangeToDivide, newRange.getEndTime());
                    dividedRanges.first().overlapDatas(newRange.getDatas());
                }
                case INNER -> {
                    dividedRanges = divideRangeAndSubstitute(rangeToDivide, newRange.getStartTime());
                    dividedRanges = divideRangeAndSubstitute(dividedRanges.last(), newRange.getEndTime());
                    dividedRanges.first().overlapDatas(newRange.getDatas());
                }
            }

        }
    }

    public TreeSet<RT> divideRangeAndSubstitute(RT rangeToDivide,LocalTime middleTime) throws Exception {
        TreeSet<RT> dividedRanges = (TreeSet<RT>) rangeToDivide.divide(class_RT,middleTime);
        subRangesSet.remove(rangeToDivide);
        subRangesSet.add(dividedRanges.first());
        subRangesSet.add(dividedRanges.last());
        return dividedRanges;
    }

    private boolean checkRangeInOpeningRange (RangeTime range){
        return range.getStartTime().isBefore(getOpeningTime()) || range.getEndTime().isAfter(getClosureTime()) ? false : true;
    }

    public boolean checkTimeInOpeningRange (LocalTime time) {
        return (time.isAfter(getOpeningTime()) || time.equals(getOpeningTime())) && (time.isBefore(getClosureTime())); // todo mettere una zona NO range alla fine??
    }



    public String getDetailsOfRanges () {
        String str = "";
        for(RangeTime range : subRangesSet){
            str += range.getDetailsString() + "\n";
        }
        return str;
    }


    public void printRanges () {
        System.out.println(getDetailsOfRanges());
    }




    @Override
    public void overlapDatas(TreeSet<RT> newDatas) {

    }

    @Override
    public TreeSet<RT> getDatas() {
        return null;
    }

    @Override
    public void setDatas(TreeSet<RT> subRangesSet) {
        this.subRangesSet = subRangesSet;
    }

    @Override
    public TreeSet<RT> copyDatas(TreeSet<RT> dataToCopy) {
        return null;
    }

    @Override
    public String getDetailsString() {
        return null;
    }


}
