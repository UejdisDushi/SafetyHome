package it.upo.reti2s.utils;


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

import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
/**

/**
 * Created by lucaf on 17/07/2017.
 */
public class UtilCalendario
{
    private static final String APPLICATION_NAME = "safetyhome-173418";
    private static final java.io.File DATA_STORE_DIR =
            new java.io.File(System.getProperty("user.home"), ".credentials/calendar-java-quickstart");
    private static FileDataStoreFactory dataStoreFactory;
    private static HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static com.google.api.services.calendar.Calendar client;
    static final java.util.List<Calendar> addedCalendarsUsingBatch = Lists.newArrayList();

    public static boolean attivazioneServizio()
    {
        boolean attivaServizioMonitoraggio = false;

        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
            Credential credential = authorize();
            com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName("applicationName").build();
            com.google.api.services.calendar.model.Calendar calendar20010057 =
                    service.calendars().get("20010057@studenti.uniupo.it").execute();
            String pageToken = null;
            do {
                Events events = service.events().list("20010057@studenti.uniupo.it").setPageToken(pageToken).execute();
                List<Event> items = events.getItems();
                for (Event tmp : items)
                {
                    //tiro su l ide del corrente evento
                    System.out.println("evento = "+tmp.getSummary()+" attivare vale : "+ attivaServizio(String.valueOf(tmp.getStart()),String.valueOf(tmp.getEnd())));
                    if(attivaServizio(String.valueOf(tmp.getStart()),String.valueOf(tmp.getEnd()))==true)
                    {
                        attivaServizioMonitoraggio=true;
                        return true;
                    }
                }
                pageToken = events.getNextPageToken();
            } while (pageToken != null);


        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(1);
        return attivaServizioMonitoraggio;
    }


















