package com.grokkingandroid.sampleapp.samples.gcm.ccs.server;

/**
 * Created by junyin on 12/14/15.
 */

import org.jivesoftware.smack.XMPPException;

import java.util.*;
import java.net.*;
import javax.json.*;
import java.io.InputStream;



public class BlueBusTrackerServer {
    class Stop {
        int id;
        double lat;
        double lon;

        public Stop(int id, double lat, double lon) {
            this.id = id;
            this.lat = lat;
            this.lon = lon;
        }
    }
    class Node {
        double lon;
        double lat;
        String depatureTime;
        int duration_time;   //seconds
        long walking_time;
        long busDepatureTime;
        String busName;
    }
    List<Stop> stopList;
    HashMap<String, Integer> routeMap;
    HashMap<String, String> busNameMap;
    List<Node> choices;
    public static long mId = 0;
    int status = 0;
    public long busArrivalTime = 0;
    public boolean hasBus = false;
    public int targetArrivalTime = 0;

    public BlueBusTrackerServer() {
        getAllStops();
        getAllRoute();
    }

    private void getAllStops() {
        stopList = new ArrayList<Stop>();
        URL url;
        InputStream is = null;

        try {
            url = new URL("http://mbus.doublemap.com/map/v2/stops");
            is = url.openStream();
        } catch (Exception e) {
            System.out.println("Invalid get all stops url\n");
        }

        JsonReader rdr = Json.createReader(is);
        JsonArray results = rdr.readArray();

        for (JsonObject result : results.getValuesAs(JsonObject.class))
            stopList.add(new Stop(result.getJsonNumber("id").intValue(),
                    result.getJsonNumber("lat").doubleValue(),
                    result.getJsonNumber("lon").doubleValue()));
    }

    private void getAllRoute() {
        routeMap = new HashMap<>();
        busNameMap = new HashMap<>();

        URL url;
        InputStream is = null;

        try {
            url = new URL("http://mbus.doublemap.com/map/v2/routes");
            is = url.openStream();
        } catch (Exception e) {
            System.out.println("Invalid get all routes url\n");
        }

        JsonReader rdr = Json.createReader(is);
        JsonArray results = rdr.readArray();

        for (JsonObject result : results.getValuesAs(JsonObject.class)) {
            routeMap.put(result.getJsonString("name").toString(), result.getJsonNumber("id").intValue());
            busNameMap.put(result.getJsonString("name").toString(), result.getJsonString("short_name").toString());
        }
    }

    public List<Integer> getArrivalTime(int stopId, int routeId) {
        System.out.println("stopId : " + stopId + "\t" +  "routeId : " + routeId);
        List<Integer> arrivalTime = new ArrayList<>();
        URL url;
        InputStream is = null;
        try {
            url = new URL("http://mbus.doublemap.com/map/v2/eta?stop="+stopId);
            is = url.openStream();
        } catch (Exception e) {
            System.out.println("Invalid get eta url\n");
        }

        JsonReader rdr = Json.createReader(is);
        JsonObject tmp = rdr.readObject();
        tmp = tmp.getJsonObject("etas");
        tmp = tmp.getJsonObject("" + stopId);
        JsonArray results = tmp.getJsonArray("etas");
        for (JsonObject result : results.getValuesAs(JsonObject.class)) {
            if (result.getJsonNumber("route").intValue() == routeId)
                arrivalTime.add(result.getJsonNumber("avg").intValue());
        }
        return arrivalTime;
    }

    public int findDepartureStop (double lat, double lon) {
        double gap = Double.MAX_VALUE;
        int res = -1;
        for (Stop tmp : stopList) {
            double cur = (tmp.lat - lat) * (tmp.lat - lat) + (tmp.lon - lon) * (tmp.lon - lon);
            if (cur < gap) {
                gap = cur;
                res = tmp.id;
            }
        }
        System.out.println("Find Departure Stop exit");
        return res;
    }

