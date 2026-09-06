package com.evergreen.generalhospital.config;


import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.LocalTime;


@ConfigurationProperties(prefix = "hospital")
@Validated
public class HospitalProperties {

    private final Scheduling scheduling = new Scheduling();

    public Scheduling getScheduling() { return scheduling; }

    public static class Scheduling {
        // define shift start time, end time and appointment duration

        @NotNull
        private LocalTime defaultShiftStart;
        @NotNull
        private LocalTime defaultShiftEnd;
        @NotNull
        private Duration defaultSlotDuration;


        public LocalTime getDefaultShiftStart() {
            return defaultShiftStart;
        }

        public void setDefaultShiftStart(LocalTime defaultShiftStart) {
            this.defaultShiftStart = defaultShiftStart;
        }

        public LocalTime getDefaultShiftEnd() {
            return defaultShiftEnd;
        }

        public void setDefaultShiftEnd(LocalTime defaultShiftEnd) {
            this.defaultShiftEnd = defaultShiftEnd;
        }

        public Duration getDefaultSlotDuration() {
            return defaultSlotDuration;
        }

        public void setDefaultSlotDuration(Duration defaultSlotDuration) {
            this.defaultSlotDuration = defaultSlotDuration;
        }

    }
}