    private static Credential authorize() throws Exception
    {
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


    public static boolean attivaServizio(String data_inizio,String data_fine)
    {

        System.out.println("INIZIO METODO ATTIVA SERVIZIO \n\n");
        //System.out.println("lunghezza data inizio: "+ data_inizio.length() + " lunghezza data fine: " +data_fine.length());ù




        Date now = new Date(java.util.Calendar.getInstance().getTime().getTime());
        DateTime now_convertita = new DateTime(now);

        Boolean attivareServizio = false;
        Boolean eventoGiornata = false;

        if(data_inizio.length()==21)
        {
            eventoGiornata=true;
        }


        //Prendo la stringa della data di partenza
        //String data_partenza = String.valueOf(tmp.getStart()).substring(13,42);//<--formato ok


        //System.out.println(now_convertita);
        String data_now = now_convertita.toString();
        String data_inizioEvento = data_inizio;
        String data_fineEvento = data_fine;


        System.out.println("DATA NOW VALE: "+data_now+"\n DATA INIZIO VALE : "+data_inizioEvento+"\n DATA FINE VALE : "+data_fineEvento+"\n\n");


        //Data separata Evento now generato
        int aaaa_now = 0;
        int mm_now = 0;
        int gg_now = 0;
        int hh_now = 0;
        int min_now = 0;
        int sec_now = 0;







        //variabili di partenza
        int aaaa_inizioEvento=0;
        int mm_inizioEvento=0;
        int gg_inizioEvento=0;
        int hh_inizioEvento=0;
        int min_inizioEvento=0;
        int sec_inizioEvento=0;

        int aaaa_fineEvento=0;
        int mm_fineEvento=0;
        int gg_fineEvento=0;
        int hh_fineEvento=0;
        int min_fineEvento=0;
        int sec_fineEvento=0;



        //parsing data now passa
        if(data_fineEvento.length()==21)
        {
            aaaa_now = Integer.valueOf(data_now.substring(0,4));
            mm_now = Integer.valueOf(data_now.substring(5,7));
            gg_now = Integer.valueOf(data_now.substring(8,10));
            //System.out.println("DATA NOW SEPARATA = "+aaaa_now+" "+mm_now+" "+gg_now);
        }
        else//data vale 44
        {
            aaaa_now = Integer.valueOf(data_now.substring(0,4));
            mm_now = Integer.valueOf(data_now.substring(5,7));
            gg_now = Integer.valueOf(data_now.substring(8,10));
            hh_now = Integer.valueOf(data_now.substring(11,13));
            min_now=Integer.valueOf(data_now.substring(14,16));
            sec_now = Integer.valueOf(data_now.substring(17,19));
            //System.out.println("DATA NOW SEPARATA = "+aaaa_now+" "+mm_now+" "+gg_now+" "+hh_now+" "+min_now+" "+sec_now);
        }


        //Data separata Evento inizio PASSA
        if(data_inizioEvento.length()==21)//evento che dura una giornata
        {
            eventoGiornata= true;
            //System.out.println("data di 21");
            aaaa_inizioEvento = Integer.valueOf(data_inizioEvento.substring(9,13));
            mm_inizioEvento = Integer.valueOf(data_inizioEvento.substring(14,16));
            gg_inizioEvento = Integer.valueOf(data_inizioEvento.substring(17,19));
            //System.out.println("DATA INIZIO EVENTO SEPARATA = "+aaaa_inizioEvento+" "+mm_inizioEvento+" "+gg_inizioEvento);
        }
        else//evento con data inizio NON PASSA
        {

            aaaa_inizioEvento = Integer.valueOf(data_inizioEvento.substring(13,17));
            mm_inizioEvento = Integer.valueOf(data_inizioEvento.substring(18,20));
            gg_inizioEvento = Integer.valueOf(data_inizioEvento.substring(21,23));
            hh_inizioEvento = Integer.valueOf(data_inizioEvento.substring(24,26));
            min_inizioEvento =Integer.valueOf(data_inizioEvento.substring(27,29));
            sec_inizioEvento = Integer.valueOf(data_inizioEvento.substring(30,32));
            //System.out.println("DATA INIZIO EVENTO SEPARATA = "+aaaa_inizioEvento+" "+mm_inizioEvento+" "+gg_inizioEvento+" "+hh_inizioEvento+" "+min_inizioEvento+" "+sec_inizioEvento);
        }




        //Data separata Evento fine non passa
        if(data_fineEvento.length()==21)//evento che dura una giornata
        {
            eventoGiornata= true;
            aaaa_fineEvento = Integer.valueOf(data_fineEvento.substring(9,13));
            mm_fineEvento = Integer.valueOf(data_fineEvento.substring(14,16));
            gg_fineEvento = Integer.valueOf(data_fineEvento.substring(17,19));
            //System.out.println("DATA FINE EVENTO SEPARATA = "+aaaa_fineEvento+" "+mm_fineEvento+" "+gg_fineEvento);
        }
        else
        {
            aaaa_fineEvento = Integer.valueOf(data_fineEvento.substring(13,17));
            mm_fineEvento = Integer.valueOf(data_fineEvento.substring(18,20));
            gg_fineEvento = Integer.valueOf(data_fineEvento.substring(21,23));
            hh_fineEvento = Integer.valueOf(data_fineEvento.substring(24,26));
            min_fineEvento = Integer.valueOf(data_fineEvento.substring(27,29));
            sec_fineEvento = Integer.valueOf(data_fineEvento.substring(30,32));
            //System.out.println("DATA FINE EVENTO SEPARATA = "+aaaa_fineEvento+" "+mm_fineEvento+" "+gg_fineEvento+" "+hh_fineEvento+" "+min_fineEvento+" "+sec_fineEvento);
        }

        //System.out.println(" inizio verifica - evento giornata vale: "+eventoGiornata);




        if(eventoGiornata==true)
        {
            if (aaaa_now >=aaaa_inizioEvento && aaaa_now<=aaaa_fineEvento)//controllo anni
            {
                if (mm_now>=mm_inizioEvento && mm_now<=mm_fineEvento)//controllo mese
                {
                    if (gg_now>=gg_inizioEvento && gg_now<=gg_fineEvento)//controllo giorni
                    {
                        attivareServizio = true;
                        //System.out.println("attivare il servizio vale: "+attivareServizio);
                        return true;
                    }

                }
            }
        }
        else//controllo se è un evento giornata
        {
            if(aaaa_now>=aaaa_inizioEvento && aaaa_now<=aaaa_fineEvento)//controllo anni
            {
                if(mm_now>= mm_inizioEvento && mm_now<=mm_fineEvento)//controllo mese
                {
                    if(gg_now>=gg_inizioEvento && gg_now <=gg_fineEvento)//controllo giorni
                    {
                        if(hh_now>=hh_inizioEvento && hh_now<=hh_fineEvento)//controllo ora
                        {
                            if(min_now>= min_inizioEvento && min_now<=min_fineEvento)//controllo minuti
                            {
                                if(sec_now >= sec_inizioEvento && sec_now<=sec_fineEvento)
                                {
                                    attivareServizio = true;
                                    //System.out.println(attivareServizio);
                                    return true;
                                }

                            }//fine controllo minuti

                        }//fine controllo ora

                    }//fine controllo giorni

                }//fine controllo mese
            }//fine controllo anni
            //System.out.println("aaainizio vale"+aaaa_inizioEvento+" fine evento vale "+aaaa_fineEvento+" aanow vale "+aaaa_now);
            // System.out.println("-------------------------------------------------------");
            //return attivareServizio;
            return false;


        }

        return false;
    }



}
