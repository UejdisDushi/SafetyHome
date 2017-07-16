package it.upo.reti2s.utils;

/**
 * Created by Luca Franciscone on 13/07/2017.
 */
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.*;
import com.google.api.services.calendar.model.*;
import com.google.api.services.calendar.model.Calendar;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;





/**
 * @author Yaniv Inbar
 */
public class CalendarSample {

    /**
     * Be sure to specify the name of your application. If the application name is {@code null} or
     * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
     */
    private static final String APPLICATION_NAME = "safetyhome-173418";

    /** Directory to store user credentials. */
    private static final java.io.File DATA_STORE_DIR =
           //new java.io.File(System.getProperty("user.home"), ".store/calendar_sample");
           new java.io.File(System.getProperty("user.home"), ".credentials/calendar-java-quickstart");
          //  new java.io.File(System.getProperty("user.home"), ".credentials/calendar-java-quickstart");

    /**
     * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
     * globally shared instance across your application.
     */
    private static FileDataStoreFactory dataStoreFactory;

    /** Global instance of the HTTP transport. */
    private static HttpTransport httpTransport;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static com.google.api.services.calendar.Calendar client;

    static final java.util.List<Calendar> addedCalendarsUsingBatch = Lists.newArrayList();

    /** Authorizes the installed application to access user's protected data. */
    private static Credential authorize() throws Exception {
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(CalendarSample.class.getResourceAsStream("/client_secrets.json")));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println(
                    "Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar "
                            + "into calendar-cmdline-sample/src/main/resources/client_secrets.json");
            System.exit(1);
        }
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets,
                Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(dataStoreFactory)
                .build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static void main(String[] args) {
        try {
            // initialize the transport
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            // initialize the data store factory
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
            // authorization
            Credential credential = authorize();
            // set up global Calendar instance
            client = new com.google.api.services.calendar.Calendar.Builder(
                    httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
            // ------------------------run commands

            /*
            showCalendars();
            addCalendarsUsingBatch();
            Calendar calendar = addCalendar();
            updateCalendar(calendar);
            addEvent(calendar);
            showEvents(calendar);
            deleteCalendarsUsingBatch();
            deleteCalendar(calendar);
            */

            // Initialize Calendar service with valid OAuth credentials

            com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName("applicationName").build();

// Retrieve the calendar
            com.google.api.services.calendar.model.Calendar calendar =
                    service.calendars().get("20010057@studenti.uniupo.it").execute();

            System.out.println(calendar.getSummary());
            //Java API GUIDA
            //https://developers.google.com/google-apps/calendar/v3/reference/calendarList/get

            // raccessRole	string freeBusyReade
/*
            // Metodo usato per recuperare un evento con un dato id
            Event event = service.events().get("primary", "eventId").execute();
            System.out.println(event.getSummary());
*/

            // Metodo usato per iterare tra vari eventi in un calendario specifico
            String pageToken = null;
            do {
                Events events = service.events().list("20010057@studenti.uniupo.it").setPageToken(pageToken).execute();
                List<Event> items = events.getItems();
                for (Event event1 : items) {
                    //tiro su l ide del corrente evento
                    String eventId = event1.getId();






                    System.out.println("ID DELL'EVENTO: "+eventId+" RISULTATO GET SUMMARY : "+event1.getSummary());
                    System.out.println("Inizio dell'evento : "+String.valueOf(event1.getStart()));
                    System.out.println("Fine dell'evento : "+String.valueOf(event1.getEnd()));

                    //java.util.GregorianCalendar now = new java.util.GregorianCalendar();
                    //System.out.println(now);

                   // int anno = now.YEAR;
                   // int mese = now.MONTH;

                    EventDateTime immediato = new EventDateTime();

                    //Formato della data sdf guardare qui
                    //https://stackoverflow.com/questions/30431762/setting-date-in-google-calendar-event



                    System.out.println(immediato);

                    // int ora = now.getTime().getHours();
                    //int minuti = now.getTime().getMinutes();
                   // int secondi =  now.getTime().getSeconds();;
                   // System.out.println( anno + " "+mese+" "+giorno);


                    //System.out.print(eventEnd.toString()+"Inizio dell'evento");
                    //System.out.print(eventEnd.toString()+"Fine dell'evento");

                   // Date date = new Date(String.valueOf(event1.getStart()));//no bene
                    //System.out.println(date);
                   //String dd=String.valueOf(event1.getStart());
                   //java.util.Calendar dataConvertitaInizio = dataConvertita(String.valueOf(event1.getStart()));
                  // java.util.Calendar dataConvertitaFine = dataConvertita(String.valueOf(event1.getEnd()));
                   //System.out.println(dataConvertitaInizio.getTime());


                    //System.out.println("\n\n\n\n metodo freebusy \n\n");




                   /*TEST METODO OCCUPATO O NO */

                   //System.out.println("STRINGA FINALE \n"+convertiPerDataFreeBusy(eventStart));
                    //https://stackoverflow.com/questions/32632560/how-do-i-use-the-freebusyresponse-in-google-calendar-java-api
                    //Da {"dateTime":"2017-07-15T21:30:00.000+02:00"}
                    //A  Esempio String input "2015-09-10 19:00:00";
                    //String dIn = convertiPerDataFreeBusy(eventStart);
                    //String dIne = convertiPerDataFreeBusy(eventStart);
/*
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    //Date d = df.parse(dIn);
                    Date d = df.parse("2017-07-15 10:00:00");
                    DateTime startTime = new DateTime(d, TimeZone.getDefault());

                    //Date de = df.parse(dIne);
                    Date de = df.parse("2017-07-15 23:00:00");
                    DateTime endTime = new DateTime(de, TimeZone.getDefault());

                    FreeBusyRequest req = new FreeBusyRequest();
//https://github.com/google/google-api-dotnet-client/issues/288
                    FreeBusyRequestItem calid = new FreeBusyRequestItem();
                    calid.setId("20010057@studenti.uniupo.it");
                    req.setTimeMin(startTime);
                    req.setTimeMax(endTime);

                    com.google.api.services.calendar.Calendar.Freebusy.Query fbq = client.freebusy().query(req);

                    FreeBusyResponse fbresponse = fbq.execute();
                    System.out.println(fbresponse.toString());
                    */

                }
                pageToken = events.getNextPageToken();
            } while (pageToken != null);


        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(1);
    }

    private static void showCalendars() throws IOException {
        View.header("Show Calendars");
        CalendarList feed = client.calendarList().list().execute();
        View.display(feed);
    }

    private static void addCalendarsUsingBatch() throws IOException {
        View.header("Add Calendars using Batch");
        BatchRequest batch = client.batch();

        // Create the callback.
        JsonBatchCallback<Calendar> callback = new JsonBatchCallback<Calendar>() {

            @Override
            public void onSuccess(Calendar calendar, HttpHeaders responseHeaders) {
                View.display(calendar);
                addedCalendarsUsingBatch.add(calendar);
            }

            @Override
            public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
                System.out.println("Error Message: " + e.getMessage());
            }
        };

        // Create 2 Calendar Entries to insert.
        Calendar entry1 = new Calendar().setSummary("Calendar for Testing 1");
        client.calendars().insert(entry1).queue(batch, callback);

        Calendar entry2 = new Calendar().setSummary("Calendar for Testing 2");
        client.calendars().insert(entry2).queue(batch, callback);

        batch.execute();
    }

    private static Calendar addCalendar() throws IOException {
        View.header("Add Calendar");
        Calendar entry = new Calendar();
        entry.setSummary("Calendar for Testing 3");
        Calendar result = client.calendars().insert(entry).execute();
        View.display(result);
        return result;
    }

    private static Calendar updateCalendar(Calendar calendar) throws IOException {
        View.header("Update Calendar");
        Calendar entry = new Calendar();
        entry.setSummary("Updated Calendar for Testing");
        Calendar result = client.calendars().patch(calendar.getId(), entry).execute();
        View.display(result);
        return result;
    }


    private static void addEvent(Calendar calendar) throws IOException {
        View.header("Add Event");
        Event event = newEvent();
        Event result = client.events().insert(calendar.getId(), event).execute();
        View.display(result);
    }

    private static Event newEvent() {
        Event event = new Event();
        event.setSummary("New Event");
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 3600000);
        DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
        event.setStart(new EventDateTime().setDateTime(start));
        DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
        event.setEnd(new EventDateTime().setDateTime(end));
        return event;
    }

    private static void showEvents(Calendar calendar) throws IOException {
        View.header("Show Events");
        Events feed = client.events().list(calendar.getId()).execute();
        View.display(feed);
    }

    private static void deleteCalendarsUsingBatch() throws IOException {
        View.header("Delete Calendars Using Batch");
        BatchRequest batch = client.batch();
        for (Calendar calendar : addedCalendarsUsingBatch) {
            client.calendars().delete(calendar.getId()).queue(batch, new JsonBatchCallback<Void>() {

                @Override
                public void onSuccess(Void content, HttpHeaders responseHeaders) {
                    System.out.println("Delete is successful!");
                }

                @Override
                public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
                    System.out.println("Error Message: " + e.getMessage());
                }
            });
        }

        batch.execute();
    }

    private static void deleteCalendar(Calendar calendar) throws IOException {
        View.header("Delete Calendar");
        client.calendars().delete(calendar.getId()).execute();
    }

    public static java.util.Calendar dataConvertita(String dataInStringa)
    {
        String data_fisica = dataInStringa.substring(13,23);
        String orario = dataInStringa.substring(24,29);
        System.out.println(orario);
        String nuovaOra = data_fisica.concat(" "+orario);
        System.out.println(nuovaOra);
        int anno = Integer.parseInt(nuovaOra.subSequence(0,4).toString());
        int mese = Integer.parseInt(nuovaOra.subSequence(5,7).toString());
        int giorno = Integer.parseInt(nuovaOra.subSequence(8,10).toString());
        //System.out.println(anno);
        //System.out.println(mese);
        //System.out.println(giorno);
        //System.out.println("\n\n"+orario+"\n\n");

        int ora = Integer.parseInt(orario.subSequence(0,2).toString());
        int minuti = Integer.parseInt(orario.subSequence(3,5).toString());
        //System.out.println(ora + "    " + minuti);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.YEAR, anno);
        cal.set(java.util.Calendar.MONTH, mese - 1); // <-- months start
        cal.set(java.util.Calendar.DAY_OF_MONTH, giorno);
        cal.set(java.util.Calendar.HOUR_OF_DAY,ora);
        cal.set(java.util.Calendar.MINUTE,minuti);
        cal.set(java.util.Calendar.SECOND,0);
        //System.out.println(cal.getTime());
        return cal;
    }

    public static java.util.Calendar dataFreeBusy(String dataInStringa)
    {

        String data_fisica = dataInStringa.substring(13,23);

        String orario = dataInStringa.substring(24,29);
        System.out.println(orario);
        String nuovaOra = data_fisica.concat(" "+orario);
        System.out.println(nuovaOra);
        int anno = Integer.parseInt(nuovaOra.subSequence(0,4).toString());
        int mese = Integer.parseInt(nuovaOra.subSequence(5,7).toString());
        int giorno = Integer.parseInt(nuovaOra.subSequence(8,10).toString());
        //System.out.println(anno);
        //System.out.println(mese);
        //System.out.println(giorno);
        //System.out.println("\n\n"+orario+"\n\n");

        int ora = Integer.parseInt(orario.subSequence(0,2).toString());
        int minuti = Integer.parseInt(orario.subSequence(3,5).toString());
        //System.out.println(ora + "    " + minuti);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.YEAR, anno);
        cal.set(java.util.Calendar.MONTH, mese - 1); // <-- months start
        cal.set(java.util.Calendar.DAY_OF_MONTH, giorno);
        cal.set(java.util.Calendar.HOUR_OF_DAY,ora);
        cal.set(java.util.Calendar.MINUTE,minuti);
        cal.set(java.util.Calendar.SECOND,0);
        //System.out.println(cal.getTime());
        return cal;
    }

    public static String convertiPerDataFreeBusy(String dataInStringa)
    {
        //Da {"dateTime":"2017-07-15T21:30:00.000+02:00"}
        //A  Esempio String input "2015-09-10 19:00:00";
        String data_fisica = dataInStringa.substring(13,23);
        String orario = dataInStringa.substring(24,29);
        String anno = data_fisica.subSequence(0,4).toString();
        String mese = data_fisica.subSequence(5,7).toString();
        String giorno = data_fisica.subSequence(8,10).toString();
        String ora = orario.subSequence(0,2).toString();
        String minuti = orario.subSequence(3,5).toString();
        String finale = anno+"-"+mese+"-"+giorno+" "+ora+":"+minuti+":"+"00";
        return finale;
    }
}
