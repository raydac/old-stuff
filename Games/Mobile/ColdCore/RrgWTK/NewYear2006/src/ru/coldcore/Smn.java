//#excludeif !SMSENGINE
package ru.coldcore;

public class Smn extends SMSSender
{
    public boolean sendTextSMS(String _smsBody,String _phoneNumber)
    {
        try
        {
                   //$com.siemens.mp.gsm.SMS.send(_phoneNumber,_smsBody);
        }catch(Throwable _ex)
        {
            return false;
        }
        return true;
    }

    public boolean supportsSMS()
    {
        return true;
    }
}
