package com.iced.alexwang.models;

import com.iced.alexwang.libs.CachedFile;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lyrics {
    public static Lyrics createFromFile(CachedFile file) {
        if (patternLrcFile.matcher(file.getAbsolutePath()).find()) {
            try {
                FileInputStream fis = new FileInputStream(file.getAbsolutePath());
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

                Lyrics ret = new Lyrics();
                String line;
                while (true) {
                    line = reader.readLine();
                    if (line == null)
                        break;

                    Matcher matchTag = patternTag.matcher(line);
                    if (matchTag.matches()) {
                        String tag = matchTag.group(1);
                        String tagContent = matchTag.group(2);
                        if (tag.equalsIgnoreCase("offset"))
                            ret.offset = Integer.parseInt(tagContent);
                    } else {
                        LinkedHashMap<Integer, String> timeLyrics = new LinkedHashMap<>();
                        if (parseLyricsLine(line, timeLyrics))
                            ret.lyrics.putAll(timeLyrics);
                    }
                }
                return ret;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public String getCurrentLyrics(int ms) {
        ms -= offset;
        String line = "";

        Iterator it = lyrics.keySet().iterator();
        Integer first = (Integer) it.next();
        if (ms < first)
            return lyrics.get(first);

        Integer lastTime = first;
        for (; it.hasNext();) {
            Integer t = (Integer) it.next();

            if (ms < t)
                return lyrics.get(lastTime);

            line = lyrics.get(t);

            lastTime = t;
        }
        return line;
    }

    public enum CurrentLyricsLinesParams {
        // lines array
        Lines,
        // current line index (Integer)
        CurrentLine
    }

    // get lyrics lines around 'ms', maximum lines = windowWidth * 2 + 1 (the one, before and after)
//    public EnumMap<CurrentLyricsLinesParams, Object> getCurrentLyricsLines(int ms, int windowWidth) {
//        EnumMap<CurrentLyricsLinesParams, Object> ret = new EnumMap<CurrentLyricsLinesParams, Object>();
//
//        int targetTime = getLyricsTimeByTime(ms);
//        ArrayList<String> lines = new ArrayList<>();
//        LoopList<String> targetLines = new LoopList<>(windowWidth * 2 + 1);
//
//        boolean found = false;
//        int afterCount = 0;
//
//        Iterator it = lyrics.keySet().iterator();
//        while (it.hasNext()) {
//            Integer thisTime = (Integer)it.next();
//            targetLines.push(lyrics.get(thisTime));
//            if (thisTime == targetTime)
//                found = true;
//            if (found)
//                afterCount++;
//            if (afterCount > windowWidth)
//                break;
//        }
//
//
//    }

    Iterator getIterator(Integer key) {
        Iterator it = lyrics.keySet().iterator();

        while (it.hasNext()) {
            Integer thisKey = (Integer)it.next();
            if (thisKey.equals(key))
                return it;
        }

        return null;
    }

    // get last iterator of it in the lyrics map keyset
    Iterator last(Iterator itTgt) {
        Iterator it = lyrics.keySet().iterator();
        Iterator last = it;
        it.next();
        // it starts from the second and last starts from the first;

        while (it.hasNext()) {
            if (itTgt.equals(it))
                return last;
            last = it;
        }

        // if itTgt is the first or the last or it is not found, we return null.
        return null;
    }

    static boolean parseLyricsLine(String line, LinkedHashMap<Integer, String> timeLyrics) {
        Status status = Status.Start;

        StringBuilder sbMin = new StringBuilder();
        StringBuilder sbSec = new StringBuilder();
        StringBuilder sbCs = new StringBuilder();
        StringBuilder sbLine = new StringBuilder();
        int min = 0;
        int sec = 0;
        int cs = 0;
        String strLine = "";
        // DFA

        ArrayList<Integer> times = new ArrayList<>();
        for (int i = 0; i < line.length(); ++i) {
            char c = line.charAt(i);
            if (status == Status.Start) {
                if (c == '[')
                    status = Status.Minute;
                else {
                    sbLine.append(c);
                    status = Status.Line;
                }
            } else if (status == Status.CentiSecond) {
                if (isNumber(c))
                    sbCs.append(c);
                else if (c == ']') {
                    cs = Integer.parseInt(sbCs.toString());
                    sbCs = new StringBuilder();

                    int time = cs * 10 + sec * 1000 + min * 60000;
                    times.add(time);
                    status = Status.Start;
                } else
                    return false;
            } else if (status == Status.Minute) {
                if (isNumber(c))
                    sbMin.append(c);
                else if (c == ':') {
                    min = Integer.parseInt(sbMin.toString());
                    sbMin = new StringBuilder();
                    status = Status.Second;
                } else
                    return false;
            } else if(status == Status.Second) {
                if (isNumber(c))
                    sbSec.append(c);
                else if (c == '.') {
                    sec = Integer.parseInt(sbSec.toString());
                    sbSec = new StringBuilder();
                    status = Status.CentiSecond;
                } else
                    return false;
            } else if(status == Status.Line) {
                sbLine.append(c);
            }
        }
        strLine = sbLine.toString();

        for (Integer t : times) {
            timeLyrics.put(t, strLine);
        }
        return true;
    }

    enum Status {
        Start,
        Minute,
        Second,
        CentiSecond,
        Line
    }
    static boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

    static Pattern patternLrcFile = Pattern.compile("/[^/]+\\.lrc$", Pattern.CASE_INSENSITIVE);
    static Pattern patternTag = Pattern.compile("^\\[([A-Za-z0-9]+):([^\\[]+)\\]$");
    int offset = 0;
    TreeMap<Integer, String> lyrics = new TreeMap<>();
}
