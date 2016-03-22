package com.barneswebb.android.tts.beep;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * https://stackoverflow.com/questions/23096533/how-to-play-a-sound-with-a-given-sample-rate-in-java
 * https://stackoverflow.com/questions/12684604/android-timer-schedule-vs-scheduleatfixedrate
 * @author richard.barnes-webb
 *
 */
public class BeepEngine {

    private static final String  TAG            = "ttsBeep";
    public static float          SAMPLE_RATE    = 8000f;
    public static int            TONE_LENGTH_MS = 100;
    private static Random        random         = new Random();
    
    private static final Pattern REPEAT_REGEX = Pattern.compile("^#Repeat:?\\s*([0-9]+)\\s+.*\\s*(seq|ran).*$",Pattern.CASE_INSENSITIVE);
    public  static enum  Repeat {SEQ,RAN,NONE};

    public  static enum  Tone   {LO,HI,DBL,NONE};

    private ScheduledExecutorService scheduler      = Executors.newScheduledThreadPool(20);
    private List<TimeSlice>          timesliceList;
    private List<ScheduledFuture<?>> scheduledBeeps = new ArrayList<ScheduledFuture<?>>();


    public BeepEngine(String csvFilename) {
        try {
            timesliceList = readTimeSliceCsv(getTextFileContents(new File(csvFilename)));
        } catch (IOException e) {
            Log.e(TAG, "Can't parse csv: " + e.getMessage());
        }
       
    }
    

    private void scheduleBeeps() {

        scheduler = Executors.newScheduledThreadPool(scheduledBeeps.size());//create a thread for each timeslice
        long sumSinceStart = 0;
        scheduledBeeps.clear(); 
        
        for (TimeSlice slice : timesliceList) {
            Log.d(TAG, "scheduling: " + slice);
            scheduledBeeps.add(scheduler.schedule(createRunnable(slice), sumSinceStart , MILLISECONDS));
            sumSinceStart += slice.getDuration();
        };
    }

    
    public Runnable createRunnable(final TimeSlice slice) 
    {
        return new Runnable() 
        {
            @Override public void run() {
                long startTime = System.currentTimeMillis();

                //was: ThreadLocalRandom.current().current().nextLong(TONE_LENGTH_MS, slice.getDuration()-TONE_LENGTH_MS);
                long startBeepLocation = (long)(random.nextDouble() * (slice.getDuration() - TONE_LENGTH_MS)); //@see http://stackoverflow.com/a/2546158

                Log.d(TAG, (new SimpleDateFormat("hh:mm:ss.SSS")).format(new Date())+ "> start: {"+ slice.getDuration() +"  s:"+startBeepLocation+", t:"+slice.tone);
                
                if (slice.doIgnoreThisSlice) {
                    System.out.println("  IGNORING");
                    try {Thread.sleep(slice.getDuration());} catch (InterruptedException e) {e.printStackTrace();}
                    System.out.println( (new SimpleDateFormat("hh:mm:ss.SSS")).format(new Date())+ "    end: "+ slice.getDuration()+"}"+(System.currentTimeMillis() - (startTime+slice.getDuration())));
                    return;
                }
                
                try {Thread.sleep(startBeepLocation);} catch (InterruptedException e) {Log.e(TAG, e.getMessage());}
                switch (slice.getTone()) {
                    case HI:  highTone();   break;
                    case LO:  lowTone();    break;
                    case DBL: doubleTone(); break;
                    default: break;
                }
                
                long endTime = System.currentTimeMillis() - (startTime+slice.getDuration()); 
                Log.d(TAG, (new SimpleDateFormat("hh:mm:ss.SSS")).format(new Date())+ "    end: "+ slice.getDuration()+"} endTime: "+endTime);
                
            }
        };
    }

    

    /* Plain java only
    public static void generateTone(int hz, int msecs) {

        try {
            tone(hz, msecs, 1.0);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void tone(int hz, int msecs, double vol) throws LineUnavailableException 
    {
      byte[] buf = new byte[1];
      AudioFormat af = new AudioFormat(
              SAMPLE_RATE, // sampleRate
              8,           // sampleSizeInBits
              1,           // channels
              true,        // signed
              false);      // bigEndian
      SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
      sdl.open(af);
      sdl.start();
      for (int i=0; i < msecs*8; i++) {
        double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
        buf[0] = (byte)(Math.sin(angle) * 127.0 * vol);
        sdl.write(buf,0,1);
      }
      sdl.drain();
      sdl.stop();
      sdl.close();
    }
    */

