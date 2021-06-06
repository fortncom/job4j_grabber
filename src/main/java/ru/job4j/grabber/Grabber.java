package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.grabber.html.Parse;
import ru.job4j.grabber.html.SqlRuParse;
import ru.job4j.grabber.model.Post;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {
    private final Properties cfg = new Properties();

    public Store store() {
        return new PsqlStore(cfg);
    }

    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    public void cfg() throws IOException {
        try (InputStream in = Grabber.class.getClassLoader().getResourceAsStream("app.properties")) {
            cfg.load(in);
        }
    }

    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .withIdentity("nJob", "group1")
                .build();
        Trigger trigger = newTrigger()
                .withIdentity("t1", "group1")
                .withSchedule(cronSchedule(String.format(
                "%s %s %s %s %s %s",
                    cfg.getProperty("seconds"),
                    cfg.getProperty("t1.minutes"),
                    cfg.getProperty("t1.hours"),
                    cfg.getProperty("day-of-month"),
                    cfg.getProperty("month"),
                    cfg.getProperty("day-of-week")
                )))
                .forJob(job)
                .build();
        Trigger trigger2 = newTrigger()
                .withIdentity("t2", "group1")
                .withSchedule(CronScheduleBuilder
                        .dailyAtHourAndMinute(
                                Integer.parseInt(cfg.getProperty("t2.hours")),
                                Integer.parseInt(cfg.getProperty("t2.minutes"))))
                .forJob(job)
                .build();
        scheduler.scheduleJob(job, trigger);
        scheduler.scheduleJob(trigger2);
        web(store);
        try {
            Thread.sleep(31536000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        scheduler.shutdown();
    }

    public void web(Store store) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(Integer.parseInt(cfg.getProperty("port")))) {
                while (!server.isClosed()) {
                    Socket socket = server.accept();
                    try (OutputStream out = socket.getOutputStream()) {
                        out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                        for (Post post : store.getAll()) {
                            out.write(post.toString().getBytes(StandardCharsets.UTF_16));
                            out.write(System.lineSeparator().getBytes(StandardCharsets.UTF_16));
                            out.write(System.lineSeparator().getBytes(StandardCharsets.UTF_16));
                            out.write(System.lineSeparator().getBytes(StandardCharsets.UTF_16));
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static class GrabJob implements Job {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parser = (Parse) map.get("parse");
            parseSQL(store, parser);
        }

        private void parseSQL(Store store, Parse parser) {
            List<Post> posts = parser.list("https://www.sql.ru/forum/job-offers/1");
            for (int i = 0; i < posts.size(); i++) {
                posts.set(i, parser.detail(posts.get(i).getLink()));
            }
            try {
                for (Post post : posts) {
                    if (post.getName().toLowerCase().contains("java")
                            || post.getName().toLowerCase().contains("джава")) {
                        store.save(post);
                    }
                }
                for (Post post : store.getAll()) {
                    System.out.println(post);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber();
        grab.cfg();
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        grab.init(new SqlRuParse(), store, scheduler);
    }
}
