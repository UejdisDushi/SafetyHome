package it.upo.reti2s.utils;

import java.util.*;
import java.io.*;

import ai.api.GsonFactory;
import ai.api.model.AIResponse;
import ai.api.model.Fulfillment;
import com.github.sarxos.webcam.Webcam;
import com.google.gson.Gson;
import de.fh_zwickau.informatik.sensor.IZWayApi;
import de.fh_zwickau.informatik.sensor.ZWayApiHttp;
import de.fh_zwickau.informatik.sensor.model.devices.Device;
import de.fh_zwickau.informatik.sensor.model.devices.DeviceList;
import it.upo.reti2s.utils.ZWayLib.ZWaySimpleCallback;
import jdk.internal.util.xml.impl.Input;

import javax.imageio.ImageIO;
import java.io.*;

import static it.upo.reti2s.utils.Util.*;
import static spark.Spark.*;


import java.io.IOException;

/**
 * api.ai Webhook example.
 * It gets all tasks, a specified task from a database and provides the information to
 * the api.ai service. It also allows users to create a new task, through the api.ai
 * conversational interface.
 *
 * @author <a href="mailto:luigi.derussis@uniupo.it">Luigi De Russis</a>
 * @version 1.0 (21/05/2017)
 */
public class SafetyHomeWebhook
{
    //Settaggi cam
    final static String PATH_IMMAGINE = "Images/prova.png";
    final static String FORMATO_IMMAGINE = "PNG";
    final static String PATH_IMMAGINE_DROPBOX = "https://www.dropbox.com/s/v7arilbs00h4849/prova.png?dl=0";

    // Identificatori singoli Device
    final static int ID_APERTURAPORTE = 13;//CORRETTO
    final static int ID_PRESAPILOTATA = 3;// CORRETTO PRESA PILOTATA PER RADIO
    final static int ID_HOLDERLAMPADINA = 21;//CORRETTO EVERSPRING WALL PLUG
    final static int ID_MULTILEVEL_PURPOSE = 6;//CORRETTO DA USARE PER PURPOSE E LUMINOSITA

    //Nome del getProbeTitle() del Device
    final static String SWITCHBINARY = "SwitchBinary";
    final static String SENSORBINARY = "SensorBinary";
    final static String MULTILEVEL_LUMINESCENCE = "Luminiscence";
    final static String MULTILEVEL_PURPOSE = "purpose";

    //Settaggi connessione rete Zway
    final static String ipAddress = "172.30.1.137";
    final static String username = "admin";
    final static String password = "raz4reti2";
    final static IZWayApi zWayApi = new ZWayApiHttp(ipAddress, 8083, "http", username, password, 0, false, new ZWaySimpleCallback());

    //Settaggi per utilizzo Telegram
    static final String TELEGRAM_URL = "https://api.telegram.org/bot423930159:AAF3ES_GcBxl5HmrV5HdfF137_XCfLXc1ZU";//link del bot
    //composto in questa maniera  https://api.telegram.org/bot
    //token 423930159:AAF3ES_GcBxl5HmrV5HdfF137_XCfLXc1ZU

    static final String TELEGRAM_TOKEN = "423930159:AAF3ES_GcBxl5HmrV5HdfF137_XCfLXc1ZU";//token accesso bothfather
    static final long TELEGRAM_RESPONSE_CHAT_ID = 102856586;//id/username Luca
    static final long TELEGRAM_RESPONSE_CHAT_ID_EDI = 128905829;//id/username Edi

