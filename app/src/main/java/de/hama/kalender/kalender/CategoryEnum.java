package de.hama.kalender.kalender;

public enum CategoryEnum {

    BASKETBALL("Basketball"),
    CYCLING("Fahrrad fahren"),
    RUNNING("Laufen"),
    SWIMMING("Schwimmen"),
    POWER("Kraft"),
    GAME("Spiel"),
    OTHER("Sonstiges");

    private String value;

    private CategoryEnum(String value) {
        this.value = value;
    }

    public boolean contains(String string) {
        return value.toLowerCase().contains(string.toLowerCase());
    }

    public String getValue() {
        return value;
    }
}

