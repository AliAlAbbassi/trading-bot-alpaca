package hello;

import java.time.ZonedDateTime;

public class state {
    private String symbol;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private double prevPrice = 0;

    public state() {
    }

    public state(String symbol) {
        this.setSymbol(symbol);
    }

    public double getPrevPrice() {
        return this.prevPrice;
    }

    public void setPrevPrice(double prev) {
        this.prevPrice = prev;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setStartTime(ZonedDateTime date) {
        this.startTime = date;
    }

    public ZonedDateTime getStartTime() {
        return this.startTime;
    }

    public void setEndTime(ZonedDateTime date) {
        this.endTime = date;
    }

    public ZonedDateTime getEndTime() {
        return this.endTime;
    }

    public boolean isDateNull() {
        if (this.getStartTime() == null && this.getEndTime() == null) {
            return true;
        }
        return false;
    }
}
