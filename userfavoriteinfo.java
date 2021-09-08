import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class userfavoriteinfo {
    private String userid;
    private String fname;
    private String station_name;
    private int weekday;
    private int hour;

    public userfavoriteinfo() {

    }

    public userfavoriteinfo(ResultSet r) throws SQLException {
        this.userid = r.getString("userid");
        this.fname = r.getString("fname");
        this.station_name = r.getString("station_name");
        this.weekday = r.getInt("weekday");
        this.hour = r.getInt("hour");
    }

    public userfavoriteinfo(String userid, String fname, String station_name, int weekday, int hour) {
        this.userid = userid;
        this.fname = fname;
        this.station_name = station_name;
        this.weekday = weekday;
        this.hour = hour;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFname() {
        return this.fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getStation_name() {
        return this.station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public int getWeekday() {
        return this.weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public int getHour() {
        return this.hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public static String HeadQuery() {
        return (String.format("%20s", "No.") + "|" + String.format("%20s", "userid") + "|"
                + String.format("%20s", "fname") + "|" + String.format("%20s", "station_name") + "|"
                + String.format("%20s", "weekday") + "|" + String.format("%20s", "hour") + "|") + "\n"
                + (String.format("%-130s", "").replace(' ', '-'));
    }

    public String QueryTuple() {
        return (String.format("%20s", userid) + "|" + String.format("%20s", fname) + "|"
                + String.format("%20s", station_name) + "|" + String.format("%20d", weekday) + "|"
                + String.format("%20d", hour) + "|");
    }
}
