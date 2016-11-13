package com.barneswebb.android.tts.trainingrec;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class TrainingContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<TrainingItem> ITEMS = new ArrayList<TrainingItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, TrainingItem> ITEM_MAP = new HashMap<String, TrainingItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(TrainingItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static TrainingItem createDummyItem(int position) {
        return new TrainingItem(
                String.valueOf(position),
                "Date: "+ ExerciseDataOpenHelper.ISO8601Format.format(new Date()),
                "Duration: "+"09:99",
                "Completed Exercises: "+" Pr5f1.mp3, Pr5f10.mp3, Pr5f2.mp3, Pr5f3.mp3",
                "These are dummy comments");
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class TrainingItem {
        public final String id;
        public final String date;
        public final String duration;
        public final String comments;
        public final String complExcer;
        /**
         * @param id
         * @param date
         * @param duration
         * @param programme
         * @param comments
         */
        public TrainingItem(String id, String date, String duration, String programme, String comments) {
            this.id = id;
            this.date = date;
            this.duration = duration;
            this.complExcer = programme;
            this.comments = comments;
        }

        @Override
        public String toString() {
            return id+':'+date;
        }
    }
}
