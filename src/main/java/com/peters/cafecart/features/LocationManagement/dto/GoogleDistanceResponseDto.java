package com.peters.cafecart.features.LocationManagement.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GoogleDistanceResponseDto {

    @JsonProperty("destination_addresses")
    private List<String> destinationAddresses;

    @JsonProperty("origin_addresses")
    private List<String> originAddresses;

    private List<Row> rows;
    private String status;

    public List<Row> getRows() {
        return rows;
    }

    public String getStatus() {
        return status;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Row {
        private List<Element> elements;

        public List<Element> getElements() {
            return elements;
        }

        public void setElements(List<Element> elements) {
            this.elements = elements;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Element {
        private Distance distance;
        private Duration duration;
        private String status;

        public Distance getDistance() {
            return distance;
        }

        public void setDistance(Distance distance) {
            this.distance = distance;
        }

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Distance {
        private String text;
        private long value;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Duration {
        private String text;
        private long value;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }
    }
}