    public static void main(String[] args) throws IOException {
        boolean avviaCal = false;
        BufferedReader input = new BufferedReader (new InputStreamReader(System.in));

        System.out.println("Inserisca y per avvisare il servizio di monitoraggio da calendario : \n");
        String s = (input.readLine());
        if(s.equalsIgnoreCase("y"))
            avviaCal = true;

        if(avviaCal==true) {
            String text="";
            if(UtilCalendario.attivazioneServizio()==true) {
                Thread threadSoverglianza = new Thread(new ThreadSorveglianzaConImmagine(10));
                threadSoverglianza.start(); //faccio partire il thread per l interrogazione sottostante
            }
            else {
                text="Nessun evento in programma";
                Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
            }

        }
        //Thread threadSoverglianza = new Thread(new ThreadSorveglianzaSenzaImmagine(10));
        //threadSoverglianza.start();//faccio partire il thread per l interrogazione sottostante
        //text="Thread attivato";


        Gson gson = GsonFactory.getDefaultFactory().getGson();
        //Usato per catturare Post da API.ai
        post("/", (request, response) -> {
            Fulfillment output = new Fulfillment();
            doWebhook(gson.fromJson(request.body(), AIResponse.class), output);
            response.type("application/json");
            return output;
        }, gson::toJson);       //gson::toJson serve per prendere il contenuto della return e mapparlo in un oggetto json

    }

    /**
     * The webhook method. It is where the "magic" happens.
     * Please, notice that in this version we ignore the "urgent" field of tasks.
     *
     * @param input  the request body that comes from api.ai
     * @param output the @link(Fulfillment) response to be sent to api.ai
     */

    //tutte le action sono mappate dentro doWebhook
    private static void doWebhook(AIResponse input, Fulfillment output) throws InterruptedException, IOException {

        //In AIResponse input dobbiamo prendere un getResult e getAction, get action avrà lo stesso nome della action dell intent
        //text verrà utilizzato per il ritorno
        /*
        Holder della lampadina ha id=20             Accendi la luce da  API.ai            Accendi la luce da Telegram
         */

        String text="Device non trovato";

        if (input.getResult().getAction().equalsIgnoreCase("accendiLuce")) {
            DeviceList allDevice = getAllDevices();//aggiungo tutti i device dal metodo in util
            if(allDevice!=null) {
                Device devDaAccendere = getHolderLampadina();
                if(devDaAccendere!=null) {
                    devDaAccendere.on();
                    text="Luce accesa";
                }
            }

            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
            output.setSpeech(text);
            output.setDisplayText(text);
        }

        if (input.getResult().getAction().equalsIgnoreCase("spegniLuce")) {
            Device devDaSpegnere = getHolderLampadina();
            if(devDaSpegnere!=null) {
                devDaSpegnere.off();
                text="Luce spenta";
            }

            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
            output.setSpeech(text);
            output.setDisplayText(text);
        }

        if (input.getResult().getAction().equalsIgnoreCase("scattaFoto")) {
            Device sensoreLuminosita = getSensoreLuminosita();
            Device holderLampadina = getHolderLampadina();

            Webcam webcam = Webcam.getDefault();
            if (webcam != null && sensoreLuminosita!=null && holderLampadina!=null) {
                if( Double.parseDouble(sensoreLuminosita.getMetrics().getLevel()) <200)
                    holderLampadina.on();
                else
                    holderLampadina.off();

                webcam.open();
                try{
                    ImageIO.write(webcam.getImage(), "PNG", new File("Images/prova.png"));
                    webcam.close();
                    holderLampadina.off();
                    text = PATH_IMMAGINE_DROPBOX;
                    Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                    Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
                } catch (IOException e) {
                    text="Problema con la cam";
                    e.printStackTrace();
                    Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                    Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
                }
            }
            else {
                text = "Nessuna webcam rilevata";
                Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
            }
        }

        if (input.getResult().getAction().equalsIgnoreCase("accendiPresa")) {
            Device presaPilotata = getPresaPilotata();
            if(presaPilotata!=null) {
                presaPilotata.on();
                text = "Presa corrente accesa";
            }
            else text="Device accendi presa non trovato";

            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);

            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
            output.setSpeech(text);
            output.setDisplayText(text);
        }

