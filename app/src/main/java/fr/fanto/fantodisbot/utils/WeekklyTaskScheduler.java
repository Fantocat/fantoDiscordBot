package fr.fanto.fantodisbot.utils;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeekklyTaskScheduler {
    
    public void resumeLolTask() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            System.out.println("Task executed at: " + LocalDateTime.now());
            // Votre méthode à exécuter
        };

        // Calculer le délai jusqu'au prochain lundi à 3h du matin
        long initialDelay = calculateInitialDelayLol();
        long period = TimeUnit.DAYS.toMillis(7); // Une semaine en millisecondes

        // Planifier la tâche
        scheduler.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
    }
    

    private static long calculateInitialDelayLol() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMonday = now.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                                      .withHour(3).withMinute(0).withSecond(0).withNano(0);

        // Si aujourd'hui est lundi mais après 3h, aller au lundi suivant
        if (now.getDayOfWeek() == DayOfWeek.MONDAY && now.isBefore(nextMonday.minusDays(7))) {
            nextMonday = nextMonday.minusWeeks(1);
        }

        // Calculer la durée en millisecondes entre maintenant et le prochain lundi
        Duration duration = Duration.between(now, nextMonday);
        return duration.toMillis();
    }
}