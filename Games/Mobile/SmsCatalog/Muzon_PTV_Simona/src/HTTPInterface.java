//#excludeif !RSRC_HTTP
import javax.microedition.io.HttpConnection;
import javax.microedition.io.Connector;
import java.io.*;

//#local has_get = true
//#local has_post = false

public class HTTPInterface
{
    private static final int ATTEMPION_NUMBER = 5;

    public static int i_LastServerAnswerCode;
    public static String s_ConnectionException;

    private static final int INDEX_PROCESSED = 0;
    private static final int INDEX_RESULT = 1;

    private static final int FLAG_OK = 0;
    private static final int FLAG_CONNECTIONERROR = 1;
    private static final int FLAG_SERVERERROR = 2;
    private static final int FLAG_DATAERROR = 3;

    private static final int FLAG_WORKING = 0;
    private static final int FLAG_STOPED = 1;


    //#if has_get
    public final static byte[] processGETRequestToServer(final String _url) throws IOException
    {
        final ByteArrayOutputStream p_answerBuffer = new ByteArrayOutputStream(2048);
        final int [] ai_result = new int[2];

        ai_result[INDEX_PROCESSED] = FLAG_WORKING;
        ai_result[INDEX_RESULT] = FLAG_OK;

        new Thread(new Runnable()
        {
            public final void run()
            {
                HttpConnection p_httpConnection = null;

                i_LastServerAnswerCode = -1;
                s_ConnectionException = null;

                for (int li = 0; li < ATTEMPION_NUMBER; li++)
                {
                    try
                    {
                        p_httpConnection = (HttpConnection) Connector.open(_url,Connector.READ_WRITE);
                    }
                    catch (Throwable e)
                    {
                        //#-
                        e.printStackTrace();
                        //#+
                        p_httpConnection = null;
                        s_ConnectionException = e.getMessage();
                        if (s_ConnectionException == null || s_ConnectionException.length() == 0) s_ConnectionException = e.getClass().getName();
                    }
                    if (p_httpConnection != null) break;
                }


                if (p_httpConnection == null)
                {
                    ai_result[INDEX_RESULT] = FLAG_CONNECTIONERROR;
                    ai_result[INDEX_PROCESSED] = FLAG_STOPED;
                    return;
                }
                s_ConnectionException = null;

                DataOutputStream p_dos = null;
                DataInputStream p_dis = null;

                try
                {
                    p_httpConnection.setRequestMethod(HttpConnection.GET);

                    p_httpConnection.setRequestProperty("Cache-Control", "no-cache");
                    p_httpConnection.setRequestProperty("Connection", "close");

                    int i_responseCode = p_httpConnection.getResponseCode();

                    i_LastServerAnswerCode = i_responseCode;

                    if (i_responseCode != HttpConnection.HTTP_OK)
                    {
                        ai_result[INDEX_RESULT] = FLAG_SERVERERROR;
                        return;
                    }
                    p_dis = p_httpConnection.openDataInputStream();

                    int i_responseLen = (int) p_httpConnection.getLength();
                    if (i_responseLen <= 0)
                    {
                            while(true)
                            {
                                int i_val = p_dis.read();
                                if (i_val<0) break;
                                p_answerBuffer.write(i_val);
                            }
                    }
                    else
                    {
                        while(i_responseLen>0)
                        {
                            int i_val = p_dis.read();
                            if (i_val<0) break;
                            p_answerBuffer.write(i_val);
                            i_responseLen--;
                        }
                        if (i_responseLen>0) throw new Exception("Cutted data");
                    }
                }
                catch (Throwable _ex)
                {
                    //#-
                    _ex.printStackTrace();
                    //#+
                    s_ConnectionException = _ex.getMessage();
                    if (s_ConnectionException==null || s_ConnectionException.length()==0) s_ConnectionException = _ex.getClass().getName();
                    ai_result[INDEX_RESULT] = FLAG_DATAERROR;
                }
                finally
                {
                    try
                    {
                        if (p_dis != null) p_dis.close();
                    }
                    catch (Throwable _eex)
                    {
                        p_dis = null;
                    }
                    try
                    {
                        if (p_dos != null) p_dos.close();
                    }
                    catch (Throwable _eex)
                    {
                        p_dos = null;
                    }
                    try
                    {
                        if (p_httpConnection != null) p_httpConnection.close();
                    }
                    catch (Throwable _eex)
                    {
                        p_httpConnection = null;
                    }
                    p_dis = null;
                    p_dos = null;
                    p_httpConnection = null;

                    try
                    {
                        Thread.sleep(500);
                    }
                    catch (Exception e){}

                    ai_result[INDEX_PROCESSED] = FLAG_STOPED;
                }
            }
        }).start();

        while(ai_result[INDEX_PROCESSED]==FLAG_WORKING)
        {
            try
            {
                Thread.sleep(50);
            }
            catch (Exception e)
            {
                return null;
            }
        }

        Runtime.getRuntime().gc();

        switch(ai_result[INDEX_RESULT])
        {
           case FLAG_OK : return p_answerBuffer.toByteArray();
           default: throw new IOException();
        }
    }
    //#endif

