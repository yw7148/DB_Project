import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserQuery {
    String stml;
    ResultSet r = null;
    PreparedStatement p = null;
    Connection conn;

    String viewname;

    public UserQuery(Connection conn) {
        this.conn = conn;
    }

    public List<userfavoriteinfo> QueryALLFavorites() {
        try {
            stml = "select * from " + viewname + " order by fname;";
            p = conn.prepareStatement(stml);
            r = p.executeQuery();

            List<userfavoriteinfo> result = new ArrayList<userfavoriteinfo>();
            System.out.println(userfavoriteinfo.HeadQuery());
            for (int count = 1; r.next(); count++) {
                userfavoriteinfo afavortuple = new userfavoriteinfo(r);
                System.out.print(String.format("%20d", count) + "|");
                System.out.println(afavortuple.QueryTuple());
                result.add(afavortuple);
            }

            return result;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean AddFavoriteInfo(userfavoriteinfo newfavorite) {
        try {
            stml = "insert into " + viewname + "(userid, fname, station_name, weekday, hour) values (?, ?, ?, ?, ?) ;";

            System.out.println(stml);
            p = conn.prepareStatement(stml);
            p.setString(1, newfavorite.getUserid());
            p.setString(2, newfavorite.getFname());
            p.setString(3, newfavorite.getStation_name());
            p.setInt(4, newfavorite.getWeekday());
            p.setInt(5, newfavorite.getHour());
            if (p.executeUpdate() == 1) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean DeleteFavorite(String fname) {
        try {
            stml = "delete from " + viewname + " where fname = ?";
            p = conn.prepareStatement(stml);
            p.setString(1, fname);
            return (p.executeUpdate() > 0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void SuccessLogin(String userid) {
        this.viewname = "view" + userid;
    }

    public boolean Register(String userid) {
        try {
            stml = "insert into users values (?);";

            p = conn.prepareStatement(stml);
            p.setString(1, userid);
            if (p.executeUpdate() > 0) {
                stml = "create view view" + userid + " as " + "select * " + "from user_favorite "
                        + String.format(" where userid = '%s' ;", userid);
                p = conn.prepareStatement(stml);
                p.execute();

                this.viewname = "view" + userid;

                return true;
            } else
                return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