        if (input.getResult().getAction().equalsIgnoreCase("spegniPresa")) {
            Device presaPilotata = getPresaPilotata();
            if(presaPilotata!=null) {
                presaPilotata.off();
                text = "Presa corrente spenta";
            }
            else text="Device spegni presa non trovato";

            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
            output.setSpeech(text);
            output.setDisplayText(text);
        }

        if (input.getResult().getAction().equalsIgnoreCase("verificaPresenza")) {
            //6	SensoreAmbientale_SafetyHome	General purpose  	Idle/trigered  	 	02:44 PM

            Device sensorePrenza = getSensorePresenza();
            if(sensorePrenza !=null) {
                if(sensorePrenza.getMetrics().getLevel().equalsIgnoreCase("on"))
                    text = "Rilevata presenza";
                else
                    text="Nessuna presenza rilevata";
            }
            else text="Sensore presenza non trovato";

            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);

            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
            output.setSpeech(text);
            output.setDisplayText(text);
        }

        if (input.getResult().getAction().equalsIgnoreCase("verificaPorta")) {
            Device sensoreAperturaPorta = getSensoreAperturaPorta();
            if(sensoreAperturaPorta !=null) {
                if(sensoreAperturaPorta.getMetrics().getLevel().equalsIgnoreCase("on"))
                    text = "Porta Aperta";
                else
                    text="Porta Chiusa";
            }
            else text="Sensore porta non trovato";

            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);

            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
            output.setSpeech(text);
            output.setDisplayText(text);
        }

        if (input.getResult().getAction().equalsIgnoreCase("attivaServizioMonitoraggioConImmagine")) {
            Thread t = new Thread(new ThreadSorveglianzaConImmagine(5));
            t.start();//faccio partire il thread per l interrogazione sottostante
        }

        // da adattare ai nuovi thread creati
        if (input.getResult().getAction().equalsIgnoreCase("attivaServizioMonitoraggioSenzaImmagine")) {
            Thread threadSoverglianza = new Thread(new ThreadSorveglianzaSenzaImmagine(5));
            threadSoverglianza.start();//faccio partire il thread per l interrogazione sottostante
        }

        if (input.getResult().getAction().equalsIgnoreCase("attivaMonitoraggioCalendarioConImmagine")) {
            if(UtilCalendario.attivazioneServizio()) {
                Thread threadSoverglianza = new Thread(new ThreadSorveglianzaConImmagine(10));
                threadSoverglianza.start();//faccio partire il thread per l interrogazione sottostante
            }
            else {
                text="Nessun evento in programma";
                Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
            }

            text="Thread attivato";
            output.setSpeech(text);
            output.setDisplayText(text);
        }

        if (input.getResult().getAction().equalsIgnoreCase("attivaMonitoraggioCalendarioSenzaImmagine")) {
            if(UtilCalendario.attivazioneServizio()) {
                Thread threadSoverglianza = new Thread(new ThreadSorveglianzaSenzaImmagine(2));
                threadSoverglianza.start();//faccio partire il thread per l interrogazione sottostante
            }
            else {
                text="Nessun evento in programma";
                Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
            }
            output.setSpeech(text);
            output.setDisplayText(text);
        }


        if (input.getResult().getAction().equalsIgnoreCase("attivaSimulaPresenza")) {
            Device presaPilotata = getPresaPilotata();
            Device holderLampadina = getHolderLampadina();

            if(holderLampadina!=null && presaPilotata!=null) {
                holderLampadina.on();
                presaPilotata.on();
                text="Simulazione presenza avviata";
            }

            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
        }

        if (input.getResult().getAction().equalsIgnoreCase("disattivaSimulaPresenza")) {
            Device presaPilotata = getPresaPilotata();
            Device holderLampadina = getHolderLampadina();

            if(holderLampadina!=null && presaPilotata!=null) {
                holderLampadina.off();
                presaPilotata.off();
                text="Simulazione presenza disattivata";
            }

            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
            output.setSpeech(text);
            output.setDisplayText(text);
        }

    }
}