    //#if has_post
    public static final synchronized byte[] processPOSTRequestToServer(final String _url,final byte[] _postBody,final String _contentType) throws IOException
    {
        //#if SHOWSYS
        System.out.println("POST BODY: "+new String(_postBody));
        //#endif

        final ByteArrayOutputStream p_Buffer = new ByteArrayOutputStream(1024);
        final int [] ai_result = new int[2];

        ai_result[INDEX_PROCESSED] = FLAG_WORKING;
        ai_result[INDEX_RESULT] = FLAG_OK;

        new Thread(new Runnable()
        {
            public final void run()
            {
                HttpConnection p_httpConnection = null;

                i_LastServerAnswerCode = -1;
                s_ConnectionException = null;

                for (int li = 0; li < ATTEMPION_NUMBER; li++)
                {
                    try
                    {
                        p_httpConnection = (HttpConnection) Connector.open(_url,Connector.READ_WRITE);
                    }
                    catch (Throwable e)
                    {
                        //#-
                        e.printStackTrace();
                        //#+
                        p_httpConnection = null;
                        s_ConnectionException = e.getMessage();
                        if (s_ConnectionException == null || s_ConnectionException.length() == 0) s_ConnectionException = e.getClass().getName();
                    }
                    if (p_httpConnection != null) break;
                }

                if (p_httpConnection == null)
                {
                    ai_result[INDEX_RESULT] = FLAG_CONNECTIONERROR;
                    ai_result[INDEX_PROCESSED] = FLAG_STOPED;
                    return;
                }
                s_ConnectionException = null;

                DataOutputStream p_dos = null;
                DataInputStream p_dis = null;

                try
                {
                    int i_len = _postBody.length;

                    p_httpConnection.setRequestMethod(HttpConnection.POST);

                    p_httpConnection.setRequestProperty("Content-Type", _contentType);
                    p_httpConnection.setRequestProperty("Content-Length", Integer.toString(i_len));
                    p_httpConnection.setRequestProperty("Cache-Control", "no-cache");
                    p_httpConnection.setRequestProperty("Connection", "close");

                    p_dos = p_httpConnection.openDataOutputStream();

                    p_dos.write(_postBody,0,_postBody.length);
                    //p_dos.flush();

                    int i_responseCode = p_httpConnection.getResponseCode();

                    i_LastServerAnswerCode = i_responseCode;

                    if (i_responseCode != HttpConnection.HTTP_OK)
                    {
                        ai_result[INDEX_RESULT] = FLAG_SERVERERROR;
                        return;
                    }
                    p_dis = p_httpConnection.openDataInputStream();

                    int i_responseLen = (int) p_httpConnection.getLength();
                    if (i_responseLen <= 0)
                    {
                        while (true)
                        {
                            int i_value = p_dis.read();
                            if (i_value<0) break;
                            p_Buffer.write(i_value);
                        }
                    }
                    else
                    {
                        while(i_responseLen>0)
                        {
                            int i_val = p_dis.read();
                            if (i_val<0) break;
                            p_Buffer.write(i_val);
                            i_responseLen--;
                        }
                        if (i_responseLen>0) throw new Exception("Cutted data");
                      }
                }
                catch (Throwable _ex)
                {
                    s_ConnectionException = _ex.getMessage();
                    if (s_ConnectionException==null || s_ConnectionException.length()==0) s_ConnectionException = _ex.getClass().getName();
                    ai_result[INDEX_RESULT] = FLAG_DATAERROR;
                }
                finally
                {
                    try
                    {
                        if (p_dis != null) p_dis.close();
                    }
                    catch (Throwable _eex)
                    {
                        p_dis = null;
                    }
                    try
                    {
                        if (p_dos != null) p_dos.close();
                    }
                    catch (Throwable _eex)
                    {
                        p_dos = null;
                    }
                    try
                    {
                        if (p_httpConnection != null) p_httpConnection.close();
                    }
                    catch (Throwable _eex)
                    {
                        p_httpConnection = null;
                    }
                    p_dis = null;
                    p_dos = null;
                    p_httpConnection = null;

                    try
                    {
                        Thread.sleep(500);
                    }
                    catch (Exception e){}

                    ai_result[INDEX_PROCESSED] = FLAG_STOPED;
                }
            }
        }).start();

        while(ai_result[INDEX_PROCESSED]==FLAG_WORKING)
        {
            try
            {
                Thread.sleep(50);
            }
            catch (Exception e)
            {
                return null;
            }
        }

        Runtime.getRuntime().gc();

        //#if SHOWSYS
        if (ai_result[INDEX_RESULT]==FLAG_OK) System.out.println("ANSWER BODY: "+new String(p_Buffer.toByteArray()));
        //#endif

        switch(ai_result[INDEX_RESULT])
        {
           case FLAG_OK : return p_Buffer.toByteArray();
           default: throw new IOException();
        }
    }
    //#endif

}