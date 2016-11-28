package com.kelin.scrollablepanel.sample;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by kelin on 16-11-18.
 */

public class OrderInfo {
    private long id;
    private String guestName;
    private Status status;
    private boolean isBegin;

    public enum Status {
        CHECK_IN,
        REVERSE,
        BLANK;

        private static final List<Status> VALUES =
                Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        public static Status randomStatus() {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isBegin() {
        return isBegin;
    }

    public void setBegin(boolean begin) {
        isBegin = begin;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
