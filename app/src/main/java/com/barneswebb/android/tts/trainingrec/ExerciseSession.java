package com.barneswebb.android.tts.trainingrec;

/**
 * Created by richard.barnes-webb on 2015/12/23.
 */

public class ExerciseSession {
    final int    id;
    private final String userName;
    final String excercise_date;
    final String duration;
    final String program;
    final String comments;

    public ExerciseSession(int id, String userName, String excerzDate, String excerzDur, String program, String comments) {
        this.id = id;
        this.userName=          userName;
        this.excercise_date=    excerzDate;
        this.duration=          excerzDur;
        this.program=           program;
        this.comments=          comments;
    }

    public String toCsvRecord() {
        return
                "\""+ userName          +"\","+
                "\""+ excercise_date    +"\","+
                "\""+ duration          +"\","+
                "\""+ program           +"\","+
                "\""+ comments          +"\",";
    }

    public static String csvHeaders() {
        return
                "\"User Name         \","+
                "\"Exercise date     \","+
                "\"Duration          \","+
                "\"Program           \","+
                "\"Comments          \",";
    }

}