    /* Android only */
    //@thanks: https://gist.github.com/slightfoot/6330866
    private static AudioTrack generateTone(double freqHz, int durationMs)
    {
        int count = (int)(44100.0 * 2.0 * (durationMs / 1000.0)) & ~1;
        short[] samples = new short[count];
        for(int i = 0; i < count; i += 2){
            short sample = (short)(Math.sin(2 * Math.PI * i / (44100.0 / freqHz)) * 0x7FFF);
            samples[i + 0] = sample;
            samples[i + 1] = sample;
        }
        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                count * (Short.SIZE / 8), AudioTrack.MODE_STATIC);
        track.write(samples, 0, count);
        return track;
    }
    //*/

    private static void highTone() 
    {
        generateTone(1100, TONE_LENGTH_MS);
    }
    private static void lowTone()  
    {
        generateTone(400, TONE_LENGTH_MS);
    }
    private static void doubleTone()  
    {
        generateTone(800,TONE_LENGTH_MS/2);
        generateTone(800,TONE_LENGTH_MS/2);
    }

    
    private List<TimeSlice> readTimeSliceCsv(String csvContents) throws IOException {

        ArrayList<TimeSlice> timeSliceArr = new ArrayList<TimeSlice>();
        BufferedReader reader = new BufferedReader(new StringReader(csvContents));
        int repeat_group_count = 1;
        String line;
        
        while ((line = reader.readLine()) != null) {
            Matcher matcher = REPEAT_REGEX.matcher(line);
            if (matcher.matches()) repeat_group_count = Integer.parseInt(matcher.group(1));//named not supported
            
            if (line.startsWith("#")) continue;  //ignore comments - use in headers in this case too
            
            String[] rowData = line.split(",");
            timeSliceArr.add(new TimeSlice(rowData[0], (rowData.length>1)?rowData[1]:"NONE",(rowData.length>2)?rowData[2]:null));
        }
        
 
        //repeat repeat:
        ArrayList<TimeSlice> repeatedTimeSliceArr = new ArrayList<BeepEngine.TimeSlice>();
        for (int i = 0; i < repeat_group_count; i++) { //TODO support random ordering
            repeatedTimeSliceArr.addAll(timeSliceArr);
        }
        
        return repeatedTimeSliceArr;
    }
    

    public static boolean getRandomBoolean(String percentProbability)
    {
        if (percentProbability == null) return false;
        return random.nextFloat() < (Float.parseFloat(percentProbability)/100);
    }



    private void shutdown() {

        boolean mayInterruptIfRunning = true;
        for (ScheduledFuture<?> future : scheduledBeeps) 
        {
            if (!(future.isCancelled() || future.isDone())) 
                future.cancel(mayInterruptIfRunning);
        }
        
        if (!scheduler.isShutdown()) 
            scheduler.shutdown();
        
        
    }
    
    public static String getTextFileContents(File filename) {
        String content = null;
        try {
            content = new Scanner(filename).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Unable to read ["+filename+"]: "+e.getMessage());
        }
        return content;
    }

    
    /* Plain java only
    public static class Log 
    {
        public static void e(String tag, String string) {
           System.out.println("LOG>"+tag+": "+string);
        }
        public static void d(String tag, String string) {
            e(tag, string);
        }
    }*/
    
    public class TimeSlice {
        private long duration = 0;
        private Tone tone = Tone.NONE;
        boolean doIgnoreThisSlice = false;

        public TimeSlice(String durationS, String toneS, String randomIgnoreProb) {
            this.duration = Long.parseLong(durationS.trim());
            this.tone = (toneS.trim().length()==0)?Tone.NONE:Tone.valueOf(toneS.trim());
            this.doIgnoreThisSlice = getRandomBoolean(randomIgnoreProb);
        }

        public long getDuration() {
            return duration;
        }
        public Tone getTone() {
            return tone;
        }

        @Override
        public String toString() {

            return "TimeSlice [duration=" + duration + ", tone=" + tone
                + ", doIgnoreThisSlice=" + doIgnoreThisSlice + "]";
        }
    }


    /**
     */
    public static void main(String[] args) throws Exception
    {
        System.out.println("Starting..");
        
        BeepEngine thisApp = new BeepEngine("BeepEngine.csv");
        thisApp.scheduleBeeps();
        
        Thread.sleep(25_000);
        
        thisApp.shutdown();
        
        System.out.println("Done.");
    }
}

