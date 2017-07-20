package it.upo.reti2s.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.*;
import com.google.api.services.calendar.model.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class UtilCalendario {
    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/calendar-java-quickstart");
    private static FileDataStoreFactory dataStoreFactory;
    private static HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();


    public static boolean attivazioneServizio() {
        boolean attivaServizioMonitoraggio = false;

        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
            Credential credential = authorize();
            com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName("applicationName").build();
            String pageToken = null;
            do {
                Events events = service.events().list("20010057@studenti.uniupo.it").setPageToken(pageToken).execute();
                List<Event> items = events.getItems();
                for (Event tmp : items){
                    //tiro su l ide del corrente evento
                    System.out.println("evento = "+tmp.getSummary()+" attivare vale : "+ attivazioneServizioInterno(String.valueOf(tmp.getStart()),String.valueOf(tmp.getEnd())));
                    if(attivazioneServizioInterno(String.valueOf(tmp.getStart()),String.valueOf(tmp.getEnd()))) {
                        attivaServizioMonitoraggio=true;
                        // return true;  secondo me può essere cancellato, in ogni caso fai return sotto
                    }
                }
                if(items.size()==0) return false;
                pageToken = events.getNextPageToken();
            } while (pageToken != null);
        }
        catch (IOException e) {
            //System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        //System.exit(1);           secondo me può essere cancellato, in ogni caso fa il return sotto
        return attivaServizioMonitoraggio;
    }

    private static Credential authorize() throws Exception
    {
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(UtilCalendario.class.getResourceAsStream("/client_secrets.json")));
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

    /**
     * Controlla a partire da un evento nel calendario se l'ora attuale si interseca con quel evento
     * @param data_inizio inizio evento
     * @param data_fine fine evento
     * @return True se interseca || False se non interseca
     */
    private static boolean attivazioneServizioInterno(String data_inizio, String data_fine) {
        Date now = new Date(java.util.Calendar.getInstance().getTime().getTime());
        DateTime now_convertita = new DateTime(now);
        Boolean eventoGiornata = false;
        if(data_inizio.length()==21)
            eventoGiornata=true;

        String data_now = now_convertita.toString();
        String data_inizioEvento = data_inizio;
        String data_fineEvento = data_fine;
        //System.out.println("DATA NOW VALE: "+data_now+"\n DATA INIZIO VALE : "+data_inizioEvento+"\n DATA FINE VALE : "+data_fineEvento+"\n\n");

        //Data separata Evento now generato
        int aaaa_now, mm_now, gg_now, hh_now = 0, min_now = 0, sec_now = 0;

        //INIZIALIZZAZIONE VARIABILI INIZIO EVENTO
        int aaaa_inizioEvento, mm_inizioEvento, gg_inizioEvento, hh_inizioEvento = 0, min_inizioEvento = 0, sec_inizioEvento = 0;

        //INIZIALIZZAZIONE VARIABILI FINE EVENTO
        int aaaa_fineEvento, mm_fineEvento, gg_fineEvento, hh_fineEvento = 0;

        if(eventoGiornata){   //21
            aaaa_now = Integer.valueOf(data_now.substring(0,4));
            mm_now = Integer.valueOf(data_now.substring(5,7));
            gg_now = Integer.valueOf(data_now.substring(8,10));

            aaaa_inizioEvento = Integer.valueOf(data_inizioEvento.substring(9,13));
            mm_inizioEvento = Integer.valueOf(data_inizioEvento.substring(14,16));
            gg_inizioEvento = Integer.valueOf(data_inizioEvento.substring(17,19));

            aaaa_fineEvento = Integer.valueOf(data_fineEvento.substring(9,13));
            mm_fineEvento = Integer.valueOf(data_fineEvento.substring(14,16));
            gg_fineEvento = Integer.valueOf(data_fineEvento.substring(17,19));
        }
        else{    //EVENTO INIZIO FINE LUNGHEZZA 44
            aaaa_now = Integer.valueOf(data_now.substring(0,4));
            mm_now = Integer.valueOf(data_now.substring(5,7));
            gg_now = Integer.valueOf(data_now.substring(8,10));
            hh_now = Integer.valueOf(data_now.substring(11,13));
            min_now=Integer.valueOf(data_now.substring(14,16));
            sec_now = Integer.valueOf(data_now.substring(17,19));

            aaaa_inizioEvento = Integer.valueOf(data_inizioEvento.substring(13,17));
            mm_inizioEvento = Integer.valueOf(data_inizioEvento.substring(18,20));
            gg_inizioEvento = Integer.valueOf(data_inizioEvento.substring(21,23));
            hh_inizioEvento = Integer.valueOf(data_inizioEvento.substring(24,26));
            min_inizioEvento =Integer.valueOf(data_inizioEvento.substring(27,29));
            sec_inizioEvento = Integer.valueOf(data_inizioEvento.substring(30,32));

            aaaa_fineEvento = Integer.valueOf(data_fineEvento.substring(13,17));
            mm_fineEvento = Integer.valueOf(data_fineEvento.substring(18,20));
            gg_fineEvento = Integer.valueOf(data_fineEvento.substring(21,23));
            hh_fineEvento = Integer.valueOf(data_fineEvento.substring(24,26));
        }

        if(eventoGiornata)
            if (aaaa_now >=aaaa_inizioEvento && aaaa_now<=aaaa_fineEvento)
                if (mm_now>=mm_inizioEvento && mm_now<=mm_fineEvento)
                    if (gg_now>=gg_inizioEvento && gg_now<=gg_fineEvento)
                        return true;

        else       //controllo se è NON è un evento giornata
            if(aaaa_now>=aaaa_inizioEvento && aaaa_now<=aaaa_fineEvento)
                if(mm_now>= mm_inizioEvento && mm_now<=mm_fineEvento)
                    if(gg_now>=gg_inizioEvento && gg_now <=gg_fineEvento)
                        if(hh_now>=hh_inizioEvento && hh_now<=hh_fineEvento)
                            if(min_now>= min_inizioEvento)
                                if(sec_now >= sec_inizioEvento)
                                    return true;

        return false;
    }
}
