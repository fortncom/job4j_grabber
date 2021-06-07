package ru.job4j.grabber;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import ru.job4j.grabber.html.Parse;

import java.util.function.Predicate;

public interface Grab {

    void init(Parse parse, Store store, Scheduler scheduler, Predicate<String> filter) throws SchedulerException;
}
