//#excludeif !SMSENGINE
package ru.coldcore;

public class Wma extends SMSSender
{
    public boolean supportsSMS()
    {
        return true;
    }

    public boolean sendTextSMS(String _smsBody, String _phoneNumber)
    {
        try
        {
            javax.wireless.messaging.MessageConnection p_connection = (javax.wireless.messaging.MessageConnection) javax.microedition.io.Connector.open("sms://" + _phoneNumber);
            javax.wireless.messaging.Message p_txtMessage = p_connection.newMessage(javax.wireless.messaging.MessageConnection.TEXT_MESSAGE, "sms://" + _phoneNumber);
            ((javax.wireless.messaging.TextMessage) p_txtMessage).setPayloadText(_smsBody);
            p_connection.send(p_txtMessage);
            p_connection.close();
            p_txtMessage = null;
            p_connection = null;
        }
        catch (Throwable _ex)
        {
            //#-
            _ex.printStackTrace();
            //#+
            return false;
        }
        return true;
    }
}
