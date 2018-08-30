package de.hama.kalender.kalender.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hama.kalender.kalender.CategoryEnum;

public class CalendarCollection implements Serializable {
    private String user, note, league, age, coach, start, end;
    private CategoryEnum type;
    private Integer id, intensity;
    private Date date;

    public CalendarCollection(Integer id, String user, Date date) {
        this.id = id;
        this.user = user;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public CategoryEnum getType() {
        return type;
    }

    public void setType(CategoryEnum type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public Integer getIntensity() {
        return intensity;
    }

    public void setIntensity(Integer intensity) {
        this.intensity = intensity;
    }

    public String getFormattedDate() {
        return new SimpleDateFormat("dd.MM.yyyy").format(date);
    }

    public Date getOriginalDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}