    public void queryGoogle(String origin, String destination, int arrival_time) {
        URL url;
        InputStream is = null;
        choices= new ArrayList<Node>();
        String s1 = "https://maps.googleapis.com/maps/api/directions/json?origin=";
        String s2 = "&destination=";
        String s3 = ",Ann Arbor,MI&mode=transit&arrival_time="+arrival_time+"&alternatives=true&transit_routing_preference=fewer_transfers&key=AIzaSyBS5etu4BTZUHhEcjVlMdfJGOklovs_N1o";

        String urlString = s1 + origin + s2 + destination + s3;
        String encodedUrl = null;
        encodedUrl = urlString.replaceAll(" ", "%20");
        System.out.println(encodedUrl);
        try {
            url = new URL(encodedUrl);
            //url = new URL ("https://maps.googleapis.com/maps/api/directions/json?origin=Pierpont%20Commons,Ann%20Arbor,MI&destination=Central%20Campus%20Transit%20Center,Ann%20Arbor,MI&mode=transit&alternatives=true&transit_routing_preference=fewer_transfers&key=AIzaSyBS5etu4BTZUHhEcjVlMdfJGOklovs_N1o");
            is = url.openStream();
        } catch (Exception e) {
            System.out.println("Invalid get Google Map url\n");
        }

        JsonReader rdr = Json.createReader(is);
        JsonObject tmp = rdr.readObject();
        JsonArray routes = tmp.getJsonArray("routes");
        for (JsonObject r : routes.getValuesAs(JsonObject.class)) {
            JsonArray legs = r.getJsonArray("legs");
            for (JsonObject l : legs.getValuesAs(JsonObject.class)) {
                Node cur = new Node();
                cur.depatureTime = l.getJsonObject("departure_time").getJsonString("text").toString();
                cur.duration_time = l.getJsonObject("duration").getJsonNumber("value").intValue();
                JsonArray steps = l.getJsonArray("steps");
                if (steps.size() < 1)
                    System.out.println("error - steps < 1");
                JsonObject step1 = steps.getJsonObject(0);
                JsonObject step2 = null;
                if(step1.getJsonString("travel_mode").toString().equals("\"WALKING\"")) {
                    cur.walking_time = step1.getJsonObject("duration").getJsonNumber("value").longValue();
                    step2 = steps.getJsonObject(1);
                } else {
                    cur.walking_time = 0;
                    step2 = step1;
                }
                if (step2.getJsonString("travel_mode").equals("\"TRANSIT\"")) {
                    System.out.println("Error is not transit");
                }
                JsonObject transit_details = step2.getJsonObject("transit_details");
                cur.lat = transit_details.getJsonObject("departure_stop")
                        .getJsonObject("location").getJsonNumber("lat").doubleValue();
                cur.lon = transit_details.getJsonObject("departure_stop")
                        .getJsonObject("location").getJsonNumber("lng").doubleValue();
                cur.busDepatureTime = transit_details.getJsonObject("departure_time")
                        .getJsonNumber("value").longValue();
                cur.busName = transit_details.getJsonObject("line").getJsonString("name").toString();
                JsonArray agencies = transit_details.getJsonObject("line").getJsonArray("agencies");
                for (JsonObject agent : agencies.getValuesAs(JsonObject.class)) {
                    if (agent.getJsonString("name").toString().equals("\"University of Michigan Transit Services\"")) {
                        choices.add(cur);
                        break;
                    }
                }
            }
        }
    }

    public String findChoices(String GPS, String Destination, int hour, int min) {
        Calendar now = Calendar.getInstance();
        Date date = new Date(now.get(Calendar.YEAR) - 1900, now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH), hour, min);
        int arrival_time = (int)(date.getTime()/1000);
        targetArrivalTime = arrival_time;
        queryGoogle(GPS, Destination, arrival_time);
        String res = ++mId + "|" + "BusOpt";
        for (int i = 0; i < 3 && i < choices.size(); ++i) {
            String busName = busNameMap.get(choices.get(i).busName);
            String departTime = choices.get(i).depatureTime;
            res = res + "|" + busName.substring(1, busName.length()-1) + " " + departTime;
        }
        System.out.println(res + "|");
        return res + "|";
    }

    public boolean toNotify(List<Integer> arrivalTime, long walkTime, int duration_time) {
        long currentTime = System.currentTimeMillis() / 1000;
        long factor = 180; //reserve additional 120 sec
        System.out.println("walkTime time : " + walkTime + "\t" + "duration_time : " + duration_time);
        System.out.println("targetArrivalTime: " + targetArrivalTime);
        System.out.println("currentTime: " + currentTime);
        System.out.println("busNextArrival time");
        for (int tmp : arrivalTime) {
            System.out.print(tmp + "\t");
        }
        System.out.print("\n");
        //离预计出发时间半小时以上，不通知。
        if (targetArrivalTime - duration_time - currentTime - factor> 1800)
            return false;

        busArrivalTime = 0;
        for (Integer arrival : arrivalTime) {
            long curBusArrivalTime = currentTime + arrival * 60;
            if (curBusArrivalTime + duration_time < targetArrivalTime) {
                if (curBusArrivalTime >= walkTime + currentTime) {
                    busArrivalTime = curBusArrivalTime;
                }
            } else {
                break;
            }
        }

        if (busArrivalTime != 0) {
            hasBus = true;
        } else if (hasBus) {
            if (!arrivalTime.isEmpty())
                busArrivalTime = currentTime + arrivalTime.get(0) * 60;
        }

        System.out.println("busNextArrival time" + busArrivalTime );
        if (busArrivalTime == 0 || Math.abs(busArrivalTime - walkTime - currentTime) < factor)
            return true;
        else
            return false;
    }

    public static void main(String[] args) {
    }
}
