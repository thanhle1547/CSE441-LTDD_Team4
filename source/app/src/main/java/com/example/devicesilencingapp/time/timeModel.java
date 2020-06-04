package com.example.devicesilencingapp.time;

public class timeModel implements Comparable<timeModel>{
    public static final int SUNDAY = 0;
    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRDIAY = 5;
    public static final int SATURDAY = 6;

    public long id = -1;
    public int timeHour;
    public int timeMinute;
    public boolean[] repeatingDays;
    public boolean isEnabled;

    public timeModel(){repeatingDays = new boolean[7];}
    public timeModel(long id, int timeHour, int timeMinute,
                      boolean[] repeatingDays, boolean isEnabled) {
        this.id = id;
        this.timeHour = timeHour;
        this.timeMinute = timeMinute;
        this.repeatingDays = repeatingDays;
        this.isEnabled = isEnabled;
    }
    public timeModel(int timeHour, int timeMinute,
                     boolean[] repeatingDays, boolean isEnabled) {
        this.timeHour = timeHour;
        this.timeMinute = timeMinute;
        this.repeatingDays = repeatingDays;
        this.isEnabled = isEnabled;
    }



    @Override
    public int compareTo(timeModel model) {
        if (model == null)
            return -1;
        return getId() == model.getId()
                && getisEnabled() == model.getisEnabled()
                && getRepeatingDays() == model.getRepeatingDays()
                && getTimeHour() == model.getTimeHour()
                && getTimeMinute() == model.getTimeMinute()
                ? 0 : -1;
    }

    /**
     *
     * @param model the model to be compared
     * @return a negative integer if not equal or null, zero if equal to
     * @Override
     *     public int compareTo(timeModel model) {
     *         if (model == null)
     *             return -1;
     *         return getId() == model.getId()
     *                 && getName().equals(model.getName())
     *                 && getAddress().equals(model.getAddress())
     *                 && getLabel() == model.getLabel()
     *                 && getLongitude() == model.getLongitude()
     *                 && getLatitude() == model.getLatitude()
     *                 && getRadius() == model.getRadius()
     *                 && getExpiration() == model.getExpiration()
     *                 && getStatus() == model.getStatus()
     *                 ? 0 : -1;
     *     }
     */


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTimeHour() {
        return timeHour;
    }

    public void setTimeHour(int timeHour) {
        this.timeHour = timeHour;
    }

    public int getTimeMinute() {
        return timeMinute;
    }

    public void setTimeMinute(int timeMinute) {
        this.timeMinute = timeMinute;
    }

    public boolean[] getRepeatingDays() {
        return repeatingDays;
    }
    public void setRepeatingDay(int dayOfWeek, boolean value) {
        repeatingDays[dayOfWeek] = value;
    }
    public boolean getRepeatingDay(int dayOfWeek) {
        return repeatingDays[dayOfWeek];
    }
    public void setRepeatingDays(boolean[] repeatingDays) {
        this.repeatingDays = repeatingDays;
    }


    public boolean isEnabled() {
        return isEnabled;
    }
    public boolean getisEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }
}
