package calendar.range;

public enum OverlapInfoEnum {

    NO_INFO,
    START, // Start inner end out
    END, // end inner start out

    TOTAL_COVER,

    INNER;

    //  START_EQUALS_END_OUT, = TOTAL_COVER
    // START_EQUALS_END_INNER, = END
    //  END_EQUALS_START_OUT, = TOTAL_COVER
    //  END_EQUALS_START_INNER, = START
    //  START_EQUALS_END_EQUALS, = TOTAL_COVER

}
