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

import static it.upo.reti2s.utils.SFWebhook.*;

/**
 * Created by Luca Franciscone on 12/07/2017.
 */
public class Util
{

    //PRENDERE PER LA CONVERSIONE
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
            //convert message into utf-8
            converted = convertToUtf(message);
            responseJSON = "{ \"text\" : \"" + converted + "\", \"chat_id\" : " + aChatId+ " }";
            response = eseguiPost(telegram_url + "/sendMessage", responseJSON);
        }
        catch(Exception e)
        {
            //e.printStackTrace();
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


    public static DeviceList getAllDevices()
    {
        return zWayApi.getDevices();
    }

    public static Device getDevice(String deviceType, int id)
    {
        DeviceList lista = getAllDevices();

        for(Device tmp : lista.getAllDevices())
        {
            if(tmp.getDeviceType().equalsIgnoreCase(deviceType) && tmp.getNodeId()== id)
            {
                return tmp;
            }
        }
        return null;

    }

    //probtype può essere temperatura luminosita ecc
    public static  Device getDevice(String deviceType,String probeTitle, int id)
    {
        DeviceList lista = getAllDevices();

        for(Device tmp : lista.getAllDevices())
        {
            if(tmp.getDeviceType().equalsIgnoreCase(deviceType) && tmp.getNodeId()== id && tmp.getMetrics().getProbeTitle().contains(probeTitle))
            {
                return tmp;
            }
        }
        return null;
    }

    public static  Device getSensorePresenza()
    {
        return getDevice(SENSORBINARY,MULTILEVEL_PURPOSE,MULTILEVEL_ID);
    }

    public static Device getSensoreAperturaPorta()
    {
        return getDevice(SENSORBINARY,APERTURA_PORTE_ID);

    }

    public static Device getHolederLampadina()
    {
        return getDevice(SWITCHBINARY,HOLDER_LAMPADINA);
    }

    public static Device getSensoreLuminosita()
    {
        return getDevice(SENSORMULTILEVEL,MULTILEVEL_LUMINESCENCE,MULTILEVEL_ID);
    }


    public static void accendiDevice(Device device)
    {
        device.on();
    }

    public static void spegniDevice(Device device)
    {
        device.off();
    }

}
