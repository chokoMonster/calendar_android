package de.hama.kalender.kalender.entity;

import java.io.Serializable;
import java.util.Date;

public class Gear implements Serializable {

    private int id;
    private int gear;
    private Date date;

    public Gear() {
    }

    public Gear(int id, Date date, int gear) {
        this.id=id;
        this.date=date;
        this.gear=gear;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGear() {
        return gear;
    }

    public void setGear(int gear) {
        this.gear = gear;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
