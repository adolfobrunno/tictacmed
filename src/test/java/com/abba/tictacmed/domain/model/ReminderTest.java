package com.abba.tictacmed.domain.model;

import com.abba.tictacmed.domain.exceptions.InvalidRruleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReminderTest {

    @Test
    void updateNextDispatchSetsNextDispatchForValidRrule() {
        Reminder reminder = new Reminder();
        reminder.setRrule("FREQ=MINUTELY;INTERVAL=5;DURATION=24H");

        reminder.updateNextDispatch();

        System.out.println(reminder.getNextDispatch());

        assertNotNull(reminder.getNextDispatch());
    }


    @Test
    void updateNextDispatchSetsNullWhenNoMoreOccurrences() {
        Reminder reminder = new Reminder();
        reminder.setRrule("FREQ=DAILY;UNTIL=19990101T000000Z");

        reminder.updateNextDispatch();

        assertNull(reminder.getNextDispatch());
    }

    @Test
    void updateNextDispatchThrowsOnInvalidRrule() {
        Reminder reminder = new Reminder();
        reminder.setRrule("NOT-A-RRULE");

        assertThrows(InvalidRruleException.class, reminder::updateNextDispatch);
    }
}
