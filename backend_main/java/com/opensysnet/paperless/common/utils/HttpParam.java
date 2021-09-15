package com.opensysnet.paperless.common.utils;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpParam {

    private AtomicInteger index = new AtomicInteger( 0 );
    private String type[] = new String[ HttpUtils.MAX_HTTP_PARAM ];
    private String value[] = new String[ HttpUtils.MAX_HTTP_PARAM ];

    public int add( String type, String value ) {
        if( index.get( ) < HttpUtils.MAX_HTTP_PARAM ) {
            /* 데이터를 넣기 위해 이동 */
            this.type[ index.get( ) ] = type;
            this.value[ index.get( ) ] = value;
            return index.getAndIncrement( );
        }
        return -1;
    }

    public String[] get( ) {
        if ( index.decrementAndGet() < 0 ) {
            return null;
        }
        String data[] = new String[ 2 ];
        data[ 0 ] = this.type[ index.get( ) ];
        data[ 1 ] = this.value[ index.get( ) ];
        return data;
    }

    @Override
    public String toString( ) {
        return "HttpParam{" +
                "index=" + index.get( ) +
                ", type=" + Arrays.toString( type ) +
                ", value=" + Arrays.toString( value ) +
                '}';
    }

    public static HttpParam getDefaultJsonHeader() {
        HttpParam headerParam = new HttpParam( );

        headerParam.add( HttpUtils.STR_CONTENT_TYPE, HttpUtils.STR_APPLICATION_JSON );
        headerParam.add( HttpUtils.STR_ACCEPT, HttpUtils.STR_APPLICATION_JSON );

        return headerParam;
    }
}
