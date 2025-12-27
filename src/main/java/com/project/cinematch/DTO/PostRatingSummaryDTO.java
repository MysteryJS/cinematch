package com.project.cinematch.DTO;

public class PostRatingSummaryDTO {

    private double average;
    private long count;
    private Integer myRating;

    public PostRatingSummaryDTO(double average, long count, Integer myRating) {
        this.average = average;
        this.count = count;
        this.myRating = myRating;
    }

    public double getAverage() { return average; }
    public long getCount() { return count; }
    public Integer getMyRating() { return myRating; }
}
