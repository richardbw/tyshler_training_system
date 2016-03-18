package com.barneswebb.android.tts.trainingrec;

/**
 * Created by richard.barnes-webb on 2015/12/23.
 */
public class ExerciseSession {
    final int    id;
    final String userName;
    final String excerzDate;
    final String excerzDur;
    final String program;
    final String comments;

    public ExerciseSession(int id, String userName, String excerzDate, String excerzDur, String program, String comments) {
        this.id = id;
        this.userName=   userName;
        this.excerzDate= excerzDate;
        this.excerzDur=  excerzDur;
        this.program=    program;
        this.comments=   comments;           
    }
}
