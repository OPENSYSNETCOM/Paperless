package com.opensysnet.paperless.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpUtils {

    public static final int MAX_HTTP_PARAM = 10;

    /* HTTP Method Type */
    public static final int GET         = 1;
    public static final int PUT         = 2;
    public static final int DELETE      = 3;
    public static final int POST        = 4;
    public static final int HEAD        = 5;

    /* HTTP Header Property */
    public static final String STR_CONTENT_TYPE = "Content-Type";
    public static final String STR_ACCEPT = "accept";
    public static final String STR_APPLICATION_JSON = "application/json";

    public static final int HTTP = 0;
    public static final int HTTPS = 1;

    /* Time out */
    public static final int CONNECTION_TIMEOUT = 2500;
    public static final int READ_TIMEOUT = 2500;

    private static Logger logger = LoggerFactory.getLogger( HttpUtils.class );

    private static void disableSSL( ) {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{ new OsnTrustManager( ) };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance( "SSL" );
            sc.init( null, trustAllCerts, new java.security.SecureRandom( ) );
            HostnameVerifier allHostsValid = new HostnameVerifier( ) {
                public boolean verify( String hostname, SSLSession session ) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier( allHostsValid );
            HttpsURLConnection.setDefaultSSLSocketFactory( sc.getSocketFactory( ) );
        } catch( Exception e ) {
            logger.error("HttpUtils.disableSSL : " + e.getMessage(), e);
        }
    }


    /**
     * @param type : http : 0, https : 1
     * @param ipAddr : Host
     * @param port : Port
     * @param path : Path
     * @param urlParam : Param
     * @return : URL
     */
    public String urlBuilder( int type, String ipAddr, String port, String path, HttpParam urlParam ) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();

        /** Example : https://IP:Port/test/123?abc=xyz */
        /* Set Scheme */
        if( type == HTTPS ) {
            uriBuilder.scheme( "https" );
        } else {
            uriBuilder.scheme( "http" );
        }
        /* Set Host */
        uriBuilder.host( ipAddr );
        /* Set Port */
        uriBuilder.port( Integer.valueOf( port ) );
        /* Set Path */
        uriBuilder.path( path );

        if( urlParam != null ) {
            while( true ) {
                String parameter[];
                if( ( parameter = urlParam.get( ) ) != null ) {
                    uriBuilder.queryParam( parameter[ 0 ], parameter[ 1 ] );
                    continue;
                }
                break;
            }
        }
        logger.debug( "Server_URL : [ {} ] ", uriBuilder.toUriString() );
        return uriBuilder.toUriString();
    }

    private HttpURLConnection headerProperty( HttpURLConnection conn, HttpParam headerParam ) {
        if( headerParam != null ) {
            while( true ) {
                String property[];
                if( ( property = headerParam.get( ) ) != null ) {
                    logger.debug( "Header Property = [ {} ] [ {} ]", property[ 0 ], property[ 1 ] );
                    conn.setRequestProperty( property[ 0 ], property[ 1 ] );
                    continue;
                }
                break;
            }
        }
        return conn;
    }

    private int isHttps(String serverUrl ) {
        if( serverUrl.indexOf( "https" ) == 0 ) {
            return 0;
        }
        return -1;
    }

    private HttpURLConnection getConnection( int methodType, String SERVER_URL, HttpParam headerParam, String strMsg, byte[] byteMsg ) {
        URL url = null;
        HttpURLConnection urlConn = null;
        DataOutputStream dataOutputStream = null;

        try {
            /* For Unknow Host Name Exception. Private SSL */
            if( isHttps( SERVER_URL ) == 0 ) disableSSL( );

            url = new URL( SERVER_URL );

            /* Open Connection */
            urlConn = (HttpURLConnection) url.openConnection( );

            /* Set Request Method */
            switch( methodType ) {
                case HttpUtils.GET:
                    urlConn.setRequestMethod( "GET" );
                    break;
                case HttpUtils.PUT:
                    urlConn.setRequestMethod( "PUT" );
                    break;
                case HttpUtils.DELETE :
                    urlConn.setRequestMethod( "DELETE" );
                    break;
                case HttpUtils.POST :
                    urlConn.setRequestMethod( "POST" );
                    break;
                case HttpUtils.HEAD :
                    urlConn.setRequestMethod( "HEAD" );
                    break;
            }
            /* no user interaction */
            urlConn.setAllowUserInteraction( false );
            /* Want To Send Body Data : Get, Head은 필요치 않음 */
            if( methodType == HttpUtils.GET || methodType == HttpUtils.HEAD ) {
                urlConn.setDoOutput( false );
            } else {
                urlConn.setDoOutput( true );
            }
            urlConn.setConnectTimeout(HttpUtils.CONNECTION_TIMEOUT);
            urlConn.setReadTimeout(HttpUtils.READ_TIMEOUT);

            /* Set Header */
            headerProperty( urlConn, headerParam );

            /* Write Msg */
            if( strMsg != null ) {
                dataOutputStream = new DataOutputStream( urlConn.getOutputStream( ) );
                dataOutputStream.writeBytes( strMsg );
                dataOutputStream.flush( );
            } else if ( byteMsg != null ) {
                dataOutputStream = new DataOutputStream( urlConn.getOutputStream( ) );
                dataOutputStream.write( byteMsg );
                dataOutputStream.flush( );
            }

            return urlConn;

        } catch( MalformedURLException e ) {
            e.printStackTrace( );
        } catch( ProtocolException e ) {
            e.printStackTrace( );
        } catch( IOException e ) {
            e.printStackTrace( );
        }
        return null;
    }

    public HttpURLConnection doGet(String SERVER_URL, HttpParam headerParam ) {
        return getConnection( HttpUtils.GET, SERVER_URL, headerParam, null, null );
    }

    public HttpURLConnection doHead(String SERVER_URL, HttpParam headerParam ) {
        return getConnection( HttpUtils.HEAD, SERVER_URL, headerParam, null, null );
    }

    public HttpURLConnection doDelete(String SERVER_URL, HttpParam headerParam, String bodyMsg ) {
        return getConnection( HttpUtils.DELETE, SERVER_URL, headerParam, bodyMsg, null );
    }

    public HttpURLConnection doPut( String SERVER_URL, HttpParam headerParam, String bodyMsg ) {
        return getConnection( HttpUtils.PUT, SERVER_URL, headerParam, bodyMsg, null );
    }

    public HttpURLConnection doPost( String SERVER_URL, HttpParam headerParam, String bodyMsg ) {
        return getConnection( HttpUtils.POST, SERVER_URL, headerParam, bodyMsg, null );
    }

    public HttpURLConnection doFileUpload( String SERVER_URL, HttpParam headerParam, byte[] bodyMsg ) {
        return getConnection( HttpUtils.POST, SERVER_URL, headerParam, null, bodyMsg );
    }

    public String getErrorMsg( HttpURLConnection conn ) {
        InputStream inputStream = null;
        StringBuffer stringBuffer = null;

        try {
            inputStream = conn.getErrorStream( );
            InputStreamReader inputStreamReader = new InputStreamReader( inputStream );
            BufferedReader bufferedReader = new BufferedReader( inputStreamReader );
            stringBuffer = new StringBuffer(  );

            String nextLine = bufferedReader.readLine( );

            while( nextLine != null ) {
                stringBuffer.append( nextLine );
                nextLine = bufferedReader.readLine( );
            }
        } catch( IOException e ) {
            logger.error("HttpUtils.getResponseMessage : " + e.getMessage(), e);
        } finally {
            try {
                if( inputStream != null )
                    inputStream.close();
            } catch( IOException e ) {
                e.printStackTrace( );
            }
        }

        if ( stringBuffer == null ) return null;

        return stringBuffer.toString();

    }

    public String getResponseMessage( HttpURLConnection conn ) {
        InputStream inputStream = null;
        StringBuffer stringBuffer = null;

        try {
            inputStream = conn.getInputStream( );
            InputStreamReader inputStreamReader = new InputStreamReader( inputStream );
            BufferedReader bufferedReader = new BufferedReader( inputStreamReader );
            stringBuffer = new StringBuffer(  );

            String nextLine = bufferedReader.readLine( );

            while( nextLine != null ) {
                stringBuffer.append( nextLine );
                nextLine = bufferedReader.readLine( );
            }
        } catch( IOException e ) {
            logger.error("HttpUtils.getResponseMessage : " + e.getMessage(), e);
        } finally {
            try {
                if( inputStream != null )
                    inputStream.close();
            } catch( IOException e ) {
                e.printStackTrace( );
            }
        }

        if ( stringBuffer == null ) return null;

        return stringBuffer.toString();
    }
}
