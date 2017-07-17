package it.upo.reti2s.utils;

import de.fh_zwickau.informatik.sensor.model.devices.Device;
import de.fh_zwickau.informatik.sensor.model.devices.DeviceList;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import static it.upo.reti2s.utils.SafetyHomeWebhook.*;

/**
 * Created by Luca Franciscone on 12/07/2017.
 */
public class Util
{
    //METODI PER INVIARE MESSAGGI CON TELEGRAM
    //Metodo usato per inviare messaggio a telegram senza webhook mediante post
    /*
     message è il messaggio in stringa da inviare
     /chatid è l id dell utente a cui rispondere nella chat
          */
    public static void sendMessage(String message, long aChatId, String telegram_url) throws SecurityException, IOException
    {
        String response = "";
        String responseJSON = "";
        String converted = "";
        try
        {
            converted = convertToUtf(message);
            responseJSON = "{ \"text\" : \"" + converted + "\", \"chat_id\" : " + aChatId+ " }";
            response = eseguiPost(telegram_url + "/sendMessage", responseJSON);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String convertToUtf(String s)
    {
        String[] INVALID_UTF8 = {"à", "è", "é", "ì", "ò", "ù", "À", "È", "É", "Ì", "Ò", "Ù"};
        String[] VALIDATED_UTF8 = {"a'", "e'", "e'", "i'", "o'", "u'", "A'", "E'", "E'", "I'", "O'", "U'"};

        for(int i=0; i<INVALID_UTF8.length; i++)
            s = s.replace(INVALID_UTF8[i], VALIDATED_UTF8[i]);

        byte[] bytes = s.getBytes( Charset.forName("UTF-16" ));
        String ret = new String( bytes, Charset.forName("UTF-16") );
        return ret;
    }


    //HTTP POST PARTE CHIAMATA PER L INVIO DEL MESSAGGIO
    public static String eseguiPost(String url, String json) throws Exception
    {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(json);
        wr.flush();
        wr.close();
        return writeResp(con);
    }

    private static String writeResp(HttpsURLConnection con) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();
        return response.toString();
    }


    //INIZIO METODI PER I DEVICE


    public static DeviceList getAllDevices()
    {
        DeviceList lista = zWayApi.getDevices();
        if(lista!=null)
        {
            return lista;
        }
        return null;
    }


    public static Device getDevice(String deviceType, int id)
    {
        DeviceList lista = getAllDevices();
        if(lista!=null)
        {
            for(Device tmp : lista.getAllDevices())
            {
                if(tmp.getDeviceType().equalsIgnoreCase(deviceType) && tmp.getNodeId()== id)
                {
                    return tmp;
                }
            }
        }
        else
        {
            return null;
        }
        return null;
    }


    public static  Device getSensorePresenza() {
        DeviceList lista = zWayApi.getDevices();
        if (lista != null) {
            for (Device tmp : lista.getAllDevices()) {
                if (tmp.getNodeId() == ID_MULTILEVEL_PURPOSE &&
                        tmp.getDeviceType().equalsIgnoreCase(SENSORBINARY) &&
                        tmp.getMetrics().getProbeTitle().contains(MULTILEVEL_PURPOSE)) {
                    return tmp;
                }
            }
        }
        return null;
    }

    public static Device getSensoreAperturaPorta()
    {
        DeviceList lista = zWayApi.getDevices();
        if(lista!=null) {
            for (Device tmp : lista.getAllDevices())
            {
                if (tmp.getNodeId() == ID_APERTURAPORTE && tmp.getDeviceType().equalsIgnoreCase(SENSORBINARY))
                {
                    return tmp;
                }
            }
        }
        return null;
    }

    public static String getPortaAperta(Device aperturaPorta)
    {
        return aperturaPorta.getMetrics().getLevel();
    }


    public static Device getSensoreLuminosita()
    {
        DeviceList lista = getAllDevices();
        if(lista!=null)
        {
            for(Device tmp : lista.getAllDevices())
            {
                if(tmp.getNodeId() == ID_MULTILEVEL_PURPOSE &&
                        tmp.getMetrics().getProbeTitle().contains(MULTILEVEL_LUMINESCENCE))
                {
                    return tmp;
                }
            }
        }
        return null;
    }

    //manca il metodo per acendere la spina

    public static Device getPresaPilotata()
    {
        return getDevice(SWITCHBINARY,ID_PRESAPILOTATA);
    }
}
