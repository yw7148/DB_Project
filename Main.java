import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws SQLException {
        // Check Postgres Driver available
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Connect to local RDBMS
        Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://ec2-54-243-92-68.compute-1.amazonaws.com/d4d9vt587ncufe?user=lvztqnrnmlrpgs&password=5115922033898d872196935f79d4df337031771eeeb6aec20846194bfd30bb4e");
        Statement st = conn.createStatement();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        UserQuery userTool = new UserQuery(conn);

        Boolean signed = false;
        while (true) {
            String Id = null;
            while (!signed) {
                System.out.print("Sign In with ID: ");

                try {
                    Id = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Query
                ResultSet rs = st
                        .executeQuery(String.format("SELECT count(1) FROM public.users WHERE userid = '%s'", Id));
                if (rs.next()) {

                    if (0 != rs.getInt(1)) {
                        userTool.SuccessLogin(Id);
                        signed = true;
                    } else {
                        System.out.print("That Id was not exist in our service. Register ? (Y / N)");
                        String answer;
                        try {
                            answer = br.readLine();
                            if (answer.equals("Y")) {
                                if (userTool.Register(Id)) {
                                    System.out.println("Register : Success");
                                    signed = true;
                                } else {
                                    System.out.println("Register : Error");
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println();
                }

                rs.close();
            }
            // System.out.println(String.format("Hello? %s", Id));
            System.out.println("1. Search");
            System.out.println("2. Favorite");
            System.out.println("3. Logout");
            System.out.print("choose one : ");
            try {
                String answer = br.readLine();
                if (answer.equals("1")) {
                    System.out.print("Enter the station name: ");
                    String stationName = br.readLine();
                    System.out.print("Enter the month(6~9): ");
                    Integer month = Integer.parseInt(br.readLine());
                    System.out.print("Enter the day(0~6): ");
                    Integer day = Integer.parseInt(br.readLine());
                    System.out.print("Enter the hour(5~24): ");
                    Integer hour = Integer.parseInt(br.readLine());
                    System.out.print("Enter the holiday(0,1): ");
                    Integer isHollyday = Integer.parseInt(br.readLine());
                    System.out.print("Enter the precipitation: ");
                    Integer precipitation = Integer.parseInt(br.readLine());
                    ResultSet rs = st.executeQuery("SELECT DISTINCT u.usetype" + ",avg(u.usage)"
                            + " FROM hourlyweather w" + " LEFT JOIN holidays h ON w.date=h.date"
                            + " LEFT JOIN station_usage u ON w.date=u.date AND w.hour=u.hour"
                            + " LEFT JOIN station s ON u.station_name=s.station_name"
                            + String.format(" WHERE s.station_name LIKE '%s'", /* 검색할 역 */stationName)
                            + String.format(" AND EXTRACT(MONTH FROM w.date)::INTEGER=%d", /* 조회 월 */month)
                            + String.format(" AND EXTRACT(DOW FROM w.date)::INTEGER=%d", /* 조회 요일(일요일: 0) */day)
                            + String.format(" AND w.hour=%d", /* 시간 */hour) + " AND h.date IS "
                            + (/* 휴일 */0 == isHollyday/* 평일:0 휴일:1 */ ? "" : "NOT") + " NULL"
                            + " GROUP BY s.exchange_station" + ",s.station_name"
                            + ",EXTRACT(MONTH FROM w.date)::INTEGER" + ",EXTRACT(DOW FROM w.date)::INTEGER" + ",w.hour"
                            + ",u.usetype" + ",precipitation"
                            + String.format(" HAVING min(abs(precipitation - %1$d)) = abs(precipitation - %1$d)",
                                    precipitation));
                    int count = 0;
                    while (rs.next()) {
                        System.out.println(String.format("%s %s", rs.getString(1), rs.getString(2)));
                        count++;
                    }
                    if (count == 0) {
                        System.out.println("not enough data for weekday = " + day + " and isholiday = " + isHollyday);
                    }

                } else if (answer.equals("2")) {
                    List<userfavoriteinfo> curfavorites;
                    while (true) {
                        curfavorites = userTool.QueryALLFavorites();
                        System.out.println("1. select one to query");
                        System.out.println("2. add to favorite");
                        System.out.println("3. delete a favorite");
                        System.out.println("4. Exit favorite");
                        System.out.print("choose one : ");
                        String action = br.readLine();
                        if (action.equals("1")) {
                            // 검색
                            System.out.print("choose number(1~ ) : ");
                            int selection = Integer.parseInt(br.readLine());
                            System.out.print("Enter the month(6~9): ");
                            Integer month = Integer.parseInt(br.readLine());
                            System.out.print("Enter the holiday(0,1): ");
                            Integer isHollyday = Integer.parseInt(br.readLine());
                            System.out.print("Enter the precipitation: ");
                            Integer precipitation = Integer.parseInt(br.readLine());
                            userfavoriteinfo curfavorite = curfavorites.get(selection - 1);
                            ResultSet rs = st.executeQuery("SELECT DISTINCT u.usetype" + ",avg(u.usage)"
                                    + " FROM hourlyweather w" + " LEFT JOIN holidays h ON w.date=h.date"
                                    + " LEFT JOIN station_usage u ON w.date=u.date AND w.hour=u.hour"
                                    + " LEFT JOIN station s ON u.station_name=s.station_name"
                                    + String.format(" WHERE s.station_name LIKE '%s'",
                                            /* 검색할 역 */curfavorite.getStation_name())
                                    + String.format(" AND EXTRACT(MONTH FROM w.date)::INTEGER=%d", /* 조회 월 */month)
                                    + String.format(" AND EXTRACT(DOW FROM w.date)::INTEGER=%d",
                                            /* 조회 요일(일요일: 0) */curfavorite.getWeekday())
                                    + String.format(" AND w.hour=%d", /* 시간 */curfavorite.getHour()) + " AND h.date IS "
                                    + (/* 휴일 */0 == isHollyday/* 평일:0 휴일:1 */ ? "" : "NOT") + " NULL"
                                    + " GROUP BY s.exchange_station" + ",s.station_name"
                                    + ",EXTRACT(MONTH FROM w.date)::INTEGER" + ",EXTRACT(DOW FROM w.date)::INTEGER"
                                    + ",w.hour" + ",u.usetype" + ",precipitation"
                                    + String.format(
                                            " HAVING min(abs(precipitation - %1$d)) = abs(precipitation - %1$d)",
                                            precipitation));

                            int count = 0;
                            while (rs.next()) {
                                System.out.println(String.format("%s %s", rs.getString(1), rs.getString(2)));
                                count++;
                            }
                            if (count == 0) {
                                System.out.println("not enough data for weekday = " + curfavorite.getWeekday()
                                        + " and isholiday = " + isHollyday);
                            }
                            rs.close();
                        } else if (action.equals("2")) {
                            // 추가
                            userfavoriteinfo newfavorite = new userfavoriteinfo();
                            newfavorite.setUserid(Id);
                            System.out.print("enter name of this favorite      : ");
                            newfavorite.setFname(br.readLine());
                            System.out.print("enter station name (2 line)      : ");
                            newfavorite.setStation_name(br.readLine());
                            System.out.print("enter weekday (0 : SUN, 6 : SAT) : ");
                            newfavorite.setWeekday(Integer.parseInt(br.readLine()));
                            System.out.print("enter hour (5 ~ 24)              : ");
                            newfavorite.setHour(Integer.parseInt(br.readLine()));
                            if (userTool.AddFavoriteInfo(newfavorite))
                                System.out.println("Add : Success");
                            else
                                System.out.println("Add : Error");

                        } else if (action.equals("3")) {
                            // 제거
                            System.out.print("select number to delete (1~): ");
                            int number = Integer.parseInt(br.readLine());
                            if (userTool.DeleteFavorite(curfavorites.get(number - 1).getFname()))
                                System.out.println("Delete : Success");
                            else
                                System.out.println("Delete : Error");

                        } else if (action.equals("4"))
                            break;

                    }
                } else if (answer.equals("3")) {
                    signed = false;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}